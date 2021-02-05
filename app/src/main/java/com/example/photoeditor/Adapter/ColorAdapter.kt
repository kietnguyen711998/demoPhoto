package com.example.photoeditor.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.ColorAdapter.ColorViewholder
import com.example.photoeditor.R

class ColorAdapter(var context: Context, var colorList: MutableList<Int>, var listener: ColorAdapterListener) : RecyclerView.Adapter<ColorViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewholder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false)
        return ColorViewholder(itemView)
    }

    override fun onBindViewHolder(holder: ColorViewholder, position: Int) {
        holder.color_section?.setCardBackgroundColor(colorList[position])
    }

    override fun getItemCount() = colorList.size

    inner class ColorViewholder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var color_section: CardView? = itemView?.findViewById<View?>(R.id.color_section) as CardView?

        init {
            itemView?.setOnClickListener { listener.onColorSelected(colorList[adapterPosition]) }
        }
    }

    interface ColorAdapterListener {
        fun onColorSelected(color: Int)
    }

}