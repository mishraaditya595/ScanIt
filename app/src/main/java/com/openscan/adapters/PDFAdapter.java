package com.openscan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openscan.R;

import java.io.File;
import java.util.ArrayList;

public class PDFAdapter extends ArrayAdapter<File> {

    Context context;
    ArrayList<File> fileList;
    ViewHolder viewHolder;


    public PDFAdapter(Context context, ArrayList<File> fileList) {
        super(context, R.layout.adapter_pdf, fileList);
        this.context = context;
        this.fileList = fileList;
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
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.filename = view.findViewById(R.id.filename_TV);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.filename.setText(fileList.get(position).getName());
        return view;
    }

    public class ViewHolder
    {
        TextView filename;
    }
}
