package uk.galexite.guildevents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.viewmodel.EventViewModel;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 *
 * Suppressing 'Weaker Access' because Fragments must be public.
 */
@SuppressWarnings("WeakerAccess")
public class EventDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * The view model.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private EventViewModel mEventViewModel;
    /**
     * The event this Fragment is detailing.
     */
    private Event mEvent;
    /**
     * The organiser's name text view for the event.
     */
    private TextView mOrganiserView;
    /**
     * The name text view for the event.
     */
    private TextView mNameView;
    /**
     * The description text view for the event.
     */
    private TextView mDetailView;
    /**
     * The card view showing the event's location.
     */
    private CardView mLocationCardView;
    /**
     * Location for the event.
     */
    private TextView mLocationView;
    /**
     * Displays the date and time when the event is running.
     */
    private TextView mDateRangeView;

    private final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("EEEE, d MMMM YYYY 'at' h:mm aa", Locale.ENGLISH);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        assert getArguments() != null;

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            int id = getArguments().getInt(ARG_ITEM_ID);
            mEventViewModel.getEvent(id).observe(this, this::setEvent);
        }
    }

    /**
     * Set the Event that this Fragment is detailing.
     *
     * @param event to display
     */
    private void setEvent(@NonNull Event event) {
        mEvent = event;

        if (mOrganiserView != null) {
            mOrganiserView.setText(mEvent.getOrganiserName());
        }

        if (mNameView != null) {
            mNameView.setText(mEvent.getName());
        }

        if (mDetailView != null) {
            mDetailView.setText(mEvent.getDescription());
        }

        if (mEvent.getLocation() == null) {
            // If the location is null, there is no point in showing the location card!
            mLocationCardView.setVisibility(View.INVISIBLE);
        } else {
            if (mLocationView != null) {
                mLocationCardView.setVisibility(View.VISIBLE);
                mLocationView.setText(mEvent.getLocation());
            }
        }

        if (mDateRangeView != null) {
            mDateRangeView.setText(simpleDateFormat.format(Timestamp.valueOf(mEvent.getFromDate())));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.open_in_browser) { // Open this event in the browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(getHttpUri());

            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.share) { // Share
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this event: " + getHttpUri());
            intent.setType("text/plain");

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get an HTTP Uri to the event to view in a web browser or share.
     *
     * @return a Uri with an HTTP scheme for viewing the event on the Guild's website.
     */
    private Uri getHttpUri() {
        Uri uri = Uri.parse(mEvent.getUrl());

        /* Sometimes, an event may link to an external source. We need to check and make sure
           if the scheme is not already present before we go and add the Guild's domain name to
           the URI. */
        if (uri.getScheme() == null || uri.getScheme().isEmpty()) {
            uri = uri.buildUpon()
                    .scheme("http")
                    .authority("www.exeterguild.org")
                    .build();
        }

        return uri;
    }

    /**
     * Find the event's location on a map by creating an explicit intent to launch Google Maps to
     * search for the location near Exeter.
     *
     * @param view the button clicked upon to find the event on the map
     */
    private void onFindOnMapClick(View view) {
        // These geo coordinates are for Exeter; events are most likely to be held near here.
        Uri intentUri = Uri.parse("geo:50.716667,-3.533333?q="
                + mEvent.getLocation().replace(' ', '+'));
        final Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);

        intent.setPackage("com.google.android.apps.maps");

        startActivity(intent);
    }

    /**
     * Open the event in the user's calendar.
     * @param view the button clicked upon
     */
    private void onAddToCalendarClick(View view) {
        long startDate = Timestamp.valueOf(mEvent.getFromDate()).getTime();

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate)
                .putExtra(CalendarContract.Events.TITLE, mEvent.getName())
                .putExtra(CalendarContract.Events.DESCRIPTION, mEvent.getDescription())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, mEvent.getLocation())
                .putExtra(CalendarContract.Events.AVAILABILITY,
                        CalendarContract.Events.AVAILABILITY_BUSY);

        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail, container, false);

        mOrganiserView = rootView.findViewById(R.id.event_organiser_name);
        mNameView = rootView.findViewById(R.id.event_name);
        mDetailView = rootView.findViewById(R.id.event_description);
        mDateRangeView = rootView.findViewById(R.id.date_range);
        mLocationCardView = rootView.findViewById(R.id.location_card);
        mLocationView = rootView.findViewById(R.id.location);

        // Associate the buttons with their appropriate onClick handlers

        Button findOnMapButton = rootView.findViewById(R.id.find_on_map_button);
        findOnMapButton.setOnClickListener(this::onFindOnMapClick);

        Button addToCalendarButton = rootView.findViewById(R.id.add_to_calendar_button);
        addToCalendarButton.setOnClickListener(this::onAddToCalendarClick);

        return rootView;
    }
}
