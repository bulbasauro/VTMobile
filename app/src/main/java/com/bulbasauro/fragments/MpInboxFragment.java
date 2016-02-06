package com.bulbasauro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bulbasauro.vtmobile.R;


public class MpInboxFragment extends Fragment {

    private ListView listViewInbox;

    public MpInboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mp_inbox, container, false);

        listViewInbox = (ListView) view.findViewById(R.id.listView_mp_inbox);

        return view;
    }
}
