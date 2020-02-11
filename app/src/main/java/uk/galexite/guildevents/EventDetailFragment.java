package uk.galexite.guildevents;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.viewmodel.EventViewModel;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The view model.
     */
    private EventViewModel mEventViewModel;

    /**
     * The app bar layout.
     */
    CollapsingToolbarLayout appBarLayout;
    /**
     * The description text view for the event.
     */
    TextView detailView;
    /**
     * The event this Fragment is detailing.
     */
    private Event mEvent;
    /**
     * OnClickListener for the button the user can press to open the event in their web browser.
     * <p>
     * Uses an `ACTION_VIEW` {@link Intent} to open the web browser on the event's URL.
     */
    private View.OnClickListener onOpenInBrowserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
        }
    };

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
            mEventViewModel.getEvent(id).observe(this, new Observer<Event>() {
                @Override
                public void onChanged(Event event) {
                    setEvent(event);
                }
            });

            Activity activity = this.getActivity();
            assert activity != null;

            appBarLayout = activity.findViewById(R.id.toolbar_layout);
        }
    }

    public void setEvent(@NonNull Event event) {
        mEvent = event;

        if (appBarLayout != null) {
            appBarLayout.setTitle(event.getName());
        }

        // TODO: tech debt
        if (detailView != null) {
            detailView.setText(event.getDescription());
        }
    }

    public void onOpenInBrowserButtonClick(View view) {
        Uri uri = Uri.parse(mEvent.getUrl());

        /* Sometimes, an event may link to an external source. We need to check and make sure if the
           host is not already present before we go and add the Guild's domain name to the URI. */
        if (uri.getScheme() == null || uri.getScheme().isEmpty()) {
            uri = uri.buildUpon()
                    .scheme("http")
                    .authority("www.exeterguild.org")
                    .build();
        }

        Intent intent = new Intent(uri.toString());
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail, container, false);

        // Show the dummy content as text in a TextView.
        detailView = rootView.findViewById(R.id.item_detail);

        // Associate the 'Open in Browser' button with its OnClickListener.
        rootView.findViewById(R.id.open_in_browser_button)
                .setOnClickListener(onOpenInBrowserClickListener);

        return rootView;
    }
}
