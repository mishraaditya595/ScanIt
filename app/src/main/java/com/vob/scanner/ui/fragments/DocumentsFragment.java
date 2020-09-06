package com.vob.scanner.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.vob.scanner.R;
import com.vob.scanner.ui.DisplayActivity;
import com.vob.scanner.ui.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.vob.scanner.constants.Constants.OPEN_THING;

public class DocumentsFragment extends Fragment {

    FloatingActionButton openCameraButton;
    FloatingActionButton openFilesButton;

    public DocumentsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        openCameraButton = view.findViewById(R.id.openCameraButton);
        openFilesButton = view.findViewById(R.id.openFilesButton);

        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Camera Open", Toast.LENGTH_SHORT).show();
            }
        });

        openFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFiles();
            }
        });

        return view;
    }

    public void openFiles() {
        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, OPEN_THING);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == OPEN_THING) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), uri);
                    getActivity().getContentResolver().delete(uri, null, null);

                    Bitmap compressedBitmap = compressBitmap(bitmap);

                    String image = bitmapToString(bitmap);

                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra("image", image);
                    startActivity(intent);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40 , byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return compressedBitmap;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}