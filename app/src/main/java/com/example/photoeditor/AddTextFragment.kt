package com.example.photoeditor

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.ColorAdapter
import com.example.photoeditor.Adapter.ColorAdapter.ColorAdapterListener
import com.example.photoeditor.Adapter.FontAdapter
import com.example.photoeditor.Adapter.FontAdapter.FontAdapterClickListener
import com.example.photoeditor.Interface.AddTextFragmentListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class AddTextFragment : BottomSheetDialogFragment(), ColorAdapterListener, FontAdapterClickListener {
    var colorSelected = Color.parseColor("#000000")

    var edt_add_text: EditText? = null
    var recycler_color: RecyclerView? = null
    var recycler_font: RecyclerView? = null
    var btn_add_text: Button? = null
    var typefaceSelected = Typeface.DEFAULT
    var listener: AddTextFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val itemView = inflater.inflate(R.layout.fragment_add_text, container, false)
        edt_add_text = itemView.findViewById<View?>(R.id.edt_add_text) as EditText?
        recycler_color = itemView.findViewById<View?>(R.id.recycler_color) as RecyclerView?
        btn_add_text = itemView.findViewById<View?>(R.id.btn_add_text) as Button?
        recycler_color?.setHasFixedSize(true)
        recycler_color?.setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false))
        recycler_font = itemView.findViewById<View?>(R.id.recycler_font) as RecyclerView?
        recycler_font?.setHasFixedSize(true)
        recycler_font?.setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false))
        val colorAdapter = ColorAdapter(context!!, genColorList(), this)
        recycler_color?.setAdapter(colorAdapter)
        val fontAdapter = FontAdapter(context, this)
        recycler_font?.setAdapter(fontAdapter)

        //event
        btn_add_text?.setOnClickListener { listener?.onAddTextButtonClick(typefaceSelected, edt_add_text?.text.toString(), colorSelected) }
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
        colorSelected = color
    }

    override fun onFontSelected(fontName: String?) {
        typefaceSelected = Typeface.createFromAsset(context?.assets, StringBuilder("fonts/")
                .append(fontName).toString())
    }

    companion object {
        private var instance: AddTextFragment? = null
        fun getInstance(): AddTextFragment? {
            if (instance == null) instance = AddTextFragment()
            return instance
        }

        fun setListener(addTextFragment: AddTextFragment, listener: AddTextFragmentListener?) {
            addTextFragment.listener = listener
        }
    }
}