package com.example.eventcal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends Activity {

    private SwitchCompat hideCalendarSwitch;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        hideCalendarSwitch = findViewById(R.id.hideCalendarSwitch);
        changePasswordButton = findViewById(R.id.btnChangePassword);

        // Initialize switch state (example, this could be fetched from your database)
        hideCalendarSwitch.setChecked(false);

        // Set a listener for the switch to handle the state change
        hideCalendarSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Logic to hide/show calendar (e.g., update backend)
            // Api.updateHideCalendar(isChecked);
            if (isChecked) {
                Toast.makeText(SettingsActivity.this, "Calendar hidden", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Calendar visible", Toast.LENGTH_SHORT).show();
            }
        });

        // Set an onClickListener for the change password button
        changePasswordButton.setOnClickListener(v -> {
            // Change password flow
            changePassword();
        });
    }

    private void changePassword() {
        // Assuming password is always valid when this method is called
        String newPassword = "newUserPassword"; // This should be retrieved from user input

        if (newPassword.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic to update password in backend (e.g., Django API call)
        // Api.changePassword(newPassword);

        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
    }
}
