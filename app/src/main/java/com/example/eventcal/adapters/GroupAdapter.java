package com.example.eventcal.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventcal.R;
import com.example.eventcal.models.Group;
import com.example.eventcal.pojo.Schedule;
import com.example.eventcal.userStorage.UserInfo;

import java.util.List;

import kotlin.jvm.internal.markers.KMutableList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;
    private GroupActionListener groupActionListener;

    public interface GroupActionListener {
        void onJoinGroup(String groupId);
    }

    public GroupAdapter(List<Group> groupList, GroupActionListener listener) {
        this.groupList = groupList;
        this.groupActionListener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupName.setText(group.getName());
        holder.groupBio.setText(group.getBio());
        //See if the group has a schedule (so if the user is in it) add the format if applicable
        if(group.getSchedule() == null) {
            Log.d("Group Adapter Schedule", "The schedule is null for some reason");
            holder.groupSchedule.setText("No Schedule");
        }
        else {
            holder.groupSchedule.setText(formatSchedule(group.getSchedule()));
        }



        //Set button to either join or dropdown depending on if the user is in the group
        if(UserInfo.getInstance().groups.contains(group.getId())) {
            //Set the more info to visible and join to invisible
            holder.moreInfoButton.setVisibility(View.VISIBLE);
            holder.joinButton.setVisibility(View.GONE);
        }
        else {
            holder.moreInfoButton.setVisibility(View.GONE);
            holder.joinButton.setVisibility(View.VISIBLE);
        }

        // Toggle visibility of the more info layout
        holder.moreInfoButton.setOnClickListener(v -> {
            if (holder.moreInfoLayout.getVisibility() == View.GONE) {
                holder.moreInfoLayout.setVisibility(View.VISIBLE); // Show info
                //Change the icon for the button?
                holder.moreInfoButton.setImageResource(R.drawable.arrow_dropdown_up);
            } else {
                holder.moreInfoLayout.setVisibility(View.GONE); // Hide info
                holder.moreInfoButton.setImageResource(R.drawable.arrow_dropdown_down);
            }
        });

        //Handle the user trying to join a certain group
        // Toggle visibility of the more info layout
        holder.joinButton.setOnClickListener(v -> {
            if (groupActionListener != null) {
                groupActionListener.onJoinGroup(group.getId());
            }
        });
    }

    public String formatSchedule(Schedule schedule) {
        Log.d("Group Adapter Schedule", "In formatting schedule");
        StringBuilder output = new StringBuilder();
        output.append("Available Times: \n");
        output.append("Monday: \n");
        for(Pair<String, String> time : schedule.monday) {
            output.append(time.first).append("-").append(time.second).append("\n");
        }

        output.append("Tuesday: \n");
        for(Pair<String, String> time : schedule.tuesday) {
            output.append(time.first).append("-").append(time.second).append("\n");
        }

        output.append("Wednesday: \n");
        for(Pair<String, String> time : schedule.wednesday) {
            output.append(time.first).append("-").append(time.second).append("\n");
        }

        output.append("Thursday: \n");
        for(Pair<String, String> time : schedule.thursday) {
            output.append(time.first).append("-").append(time.second).append("\n");
        }

        output.append("Friday: \n");
        for(Pair<String, String> time : schedule.friday) {
            output.append(time.first).append("-").append(time.second).append("\n");
        }

        output.append("Saturday: \n");
        for(Pair<String, String> time : schedule.saturday) {
            output.append(time.first).append("-").append(time.second).append("\n");
        }

        output.append("Sunday: \n");
        for(Pair<String, String> time : schedule.sunday) {
            output.append(time.first).append("-").append(time.second).append("\n");
        }

        //Finalize the string builder
        return output.toString();

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        ImageButton moreInfoButton;
        Button joinButton;
        LinearLayout moreInfoLayout;
        TextView groupBio;
        TextView groupSchedule;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            moreInfoButton = itemView.findViewById(R.id.more_btn);
            joinButton = itemView.findViewById(R.id.join_btn);
            moreInfoLayout = itemView.findViewById(R.id.more_info_layout);
            groupBio = itemView.findViewById(R.id.group_info);
            groupSchedule = itemView.findViewById(R.id.group_schedule_info);
        }
    }
}