package com.example.eventcal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventcal.R
import com.example.eventcal.models.Event
import java.time.format.DateTimeFormatter

class EventAdapter(
    private val events: List<Event>,
    private val onDeleteClick: (Event) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private val items: List<ListItem> = createListItems(events)

    // Define ListItem sealed class here
    sealed class ListItem {
        data class DateHeader(val date: String) : ListItem()
        data class EventItem(val event: Event) : ListItem()
    }

    private fun createListItems(events: List<Event>): List<ListItem> {
        val eventsByDate: MutableMap<String, MutableList<Event>> = mutableMapOf()

        // Group events by date
        for (event in events) {
            val date = event.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            eventsByDate.getOrPut(date) { mutableListOf() }.add(event)
        }

        val listItems = mutableListOf<ListItem>()

        // Populate the final list with headers and events
        for ((date, eventsList) in eventsByDate) {
            listItems.add(ListItem.DateHeader(date)) // Add the date header
            for (event in eventsList) {
                listItems.add(ListItem.EventItem(event)) // Add each event
            }
        }

        return listItems
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.DateHeader -> TYPE_HEADER
            is ListItem.EventItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.date_header_item, parent, false)
            DateHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
            EventViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder) {
            val eventItem = items[position] as ListItem.EventItem
            holder.titleTextView.text = eventItem.event.title
            holder.dateTextView.text = eventItem.event.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

            /*holder.deleteButton.setOnClickListener {                FIXME
                onDeleteClick(eventItem.event)
            }*/
        } else if (holder is DateHeaderViewHolder) {
            val headerItem = items[position] as ListItem.DateHeader
            holder.dateHeaderTextView.text = headerItem.date
        }
    }

    override fun getItemCount(): Int = items.size

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.event_title)
        val dateTextView: TextView = view.findViewById(R.id.event_time)
        //val deleteButton: Button = view.findViewById(R.id.delete_button)  FIXME with the other delete thing in the item
    }

    class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateHeaderTextView: TextView = view.findViewById(R.id.date_header)
    }
}
