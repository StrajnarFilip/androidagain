package com.proculite.androidagain.common.storageaccess;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class OpenDocumentGuide implements ActivityResultCallback<Uri> {
    private final String TAG = ActivityResultContracts.OpenDocument.class.getName();
    private final ActivityResultLauncher<String[]> openDocumentLauncher;
    private final Context context;
    private UriHandler uriHandler;

    public OpenDocumentGuide(
            Context context,
            ActivityResultCaller activityResultCaller
    ) {
        this.context = context;
        ActivityResultContracts.OpenDocument openDocumentContract =
                new ActivityResultContracts.OpenDocument();
        openDocumentLauncher = activityResultCaller.registerForActivityResult(openDocumentContract, this);
    }

    @Override
    public void onActivityResult(Uri o) {
        uriHandler.handleUri(o);
    }

    public void readFromInputStream(String[] suggestedMimeTypes, InputStreamHandler inputStreamHandler){
        uriHandler = uri -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if(inputStream == null)
                {
                    Log.e(TAG, "Obtained input stream is null.");
                    return;
                }
                inputStreamHandler.handleInputStream(inputStream);
                inputStream.close();
            }catch (FileNotFoundException e){
                Log.e(TAG, "FileNotFoundException occurred on read from InputStream.");
            } catch (IOException e) {
                Log.e(TAG, "IOException occurred on read from InputStream.");
            }
        };

        openDocumentLauncher.launch(suggestedMimeTypes);
    }
}
