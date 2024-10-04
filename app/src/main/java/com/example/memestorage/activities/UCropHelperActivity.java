package com.example.memestorage.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memestorage.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class UCropHelperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ucrop_helper);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.tv_test_ucrop_helper).setOnClickListener(v -> {
            Uri initialUri = Uri.parse(getIntent().getStringExtra("initialUri"));
            Uri destinationUri = Uri.fromFile(new File(this.getCacheDir(), "cropped_image.jpg" + System.currentTimeMillis()));
            UCrop.of(initialUri, destinationUri)
//                .withAspectRatio(16, 9)
//                .withMaxResultSize(maxWidth, maxHeight)
                    .start(this);

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            returnUriToFragment(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void returnUriToFragment(Uri newUri) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returnedUri", newUri);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}