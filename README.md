# Guild Events

This app shows events scraped from the University of Exeter Student's Guild website. It allows the
user to browse through these events, even when they are offline (as long as they have initially used
the app on an Internet connection). The list of events are updated when the EventListActivity
detects when they are out of date, and there is an Internet connection available.

Users may view specific events in detail by tapping on them in the list. They may filter down this
list to see only events created by certain organisations, such as the 'Out of Doors Society',
allowing them to keep track of up and coming events held by that society.

If a user is interested in adding the event to the calendar to remind them, they can select 'Add to
Calendar' from the detail screen for that event. Searching for the event on Google Maps is possible
using the 'Locate on Map' button if the event location was made available on the website.

The user may share events with friends or classmates using the 'Share' menu button, or open the
event in their web browser using the 'Open in Browser' button.

Tested primarily on Nexus 5 and Nexus 5X API 28 emulators, and on a real Moto G5S device.