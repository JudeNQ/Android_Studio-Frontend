package com.example.eventcal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventcal.R;
import com.example.eventcal.models.TimeData;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {

    private List<TimeData> timeDataList;
    private OnItemClickListener onItemClickListener;

    // Constructor with a listener
    public TimeAdapter(List<TimeData> timeList, OnItemClickListener onItemClickListener) {
        this.timeDataList = timeList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        TimeData timeData = timeDataList.get(position);
        String timeframe = timeData.getStartTime() + " - " + timeData.getEndTime();
        holder.timeFrame.setText(timeframe);

        // Set an OnClickListener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(timeData); // Pass the data associated with the clicked item
            }
        });
    }

    public void addTimeData(TimeData data) {
        timeDataList.add(data);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public void updateTimeData(TimeData data) {
        int index = timeDataList.indexOf(data);
        if (index != -1) {
            timeDataList.set(index, data);
            notifyItemChanged(index); // Notify the adapter that the item has changed
        }
    }

    @Override
    public int getItemCount() {
        return timeDataList.size();
    }

    // ViewHolder class
    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView timeFrame;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            timeFrame = itemView.findViewById(R.id.time_frame);
        }
    }

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(TimeData timeData);  // Handle the click event and pass the clicked item data
    }
}