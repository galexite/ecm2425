package uk.galexite.guildevents

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.*
import uk.galexite.guildevents.data.entity.Event
import uk.galexite.guildevents.data.entity.Organisation
import uk.galexite.guildevents.data.viewmodel.EventViewModel
import uk.galexite.guildevents.data.viewmodel.EventViewModelFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * The first activity the user sees when entering the app. It lists the Events in the database and
 * runs the UpdateDatabaseAsyncTask to update this Event data in the database, displaying the data
 * as it comes in.
 *
 * When an Event item is clicked, the EventDetailActivity is started if on a phone (and thus not in
 * two-pane mode), or the EventDetailFragment is loaded and displayed if the device is wider than
 * 900dp, and then the event's details are further displayed.
 *
 * Based on the automatically generated Master-Detail template in Android Studio.
 */
class EventListActivity : AppCompatActivity() {
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane = false

    /**
     * The view model.
     */
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as GuildEventsApplication).eventRepository)
    }

    /**
     * The ListAdapter.
     */
    private val eventListAdapter = EventListAdapter(this)

    /**
     * The current Organiser id for filtering the Event list.
     */
    private var organiserId: Int? = null

    /**
     * The 'filter by organisation' spinner's OnItemSelectedListener. Sets the filter for the event
     * list when a specific organisation is selected to filter by.
     */
    private val onOrganisationFilterSelectedListener =
        object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) { // 'All organisations' was selected
                    // There should not be filter applied to the list of events, so clear it.
                    updateRecyclerView(null)
                    return
                }

                // The user selected an organisation, so filter the Events list for events which are
                // organised by this Organisation they have selected.
                val organisation = parent.adapter.getItem(position) as Organisation
                updateRecyclerView(organisation.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Don't do anything, the user may have clicked off the pop-up, so probably don't want
                // to change the filter.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        if (findViewById<View?>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        // TODO ???
        (application as GuildEventsApplication).updateDatabase()

        // Populate the filter 'spinner' with the list of organisations in the database.
        val spinner = findViewById<Spinner>(R.id.spinner)
        setupFilterByOrganisationSpinner(spinner)

        // Set up the recycler view with our database.
        val recyclerView = findViewById<RecyclerView>(R.id.item_list)
        setupRecyclerView(recyclerView)
    }

    /**
     * Set up the spinner allowing users to select specific organisations which they are interested
     * in Events by.
     *
     * @param spinner the Spinner view to populate with Organisation objects
     */
    private fun setupFilterByOrganisationSpinner(spinner: Spinner) {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = onOrganisationFilterSelectedListener

        eventViewModel.allOrganisations.observe(this, { organisations: List<Organisation> ->
            adapter.clear()
            adapter.add(resources.getString(R.string.all_organisers))
            adapter.addAll(organisations.map { o -> o.name })
        })
    }

    /**
     * Set up the given RecyclerView to list all the events starting from now in the database.
     *
     * @param recyclerView the RecyclerView to set up with the Events from the database
     */
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = eventListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, DividerItemDecoration.VERTICAL
            )
        )

        updateRecyclerView(null)
    }

    /**
     * Update the RecyclerView with events filtered by an organisation.
     *
     * @param organisationFilter the id of the Organisation to filter by, or null if there shall
     * be no filter applied
     */
    private fun updateRecyclerView(organisationFilter: Int?) {
        // Remove any existing observers to stop them from adding Events to the RecyclerView.
        if (organiserId == null) {
            eventViewModel.allEventsFromNow.removeObservers(this)
        } else {
            eventViewModel.getAllEventsOrganisedBy(organiserId!!).removeObservers(this)
        }

        // Apply the new Organisation filter.
        organiserId = organisationFilter
        val liveData =
            if (organiserId == null) eventViewModel.allEventsFromNow else eventViewModel.getAllEventsOrganisedBy(
                organiserId!!
            )

        liveData.observe(this, { events -> eventListAdapter.submitList(events) })
    }

    /**
     * The RecyclerView adapter that displays all the events (possibly filtered by the spinner) for
     * the user to browse through.
     */
    class EventListAdapter internal constructor(
        private val parentActivity: EventListActivity
    ) : ListAdapter<Event, EventListAdapter.EventViewHolder>(EventComparator()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
            return EventViewHolder.create(parent, parentActivity)
        }

        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
            val event = getItem(position)
            holder.bind(event)
        }

        class EventViewHolder(view: View, parentActivity: EventListActivity) :
            RecyclerView.ViewHolder(view) {
            private val organiserNameView = view.findViewById<TextView>(R.id.event_organiser_name)
            private val nameView = view.findViewById<TextView>(R.id.event_name)
            private val fromDateView = view.findViewById<TextView>(R.id.event_from_date)

            private val onClickListener = View.OnClickListener { view ->
                val event = view.tag as Event

                if (parentActivity.twoPane) {
                    // Build the EventFragment in to this Activity so the list of Events and the
                    // details (provided by this Fragment) can be displayed side by side.
                    val arguments = Bundle()
                    arguments.putInt(EventDetailFragment.ARG_ITEM_ID, event.id)

                    val fragment = EventDetailFragment()
                    fragment.arguments = arguments
                    fragment.setHasOptionsMenu(true)
                    parentActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
                } else {
                    // Open the EventDetailActivity on devices which are not large enough to show
                    // the list and the detail side by side.
                    val context = view.context
                    val intent = Intent(context, EventDetailActivity::class.java)
                    intent.putExtra(EventDetailFragment.ARG_ITEM_ID, event.id)
                    context.startActivity(intent)
                }
            }

            fun bind(event: Event) {
                organiserNameView.text = event.organiserName
                nameView.text = event.name
                fromDateView.text = simpleDateFormat.format(Timestamp.valueOf(event.fromDate))
                itemView.tag = event
                itemView.setOnClickListener(onClickListener)
            }

            companion object {
                private val simpleDateFormat by lazy {
                    SimpleDateFormat(
                        "EEEE, d MMMM, h:mm aa",
                        Locale.ENGLISH
                    )
                }

                fun create(
                    parent: ViewGroup,
                    parentActivity: EventListActivity
                ): EventViewHolder {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.event_list_content, parent, false)

                    return EventViewHolder(view, parentActivity)
                }
            }
        }

        class EventComparator : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem === newItem

            // TODO: efficiency?
            override fun areContentsTheSame(oldItem: Event, newItem: Event) =
                oldItem.id == oldItem.id
                        && oldItem.name == newItem.name
                        && oldItem.fromDate == newItem.fromDate
                        && oldItem.organiserId == newItem.organiserId
                        && oldItem.description == newItem.description
                        && oldItem.location == newItem.location
                        && oldItem.url == newItem.url
        }
    }
}