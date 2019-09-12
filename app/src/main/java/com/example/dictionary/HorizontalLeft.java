package com.example.dictionary;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalLeft extends ListFragment{


    public String showList[] = {"a","b","c","d","e","f","g","h","i","j"};
    public LayoutInflater layoutInflater;
    public static String TAG = "LeftFragment";



    public HorizontalLeft() {
        // Required empty public constructor
    }

    public static HorizontalLeft newInstance(String str){
        HorizontalLeft horizontalLeft = new HorizontalLeft();
        Bundle bundle = new Bundle();
        bundle.putString(TAG,str);
        horizontalLeft.setArguments(bundle);
        return horizontalLeft;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = getActivity().getLayoutInflater();
        this.setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,showList));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizontal_left, container, false);
        return view;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        HorizontalMain horizontalMain = (HorizontalMain)getParentFragment();
        HorizontalRight horizontalRight = (HorizontalRight)horizontalMain.getChildFragmentManager().findFragmentByTag("RightFragment");
        try{
            horizontalRight.setTV(String.valueOf(position));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
