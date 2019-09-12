package com.example.dictionary;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalRight extends Fragment {

    TextView tv;
    public LayoutInflater layoutInflater;
    public static final String TAG = "RightFragment";


    public HorizontalRight() {
        // Required empty public constructor
    }


    public static HorizontalRight newInstance(String str){
        HorizontalRight horizontalRight = new HorizontalRight();
        Bundle bundle = new Bundle();
        bundle.putString(TAG,str);
        horizontalRight.setArguments(bundle);
        return horizontalRight;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = getActivity().getLayoutInflater();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_horizontal_right, container, false);
        tv = view.findViewById(R.id.show);
        setTV("umaru!!");

        return view;
    }


    public void setTV(String str){
        tv.setText(tv.getText().toString()+"\n"+str);
    }

}
