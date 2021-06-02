package uk.galexite.guildevents

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import uk.galexite.guildevents.data.entity.Event
import uk.galexite.guildevents.data.viewmodel.EventViewModel
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a [EventListActivity]
 * in two-pane mode (on tablets) or a [EventDetailActivity]
 * on handsets.
 *
 * Suppressing 'Weaker Access' because Fragments must be public.
 */
class EventDetailFragment : Fragment() {
    /**
     * The view model.
     */
    private lateinit var eventViewModel: EventViewModel

    /**
     * The event this Fragment is detailing.
     */
    private var event: Event? = null

    /**
     * The organiser's name text view for the event.
     */
    private var organiserView: TextView? = null

    /**
     * The name text view for the event.
     */
    private var nameView: TextView? = null

    /**
     * The description text view for the event.
     */
    private var detailView: TextView? = null

    /**
     * The card view showing the event's location.
     */
    private var locationCardView: CardView? = null

    /**
     * Location for the event.
     */
    private var locationView: TextView? = null

    /**
     * Displays the date and time when the event is running.
     */
    private var dateRangeView: TextView? = null
    private val simpleDateFormat =
        SimpleDateFormat("EEEE, d MMMM yyyy 'at' h:mm aa", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        val arguments = requireArguments()
        if (arguments.containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            val id = arguments.getInt(ARG_ITEM_ID)
            eventViewModel.getEvent(id)
                .observe(this, { event: Event? ->
                    if (event != null) {
                        setEvent(event)
                    }
                })
        }
    }

    /**
     * Set the Event that this Fragment is detailing.
     *
     * @param event to display
     */
    private fun setEvent(event: Event) {
        this.event = event

        organiserView?.text = event.organiserName
        nameView?.text = event.name
        detailView?.text = event.description
        if (event.location == null) {
            // If the location is null, there is no point in showing the location card!
            locationCardView?.visibility = View.INVISIBLE
        } else {
            if (locationView != null) {
                locationCardView!!.visibility = View.VISIBLE
                locationView!!.text = event.location
            }
        }
        dateRangeView?.text = simpleDateFormat.format(Timestamp.valueOf(event.fromDate))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.event_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.open_in_browser) { // Open this event in the browser
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = httpUri
            startActivity(intent)
            return true
        } else if (item.itemId == R.id.share) { // Share
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this event: $httpUri")
            intent.type = "text/plain"
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Get an HTTP Uri to the event to view in a web browser or share.
     *
     * @return a Uri with an HTTP scheme for viewing the event on the Guild's website.
     */
    private val httpUri: Uri
        get() {
            var uri = Uri.parse(event!!.url)

            /* Sometimes, an event may link to an external source. We need to check and make sure
               if the scheme is not already present before we go and add the Guild's domain name to
               the URI. */
            if (uri.scheme == null || uri.scheme!!.isEmpty()) {
                uri = uri.buildUpon()
                    .scheme("http")
                    .authority("www.exeterguild.org")
                    .build()
            }
            return uri
        }

    /**
     * Find the event's location on a map by creating an explicit intent to launch Google Maps to
     * search for the location near Exeter.
     */
    private fun onFindOnMapClick() {
        // These geo coordinates are for Exeter; events are most likely to be held near here.
        val intentUri = Uri.parse(
            "geo:50.716667,-3.533333?q="
                    + event?.location?.replace(' ', '+')
        )
        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    /**
     * Open the event in the user's calendar.
     */
    private fun onAddToCalendarClick() {
        val startDate = Timestamp.valueOf(event?.fromDate).time
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate)
            .putExtra(CalendarContract.Events.TITLE, event?.name)
            .putExtra(CalendarContract.Events.DESCRIPTION, event?.description)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, event?.location)
            .putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.event_detail, container, false)
        organiserView = rootView.findViewById(R.id.event_organiser_name)
        nameView = rootView.findViewById(R.id.event_name)
        detailView = rootView.findViewById(R.id.event_description)
        dateRangeView = rootView.findViewById(R.id.date_range)
        locationCardView = rootView.findViewById(R.id.location_card)
        locationView = rootView.findViewById(R.id.location)

        // Associate the buttons with their appropriate onClick handlers
        val findOnMapButton = rootView.findViewById<Button>(R.id.find_on_map_button)
        findOnMapButton.setOnClickListener { onFindOnMapClick() }
        val addToCalendarButton = rootView.findViewById<Button>(R.id.add_to_calendar_button)
        addToCalendarButton.setOnClickListener { onAddToCalendarClick() }
        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}