package com.example.handychat.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handychat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobRequestList extends Fragment {


    public JobRequestList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_request_list, container, false);
    }

    public static JobRequestList newInstance() {
        JobRequestList fragment = new JobRequestList();
        return fragment;
    }

}
