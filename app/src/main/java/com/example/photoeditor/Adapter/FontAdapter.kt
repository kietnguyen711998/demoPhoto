package com.example.photoeditor.Adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.FontAdapter.FontViewHolder
import com.example.photoeditor.R
import java.util.*

class FontAdapter(var context: Context?, var listener: FontAdapterClickListener) : RecyclerView.Adapter<FontViewHolder?>() {
    var fontList: MutableList<String>
    var row_selected = -1
    private fun loadFontList(): MutableList<String> {
        val result: MutableList<String> = ArrayList()
        result.add("arial.ttf")
        result.add("times.ttf")
        result.add("thuphap.ttf")
        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.font_item, parent, false)
        return FontViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        if (row_selected == position) holder.img_check?.visibility = View.VISIBLE else holder.img_check?.visibility = View.INVISIBLE
        val typeface = Typeface.createFromAsset(context?.assets, StringBuilder("fonts/")
                .append(fontList[position]).toString())
        holder.txt_font_name?.text = fontList[position]
        holder.txt_font_demo?.typeface = typeface
    }

    override fun getItemCount() = fontList.size

    inner class FontViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_font_name: TextView? = itemView.findViewById<View?>(R.id.txt_font_name) as TextView?
        var txt_font_demo: TextView? = itemView.findViewById<View?>(R.id.txt_font_demo) as TextView?
        var img_check: ImageView? = itemView.findViewById<View?>(R.id.img_check) as ImageView?

        init {
            itemView.setOnClickListener {
                listener.onFontSelected(fontList[adapterPosition])
                row_selected = adapterPosition
                notifyDataSetChanged()
            }
        }
    }

    interface FontAdapterClickListener {
        fun onFontSelected(fontName: String?)
    }

    init {
        fontList = loadFontList()
    }
}