package com.vob.scanner.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.vob.scanner.R;


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
                Toast.makeText(getActivity(), "Files Open", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}