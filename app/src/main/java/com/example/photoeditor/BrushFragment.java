package com.example.photoeditor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.example.photoeditor.Adapter.ColorAdapter;
import com.example.photoeditor.Interface.BrushFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrushFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrushFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListener {

    SeekBar seekBar_brush_size, seekBar_opacity_size;
    RecyclerView recycler_color;
    ToggleButton btn_brush_state;
    ColorAdapter colorAdapter;

    BrushFragmentListener listener;

    static BrushFragment instance;

    public static BrushFragment getInstance(){
        if(instance == null)
            instance = new BrushFragment();
        return instance;
    }

    public void setListener(BrushFragmentListener listener){
        this.listener  = listener;
    }

    public BrushFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_brush, container, false);

        seekBar_brush_size = (SeekBar)itemView.findViewById(R.id.seekbar_brush_size);
        seekBar_opacity_size = (SeekBar)itemView.findViewById(R.id.seekbar_brush_opacity);
        btn_brush_state = (ToggleButton)itemView.findViewById(R.id.btn_brush_state);
        recycler_color = (RecyclerView) itemView.findViewById(R.id.recycler_color);
        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        colorAdapter = new ColorAdapter(getContext(),genColorList(), this);
        recycler_color.setAdapter(colorAdapter);

         //Event
        seekBar_opacity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushOpacityChangeListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onBrushSizeChangeListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btn_brush_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onBrushStateChangeListener(isChecked);
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
    public void onColorSelected(int color){
        listener.onBrushColorChangeListener(color);
    }
}