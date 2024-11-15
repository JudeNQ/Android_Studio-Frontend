package com.example.eventcal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfileActivity extends Activity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private EditText usernameEditText;
    private EditText biographyEditText;
    private Button changeProfilePicButton;
    private Button saveProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImageView = findViewById(R.id.profileImageView);
        usernameEditText = findViewById(R.id.usernameEditText);
        biographyEditText = findViewById(R.id.biographyEditText);
        changeProfilePicButton = findViewById(R.id.changeProfilePicButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        changeProfilePicButton.setOnClickListener(v -> openImagePicker());
        saveProfileButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    private void saveProfileChanges() {
        String username = usernameEditText.getText().toString();
        String biography = biographyEditText.getText().toString();

        if (username.isEmpty() || biography.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Logic to save data to backend, e.g., Django API call
            // Example API call to update profile
            // Api.updateProfile(username, biography, selectedImageUri);

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
