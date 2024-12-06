package com.example.eventcal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventcal.R;
import com.example.eventcal.models.TimeData;
import com.example.eventcal.models.WeekDays;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {

    private List<TimeData> timeDataList;
    public WeekDays day;

    // Constructor with a listener
    public TimeAdapter(List<TimeData> timeList, WeekDays day) {
        this.timeDataList = timeList;
        this.day = day;
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

        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                timeDataList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, timeDataList.size());
            }
        });

        holder.startTime.setOnFocusChangeListener((v, hasFocus) -> {
            //User clicked off the text box
            if (!hasFocus) {
                String startTime = formatTime(holder.startTime.getText().toString());
                String formattedTime = formatTime(holder.startTime.getText().toString());
                if(formattedTime.compareTo("") < 0) {
                    holder.startTime.setText("");
                    timeData.setStartTime("");
                    //Make a toast and say its invalid
                    Toast.makeText(v.getContext(), "Invalid Time Format. Please use xx:xxAM/PM", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeData.setStartTime(formattedTime); // Update the data model
                holder.startTime.setText(formattedTime); // Update the UI
            }
        });

        holder.endTime.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String endTime = formatTime(holder.endTime.getText().toString());
                String formattedTime = formatTime(holder.endTime.getText().toString());
                if(formattedTime.compareTo("") < 0) {
                    holder.endTime.setText("");
                    timeData.setEndTime("");
                    //Make a toast and say its invalid
                    Toast.makeText(v.getContext(), "Invalid Time Format. Please use xx:xxAM/PM", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeData.setEndTime(formattedTime);
                holder.endTime.setText(formattedTime);
            }
        });
    }

    public void addTimeData(TimeData data) {
        timeDataList.add(data);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public void addTimeData() {
        timeDataList.add(new TimeData("", ""));
        notifyItemInserted(timeDataList.size() - 1);
    }

    public void updateTimeData(TimeData data) {
        int index = timeDataList.indexOf(data);
        if (index != -1) {
            timeDataList.set(index, data);
            notifyItemChanged(index); // Notify the adapter that the item has changed
        }
    }

    public List<TimeData> getTimeDataList() {
        return timeDataList;
    }

    private String formatTime(String input) {
        if (input == null || input.isEmpty()) return "";

        //Simple regex-based parsing
        input = input.toUpperCase().replace(" ", ""); // Remove spaces and ensure uppercase
        if (input.matches("\\d{1,2}:\\d{2}[AP]M")) {
            return input; //Already properly formatted
        }

        if (input.matches("\\d{1,2}:\\d{2}[AP]")) {
            return input + "M"; //Add missing 'M'
        }

        if (input.matches("\\d{1,2}:\\d{2}")) {
            //Assume user input is in 24-hour format
            try {
                String[] parts = input.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                String period = (hour >= 12) ? "PM" : "AM";
                hour = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
                return String.format("%02d:%02d%s", hour, minute, period);
            } catch (NumberFormatException e) {
                return input; // Invalid input; return as-is
            }
        }

        //Return input unchanged if it doesn't match any pattern
        return "";
    }


    @Override
    public int getItemCount() {
        return timeDataList.size();
    }

    // ViewHolder class
    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        EditText startTime;
        EditText endTime;
        ImageButton deleteButton;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}