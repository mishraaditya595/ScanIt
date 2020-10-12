package com.vob.scanit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vob.scanit.R;
import com.vob.scanit.ui.activities.MainActivity;

import java.io.File;

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

        final Uri uri = FileProvider.getUriForFile(getContext(), "com.vob.scanit.provider", file);

        Button deleteBtn = v.findViewById(R.id.delete_btn);
        Button shareBtn = v.findViewById(R.id.share_btn);
        Button renameBtn = v.findViewById(R.id.rename_btn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("application/pdf");
                startActivity(Intent.createChooser(intent,"Share via"));
                dismiss();
            }
        });

        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                File fdelete = new File(uri.getPath());
                if (fdelete.exists())
                {
                    if (fdelete.delete())
                    {
                        Toast.makeText(getActivity(),"file Deleted :" + uri.getPath(),Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"file Deleted :" + uri.getPath(),Toast.LENGTH_SHORT).show();
                    }
                }
                dismiss();
            }
        });
        return v;
    }
}
