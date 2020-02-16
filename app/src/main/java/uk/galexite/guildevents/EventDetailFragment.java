package uk.galexite.guildevents;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.CollapsingToolbarLayout;

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
     * The app bar layout.
     */
    private CollapsingToolbarLayout appBarLayout;
    /**
     * The event this Fragment is detailing.
     */
    private Event mEvent;
    /**
     * The organiser's name text view for the event.
     */
    private TextView organiserView;
    /**
     * The name text view for the event.
     */
    private TextView nameView;
    /**
     * The description text view for the event.
     */
    private TextView detailView;
    private TextView dateRangeView;

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

            Activity activity = this.getActivity();
            assert activity != null;

            appBarLayout = activity.findViewById(R.id.toolbar_layout);
        }
    }

    private void setEvent(@NonNull Event event) {
        mEvent = event;

        if (organiserView != null) {
            organiserView.setText(event.getOrganiserName());
        }

        if (nameView != null) {
            nameView.setText(event.getName());
        }

        if (detailView != null) {
            detailView.setText(event.getDescription());
        }

        if (dateRangeView != null) {
            dateRangeView.setText(mEvent.getFromDate());
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

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail, container, false);

        organiserView = rootView.findViewById(R.id.event_organiser_name);
        nameView = rootView.findViewById(R.id.event_name);
        detailView = rootView.findViewById(R.id.event_description);
        dateRangeView = rootView.findViewById(R.id.date_range);

//        // Associate the 'Open in Browser' button with its OnClickListener.
//        rootView.findViewById(R.id.open_in_browser_button)
//                .setOnClickListener(onOpenInBrowserClickListener);

        return rootView;
    }
}
