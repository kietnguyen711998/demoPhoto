package com.example.photoeditor.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.ThumbnailAdapter.MyViewBolder
import com.example.photoeditor.FiltersListFragment
import com.example.photoeditor.R
import com.zomato.photofilters.utils.ThumbnailItem

class ThumbnailAdapter(private val thumbnailItems: MutableList<ThumbnailItem>, private val listener: FiltersListFragment, private val context: Context) : RecyclerView.Adapter<MyViewBolder?>() {
    private var selectedIndex = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewBolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.thumbnail_item, parent, false)
        return MyViewBolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewBolder, position: Int) {
        val thumbnailItem = thumbnailItems[position]
        holder.thumbnail?.setImageBitmap(thumbnailItem.image)
        holder.thumbnail?.setOnClickListener {
            listener.onFilterSelected(thumbnailItem.filter)
            selectedIndex = position
            notifyDataSetChanged()
        }
        holder.filter_name?.text = thumbnailItem.filterName
        if (selectedIndex == position) holder.filter_name?.setTextColor(ContextCompat.getColor(context, R.color.selected_filter)) else holder.filter_name?.setTextColor(ContextCompat.getColor(context, R.color.normal_filter))
    }

    override fun getItemCount(): Int {
        return thumbnailItems.size
    }

    inner class MyViewBolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView? = itemView.findViewById<View?>(R.id.thumbnail) as ImageView?
        var filter_name: TextView? = itemView.findViewById<View?>(R.id.filter_name) as TextView?

    }

}