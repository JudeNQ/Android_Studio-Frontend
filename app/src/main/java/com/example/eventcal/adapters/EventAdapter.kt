package com.example.eventcal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.MainActivity
import com.example.eventcal.R
import com.example.eventcal.models.Event
import com.example.eventcal.pojo.SaveEvent
import com.example.eventcal.userStorage.UserInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
//TODO If empty, prompt the user to either create or join an event
class EventAdapter(
    private val context : Context,
    private val allEvents: List<Event>,
    private val showSavedOnly: Boolean,
    private val onDeleteClick: (Event) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ListItem> = createListItems(allEvents)
    private val TYPE_HEADER = 0
    private val TYPE_SUMMARY = 1
    private val TYPE_DETAILS = 2
    private var expandedPosition = -1

    // Define ListItem sealed class here
    sealed class ListItem {
        data class DateHeader(val date: String) : ListItem()
        data class EventItem(val event: Event) : ListItem()
    }

    private fun createListItems(events: List<Event>): List<ListItem> {
        val eventsByDate = events.groupBy { it.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
        val listItems = mutableListOf<ListItem>()

        for ((date, eventsList) in eventsByDate) {
            listItems.add(ListItem.DateHeader(date)) // Add date header
            eventsList.forEach { listItems.add(ListItem.EventItem(it)) } // Add each event
        }

        return listItems
    }

    fun filter(query:String){
        var filteredEvents = if (query.isBlank()){
            allEvents
        } else{
            allEvents.filter{it.title.contains(query,ignoreCase = true)}
        }

        val eventsToShow = if (showSavedOnly) {
            val mainActivity = context as? MainActivity
            mainActivity?.GetUsersEvents(UserInfo.getInstance().userId) {
                if(it != null) run {
                    var tempList : ArrayList<Event> = ArrayList()
                    for (event in it.data) {
                        tempList.add(Event(
                            event.eventName,
                            LocalDateTime.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            event.description,
                            event.eventId))
                    }
                    filteredEvents = tempList
                }
            }
            filteredEvents
            //Get the saved events from the server
        } else {
            filteredEvents
        }
        items = createListItems(filteredEvents)
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.DateHeader -> TYPE_HEADER
            is ListItem.EventItem -> if (position == expandedPosition) TYPE_DETAILS else TYPE_SUMMARY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val view = layoutInflater.inflate(R.layout.date_header_item, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_DETAILS -> {
                val view = layoutInflater.inflate(R.layout.item_event_details, parent, false)
                EventDetailsViewHolder(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_event, parent, false)
                EventSummaryViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateHeaderViewHolder -> {
                val headerItem = items[position] as ListItem.DateHeader
                holder.dateHeaderTextView.text = headerItem.date
            }
            is EventSummaryViewHolder -> {
                val eventItem = items[position] as ListItem.EventItem
                holder.bind(eventItem.event) {
                    expandedPosition = if (expandedPosition == position) RecyclerView.NO_POSITION else position
                    notifyItemChanged(position)
                }
            }
            is EventDetailsViewHolder -> {
                val eventItem = items[position] as ListItem.EventItem
                holder.bind(eventItem.event) {
                    expandedPosition = RecyclerView.NO_POSITION
                    notifyItemChanged(position)
                }
                // Handle the Save button click
                holder.saveButton.setOnClickListener {
                    val mainActivity = context as? MainActivity
                    val saveEvent = SaveEvent(UserInfo.getInstance().userId, eventItem.event.id)
                    mainActivity?.saveEvent(saveEvent) {
                        if (it != null) run {
                            //Just give a toast message?
                            if(it.message == "Event added successfully") {
                                //Nice positive toast
                                Toast.makeText(
                                    context,
                                    "Saved event successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else {
                                //Negative toast
                                Toast.makeText(
                                    context,
                                    "Failed to save event",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int = items.size

    class EventSummaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.event_title)
        val dateTextView: TextView = view.findViewById(R.id.event_time)

        fun bind(event: Event, onClick: () -> Unit) {
            titleTextView.text = event.title
            dateTextView.text = event.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            itemView.setOnClickListener { onClick() }
        }
    }

    class EventDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.event_title)
        val dateTextView: TextView = view.findViewById(R.id.event_time)
        val descriptionTextView: TextView = view.findViewById(R.id.event_description)
        val saveButton: Button = itemView.findViewById(R.id.save_button)

        fun bind(event: Event, onClick: () -> Unit) {
            titleTextView.text = event.title
            dateTextView.text = event.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            descriptionTextView.text = event.description
            itemView.setOnClickListener { onClick() }
        }
    }

    class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateHeaderTextView: TextView = view.findViewById(R.id.date_header)
    }
}
