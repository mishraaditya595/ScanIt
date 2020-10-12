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
import com.vob.scanit.R;
import com.vob.scanit.ui.activities.MainActivity;
import com.vob.scanit.ui.fragments.HomeFragment;

import java.io.File;
import java.util.ArrayList;

public class PDFAdapter extends ArrayAdapter<File> {

    Context context;
    ArrayList<File> fileList;
    ViewHolder viewHolder;
    private ImageView options_btn;
    Activity activity;

    public PDFAdapter(Context context, ArrayList<File> fileList, Activity activity) {
        super(context, R.layout.adapter_pdf, fileList);
        this.context = context;
        this.fileList = fileList;
        this.activity = activity;
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


        return view;
    }

    public class ViewHolder
    {
        TextView filename;
    }
}
