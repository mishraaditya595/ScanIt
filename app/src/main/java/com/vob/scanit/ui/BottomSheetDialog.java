package com.vob.scanit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.vob.scanit.R;

import java.io.File;
import java.util.Objects;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    File file;

    public BottomSheetDialog(File file) {
        this.file = file;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.file_options_bottom_sheet_view,
                container, false);

        final Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), "com.vob.scanit.provider", file);

        Button deleteBtn = v.findViewById(R.id.delete_btn);
        Button shareBtn = v.findViewById(R.id.share_btn);
        Button renameBtn = v.findViewById(R.id.rename_btn);

        shareBtn.setOnClickListener(viewOnClickListener -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("application/pdf");
            startActivity(Intent.createChooser(intent, "Share via"));
            dismiss();
        });

        renameBtn.setOnClickListener(viewOnClickListener -> dismiss());

        deleteBtn.setOnClickListener(viewOnClickListener -> {
            File fDelete = file;
            if (fDelete.exists()) {
                if (fDelete.delete()) {
                    DynamicToast.makeSuccess(Objects.requireNonNull(getActivity()), "File deleted successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "File could not be deleted." + uri.getPath(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Error: File not found", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
        return v;
    }
}
