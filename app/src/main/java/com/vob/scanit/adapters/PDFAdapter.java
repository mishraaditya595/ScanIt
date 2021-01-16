package com.vob.scanit.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.vob.scanit.R;
import com.vob.scanit.ui.activities.MainActivity;
import com.vob.scanit.ui.fragments.HomeFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

public class PDFAdapter extends ArrayAdapter<File> {

    Context context;
    ArrayList<File> fileList;
    ArrayList<File> searchList = new ArrayList<>();
    ViewHolder viewHolder;
    private ImageView options_btn;
    Activity activity;

    public PDFAdapter(Context context, ArrayList<File> fileList, Activity activity) {
        super(context, R.layout.adapter_pdf, fileList);
        this.context = context;
        this.fileList = fileList;
        this.activity = activity;
        this.searchList.addAll(this.fileList);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (fileList.size() > 0)
            return fileList.size();
        else
            return 1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.filename = view.findViewById(R.id.filename_TV);

            view.setTag(viewHolder);
        }
        else
            {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.filename.setText(fileList.get(position).getName());
        options_btn = view.findViewById(R.id.options_btn);

        options_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicToast.makeWarning(v.getContext(),"Long press on the filename to open options menu",Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }

    public void filter(String characterText) {
        characterText = characterText.toLowerCase(Locale.getDefault());
        fileList.clear();
        if (characterText.length() == 0)
        {
            fileList.addAll(searchList);
        }
        else
        {
            for (File file : searchList)
            {
                if (file.getName().toLowerCase(Locale.getDefault()).contains(characterText))
                {
                    fileList.add(file);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder
    {
        TextView filename;
    }
}
