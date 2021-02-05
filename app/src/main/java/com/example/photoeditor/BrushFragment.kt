package com.example.photoeditor

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.ToggleButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.ColorAdapter
import com.example.photoeditor.Adapter.ColorAdapter.ColorAdapterListener
import com.example.photoeditor.Interface.BrushFragmentListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class BrushFragment : BottomSheetDialogFragment(), ColorAdapterListener {
    var seekBar_brush_size: SeekBar? = null
    var seekBar_opacity_size: SeekBar? = null
    var recycler_color: RecyclerView? = null
    var btn_brush_state: ToggleButton? = null
    var colorAdapter: ColorAdapter? = null
    var listener: BrushFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val itemView = inflater.inflate(R.layout.fragment_brush, container, false)
        seekBar_brush_size = itemView.findViewById<View?>(R.id.seekbar_brush_size) as SeekBar?
        seekBar_opacity_size = itemView.findViewById<View?>(R.id.seekbar_brush_opacity) as SeekBar?
        btn_brush_state = itemView.findViewById<View?>(R.id.btn_brush_state) as ToggleButton?
        recycler_color = itemView.findViewById<View?>(R.id.recycler_color) as RecyclerView?
        recycler_color?.setHasFixedSize(true)
        recycler_color?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        colorAdapter = ColorAdapter(context!!, genColorList(), this)
        recycler_color?.adapter = colorAdapter

        //Event
        seekBar_opacity_size?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener?.onBrushOpacityChangeListener(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBar_brush_size?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener?.onBrushSizeChangeListener(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        btn_brush_state?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> listener?.onBrushStateChangeListener(isChecked) })
        return itemView
    }

    private fun genColorList(): MutableList<Int> {
        val colorList: MutableList<Int> = ArrayList()
        colorList.add(Color.parseColor("#93bca8"))
        colorList.add(Color.parseColor("#bc93a7"))
        colorList.add(Color.parseColor("#b8d6fd"))
        colorList.add(Color.parseColor("#ffe8e0"))
        colorList.add(Color.parseColor("#eae4d3"))
        colorList.add(Color.parseColor("#987dc5"))
        colorList.add(Color.parseColor("#c9658f"))
        colorList.add(Color.parseColor("#dbd4bb"))
        colorList.add(Color.parseColor("#c0d6e4"))
        colorList.add(Color.parseColor("#c39797"))
        colorList.add(Color.parseColor("#dbd4bb"))
        colorList.add(Color.parseColor("#6da7a7"))
        colorList.add(Color.parseColor("#a96e71"))
        colorList.add(Color.parseColor("#df6b77"))
        colorList.add(Color.parseColor("#42a7a4"))
        colorList.add(Color.parseColor("#81d8d0"))
        colorList.add(Color.parseColor("#f6546a"))
        colorList.add(Color.parseColor("#c0d6e4"))
        colorList.add(Color.parseColor("#c39797"))
        colorList.add(Color.parseColor("#b0e0e6"))
        colorList.add(Color.parseColor("#d6893a"))
        colorList.add(Color.parseColor("#2bfe72"))
        colorList.add(Color.parseColor("#000000"))
        colorList.add(Color.parseColor("#003333"))
        colorList.add(Color.parseColor("#f8f8ff"))
        colorList.add(Color.parseColor("#35354f"))
        colorList.add(Color.parseColor("#ffe4c4"))
        colorList.add(Color.parseColor("#838b8b"))
        colorList.add(Color.parseColor("#c1cdcd"))
        colorList.add(Color.parseColor("#e0eeee"))
        colorList.add(Color.parseColor("#f0ffff"))
        colorList.add(Color.parseColor("#458b74"))
        colorList.add(Color.parseColor("#76eec6"))
        colorList.add(Color.parseColor("#8b8378"))
        colorList.add(Color.parseColor("#cdc0b0"))
        colorList.add(Color.parseColor("#eedfcc"))
        colorList.add(Color.parseColor("#ffefdb"))
        colorList.add(Color.parseColor("#987dc5"))
        colorList.add(Color.parseColor("#c9658f"))
        return colorList
    }

    override fun onColorSelected(color: Int) {
        listener?.onBrushColorChangeListener(color)
    }

    companion object {
        private var instance: BrushFragment? = null
        fun getInstance(): BrushFragment? {
            if (instance == null) instance = BrushFragment()
            return instance
        }

        fun setListener(brushFragment: BrushFragment, listener: BrushFragmentListener?) {
            brushFragment.listener = listener
        }
    }
}