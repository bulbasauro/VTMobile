package com.bulbasauro.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bulbasauro.vtmobile.R;


public class ParticipeiTopicosFragment extends Fragment {

    private ListView listView;

    public ParticipeiTopicosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participei_topicos, container, false);

        listView = (ListView) view.findViewById(R.id.listView_pessoal_participei);

        return view;
    }
}
