package com.alansar.center.administrator.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.alansar.center.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends Fragment {


    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_permissions, container, false);
    }

}