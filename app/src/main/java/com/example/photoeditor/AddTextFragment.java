package com.example.photoeditor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.photoeditor.Adapter.ColorAdapter;
import com.example.photoeditor.Interface.AddTextFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddTextFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener
{
    int colorSelected = Color.parseColor("#000000");

    AddTextFragmentListener listener;

    EditText edt_add_text;
    RecyclerView recycler_color;
    Button btn_add_text;

    public void setListener(AddTextFragmentListener listener) {
        this.listener = listener;
    }

    static AddTextFragment instance;

    public static AddTextFragment getInstance(){
        if(instance == null)
            instance = new AddTextFragment();
        return instance;
    }

    public AddTextFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View itemView = inflater.inflate(R.layout.fragment_add_text, container, false);

        edt_add_text = (EditText)itemView.findViewById(R.id.edt_add_text);
        recycler_color = (RecyclerView)itemView.findViewById(R.id.recycler_color);
        btn_add_text = (Button)itemView.findViewById(R.id.btn_add_text);
        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        ColorAdapter colorAdapter = new ColorAdapter(getContext(),genColorList(),this);
        recycler_color.setAdapter(colorAdapter);

        //event
        btn_add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAddTextButtonClick(edt_add_text.getText().toString(),colorSelected);
            }
        });

        return itemView;
    }

    private List<Integer> genColorList() {
        List<Integer> colorList = new ArrayList<>();

        colorList.add(Color.parseColor("#93bca8"));
        colorList.add(Color.parseColor("#bc93a7"));
        colorList.add(Color.parseColor("#b8d6fd"));
        colorList.add(Color.parseColor("#ffe8e0"));
        colorList.add(Color.parseColor("#eae4d3"));
        colorList.add(Color.parseColor("#987dc5"));
        colorList.add(Color.parseColor("#c9658f"));
        colorList.add(Color.parseColor("#dbd4bb"));
        colorList.add(Color.parseColor("#c0d6e4"));
        colorList.add(Color.parseColor("#c39797"));
        colorList.add(Color.parseColor("#dbd4bb"));
        colorList.add(Color.parseColor("#6da7a7"));
        colorList.add(Color.parseColor("#a96e71"));
        colorList.add(Color.parseColor("#df6b77"));
        colorList.add(Color.parseColor("#42a7a4"));
        colorList.add(Color.parseColor("#81d8d0"));
        colorList.add(Color.parseColor("#f6546a"));
        colorList.add(Color.parseColor("#c0d6e4"));
        colorList.add(Color.parseColor("#c39797"));
        colorList.add(Color.parseColor("#b0e0e6"));
        colorList.add(Color.parseColor("#d6893a"));
        colorList.add(Color.parseColor("#2bfe72"));
        colorList.add(Color.parseColor("#000000"));
        colorList.add(Color.parseColor("#003333"));
        colorList.add(Color.parseColor("#f8f8ff"));
        colorList.add(Color.parseColor("#35354f"));
        colorList.add(Color.parseColor("#ffe4c4"));
        colorList.add(Color.parseColor("#838b8b"));
        colorList.add(Color.parseColor("#c1cdcd"));
        colorList.add(Color.parseColor("#e0eeee"));
        colorList.add(Color.parseColor("#f0ffff"));
        colorList.add(Color.parseColor("#458b74"));
        colorList.add(Color.parseColor("#76eec6"));
        colorList.add(Color.parseColor("#8b8378"));
        colorList.add(Color.parseColor("#cdc0b0"));
        colorList.add(Color.parseColor("#eedfcc"));
        colorList.add(Color.parseColor("#ffefdb"));
        colorList.add(Color.parseColor("#987dc5"));
        colorList.add(Color.parseColor("#c9658f"));

        return colorList;
    }

    @Override
    public void onColorSelected(int color)
    {
        colorSelected = color;
    }
}