package uk.galexite.guildevents;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.viewmodel.EventViewModel;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailActivity}
 * on handsets.
 */
class EventDetailFragment extends Fragment {
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
     * The dummy content this fragment is presenting.
     */
    private Event mEvent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            int id = getArguments().getInt(ARG_ITEM_ID);
            mEventViewModel.getEvent(id).observe(this, new Observer<Event>() {
                @Override
                public void onChanged(Event event) {
                    mEvent = event;
                }
            });

            Activity activity = this.getActivity();
            assert activity != null;

            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mEvent.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mEvent != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mEvent.getDescription());
        }

        return rootView;
    }
}
