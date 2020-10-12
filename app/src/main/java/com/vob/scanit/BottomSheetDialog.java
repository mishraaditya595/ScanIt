package com.vob.scanit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    int position;
    public BottomSheetDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.file_options_bottom_sheet_view,
                container, false);

        Button deleteBtn = v.findViewById(R.id.delete_btn);
        Button shareBtn = v.findViewById(R.id.share_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "Click on delete btn", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "Click on share btn", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return v;
    }
}
