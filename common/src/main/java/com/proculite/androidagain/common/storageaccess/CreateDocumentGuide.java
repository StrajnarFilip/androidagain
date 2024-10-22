package com.proculite.androidagain.common.storageaccess;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class CreateDocumentGuide implements ActivityResultCallback<Uri> {
    private final String TAG = ActivityResultContracts.CreateDocument.class.getName();
    private final ActivityResultLauncher<String> createDocumentLauncher;
    private final Context context;
    private UriHandler uriHandler;

    /**
     * This constructor registers a request to start an activity for a result, therefore it must
     * be called unconditionally, as part of the initialization path, typically as a field
     * initializer of an Activity or Fragment. A suitable location to call it would be the within
     * {@link android.app.Activity#onCreate(Bundle, PersistableBundle)} method.
     * @param activityResultCaller Activity result callback, such as
     * {@link androidx.fragment.app.Fragment} or {@link androidx.appcompat.app.AppCompatActivity}.
     * @param mimeType Mime type of document.
     */
    public CreateDocumentGuide(
            Context context,
            ActivityResultCaller activityResultCaller,
            String mimeType
    ){
        this.context = context;
        ActivityResultContracts.CreateDocument createDocumentContract =
                new ActivityResultContracts.CreateDocument(mimeType);

        createDocumentLauncher = activityResultCaller
                .registerForActivityResult(createDocumentContract, this);
    }

    @Override
    public final void onActivityResult(Uri o) {
        uriHandler.handleUri(o);
    }

    public void writeToOutputStream(String suggestedFileName, OutputStreamHandler outputStreamHandler){
        uriHandler = uri -> {
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                outputStreamHandler.handleOutputStream(outputStream);
                if(outputStream != null) {
                    outputStream.close();
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFoundException occurred on write to OutputStream.");
            } catch (IOException e) {
                Log.e(TAG, "IOException occurred on write to OutputStream.");
            }
        };
        createDocumentLauncher.launch(suggestedFileName);
    }

    public void writeBytes(String suggestedFileName, byte[] bytesToWrite){
        writeToOutputStream(suggestedFileName, outputStream -> {
            try {
                outputStream.write(bytesToWrite);
            } catch (IOException e) {
                Log.e(TAG, "IOException occurred when writing bytes.");
            }
        });
    }

    public void writeString(String suggestedFileName, String stringToWrite){
        writeBytes(suggestedFileName, stringToWrite.getBytes(StandardCharsets.UTF_8));
    }
}
