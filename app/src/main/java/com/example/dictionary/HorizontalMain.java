package com.example.dictionary;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalMain extends Fragment{


    Fragment horizontalLeft = new HorizontalLeft();
    Fragment horizontalRight = new HorizontalRight();
    Bundle bundle = new Bundle();

    public HorizontalMain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizontal_main, container, false);
        getChildFragmentManager().beginTransaction().replace(R.id.hl,horizontalLeft,"LeftFragment").commit();
        getChildFragmentManager().beginTransaction().replace(R.id.hr,horizontalRight,"RightFragment").commit();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
