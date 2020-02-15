package uk.galexite.guildevents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.entity.Organisation;
import uk.galexite.guildevents.data.viewmodel.EventViewModel;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EventListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    /**
     * The view model.
     */
    private EventViewModel mEventViewModel;

    /**
     * The Firebase child event listener, listens for updates to the Event list on the database.
     */
    private final ChildEventListener mEventChildEventListener = new ChildEventListener() {

        // TODO: Room <=> Firebase

        private void insertEvent(DataSnapshot dataSnapshot) {
            Event event = dataSnapshot.getValue(Event.class);
            if (dataSnapshot.getKey() != null)
                Objects.requireNonNull(event).setId(Integer.valueOf(dataSnapshot.getKey()));
            mEventViewModel.insertEvent(event);
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            insertEvent(dataSnapshot);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            insertEvent(dataSnapshot); // as we've set the database to replace upon conflict,
            // just insert the new event again.
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };
    /**
     * The adapter for the RecyclerView.
     */
    private EventListAdapter mEventListAdapter;
    /**
     * The current Organiser id for filtering the Event list.
     */
    private Integer mOrganiserId;
    /**
     * The 'filter by organisation' spinner's OnItemSelectedListener. Sets the filter for the event
     * list when a specific organisation is selected to filter by.
     */
    private final AdapterView.OnItemSelectedListener onOrganisationFilterSelectedListener
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (id == 0) { // 'All organisations' was selected
                // There should not be filter applied to the list of events, so clear it.
                updateRecyclerView(null);
                return;
            }

            // The user selected an organisation, so filter the Events list for events which are
            // organised by this Organisation they have selected.
            Organisation organisation = (Organisation) parent.getAdapter().getItem(position);
            updateRecyclerView(organisation.getId());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Don't do anything, the user may have clicked off the pop-up, so probably don't want
            // to change the filter.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // A reference to the online Firebase database containing the scraped events.
        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase.child("events").addChildEventListener(mEventChildEventListener);

        // Set up our view model, which mediates between the Activity and the database code.
        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        // Populate the filter 'spinner' with the list of organisations in the database.
        Spinner spinner = findViewById(R.id.spinner);
        setupFilterByOrganisationSpinner(spinner);

        // Set up the recycler view with our database.
        RecyclerView recyclerView = findViewById(R.id.item_list);
        setupRecyclerView(recyclerView);
    }

    /**
     * Set up the spinner allowing users to select specific organisations which they are interested
     * in Events by.
     *
     * @param spinner the Spinner view to populate with Organisation objects
     */
    private void setupFilterByOrganisationSpinner(@NonNull Spinner spinner) {
        // This ArrayAdapter will erase the types to object, such that you can insert any arbitrary
        // type which has a toString() method. Helpful to add the 'All organisations' entry.
        final ArrayAdapter<Object> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        mEventViewModel.getAllOrganisations().observe(this, collection -> {
            adapter.clear();
            adapter.add("All organisers");
            adapter.addAll(collection);
        });
    }

    /**
     * Set up the given RecyclerView to list all the events starting from now in the database.
     *
     * @param recyclerView the RecyclerView to set up with the Events from the database
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mEventListAdapter = new EventListAdapter(this, mTwoPane);
        recyclerView.setAdapter(mEventListAdapter);
        updateRecyclerView(null);
    }

    /**
     * Update the RecyclerView with events filtered by an organisation.
     *
     * @param organisationFilter the id of the Organisation to filter by, or null if there shall
     *                           be no filter applied
     */
    private void updateRecyclerView(Integer organisationFilter) {
        // Remove any existing observers to stop them from adding Events to the RecyclerView.
        if (mOrganiserId == null) {
            mEventViewModel.getAllEventsFromNow().removeObservers(this);
        } else {
            mEventViewModel.getAllEventsOrganisedBy(mOrganiserId).removeObservers(this);
        }

        // Apply the new Organisation filter.
        mOrganiserId = organisationFilter;
        LiveData<List<Event>> liveData = mOrganiserId == null
                ? mEventViewModel.getAllEventsFromNow()
                : mEventViewModel.getAllEventsOrganisedBy(mOrganiserId);
        liveData.observe(this, mEventListAdapter::setEvents);
    }

    /**
     * The RecyclerView adapter that displays all the events (possibly filtered by the spinner) for
     * the user to browse through.
     *
     * The adapter is passed lists of {@link Event} objects to display with setEvent(List<Event>).
     */
    public static class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

        private final SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("EEEE, d MMMM, h:mm aa", Locale.ENGLISH);

        private final EventListActivity mParentActivity;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event event = (Event) view.getTag();
                if (mTwoPane) {
                    // Build the EventFragment in to this Activity so the list of Events and the
                    // details (provided by this Fragment) can be displayed side by side.
                    Bundle arguments = new Bundle();
                    arguments.putInt(EventDetailFragment.ARG_ITEM_ID, event.getId());
                    EventDetailFragment fragment = new EventDetailFragment();
                    fragment.setArguments(arguments);
                    fragment.setHasOptionsMenu(true);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    // Open the EventDetailActivity on devices which are not large enough to show
                    // the list and the detail side by side.
                    Context context = view.getContext();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(EventDetailFragment.ARG_ITEM_ID, event.getId());

                    context.startActivity(intent);
                }
            }
        };

        private List<Event> mEvents;

        EventListAdapter(EventListActivity parent,
                         boolean twoPane) {
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Event event = mEvents.get(position);

            holder.mEventOrganiserName.setText(event.getOrganiserName());
            holder.mEventName.setText(event.getName());
            holder.mEventFromDate.setText(
                    simpleDateFormat.format(Timestamp.valueOf(event.getFromDate()))
            );

            holder.itemView.setTag(event);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Set the list of events to display in the connected RecyclerView. Will cause the view to
         * refresh to show this new information using notifyDataSetChanged().
         * @param events the list of events
         */
        void setEvents(List<Event> events) {
            mEvents = events;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mEvents == null ? 0 : mEvents.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mEventOrganiserName;
            final TextView mEventName;
            final TextView mEventFromDate;

            ViewHolder(View view) {
                super(view);
                mEventOrganiserName = view.findViewById(R.id.event_organiser_name);
                mEventName = view.findViewById(R.id.event_name);
                mEventFromDate = view.findViewById(R.id.event_from_date);
            }
        }
    }
}
