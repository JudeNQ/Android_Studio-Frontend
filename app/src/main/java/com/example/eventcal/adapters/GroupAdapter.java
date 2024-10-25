package com.example.eventcal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventcal.R;
import com.example.eventcal.models.Group;

import java.util.List;

import kotlin.jvm.internal.markers.KMutableList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;

    public GroupAdapter(List<Group> groupList) {
        this.groupList = groupList;
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

        // Toggle visibility of the more info layout
        holder.moreInfoButton.setOnClickListener(v -> {
            if (holder.moreInfoLayout.getVisibility() == View.GONE) {
                holder.moreInfoLayout.setVisibility(View.VISIBLE); // Show info
            } else {
                holder.moreInfoLayout.setVisibility(View.GONE); // Hide info
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        ImageButton moreInfoButton;
        LinearLayout moreInfoLayout;
        TextView groupBio;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            moreInfoButton = itemView.findViewById(R.id.more_btn);
            moreInfoLayout = itemView.findViewById(R.id.more_info_layout);
            groupBio = itemView.findViewById(R.id.group_info);
        }
    }
}
