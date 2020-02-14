package uk.galexite.guildevents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        /**
         * A reference to the online Firebase database containing the scraped events.
         */
        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase.child("events").addChildEventListener(mEventChildEventListener);

        // Set up the recycler view with our database.
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        final EventListAdapter adapter = new EventListAdapter(this, mTwoPane);
        recyclerView.setAdapter(adapter);

        // Set up our view model.
        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        mEventViewModel.getAllEventsFromNow().observe(this, adapter::setEvents);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_list, menu);
        return true;
    }

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
                    Bundle arguments = new Bundle();
                    arguments.putInt(EventDetailFragment.ARG_ITEM_ID, event.getId());
                    EventDetailFragment fragment = new EventDetailFragment();
                    fragment.setArguments(arguments);
                    fragment.setHasOptionsMenu(true);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
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
