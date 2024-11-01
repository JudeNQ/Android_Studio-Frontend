package com.example.eventcal.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.example.eventcal.userStorage.UserInfo;

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

        //Set button to either join or dropdown depending on if the user is in the group
        if(UserInfo.info.groups.contains(group.getId())) {
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
            } else {
                holder.moreInfoLayout.setVisibility(View.GONE); // Hide info
            }
        });

        //Handle the user trying to join a certain group
        // Toggle visibility of the more info layout
        holder.joinButton.setOnClickListener(v -> {
            //Open a popup to join the group

        });
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

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            moreInfoButton = itemView.findViewById(R.id.more_btn);
            joinButton = itemView.findViewById(R.id.join_btn);
            moreInfoLayout = itemView.findViewById(R.id.more_info_layout);
            groupBio = itemView.findViewById(R.id.group_info);
        }
    }
}
class JoinGroupDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Make a nice little alert dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(inflater.inflate(R.layout.dialog_joingroup, null))
                // Add action buttons
                .setTitle("Join Group")
                .setPositiveButton("Join Group", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Sign in the user.
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JoinGroupDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
