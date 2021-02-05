package com.example.photoeditor.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.EmojiAdapter.EmojiViewHolder
import com.example.photoeditor.R
import io.github.rockerhieu.emojicon.EmojiconTextView

class EmojiAdapter(var context: Context, var emojiList: MutableList<String>, var listener: EmojiAdapterListener) : RecyclerView.Adapter<EmojiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false)
        return EmojiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.emoji_text_view?.text = emojiList[position]
    }

    override fun getItemCount(): Int {
        return emojiList.size
    }

    inner class EmojiViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var emoji_text_view: EmojiconTextView? = itemView?.findViewById<View?>(R.id.emoji_text_view) as EmojiconTextView?

        init {
            itemView?.setOnClickListener { listener.onEmojiItemSelected(emojiList.get(adapterPosition)) }
        }
    }

    interface EmojiAdapterListener {
        fun onEmojiItemSelected(emoji: String?)
    }

}