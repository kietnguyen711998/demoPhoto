package com.example.photoeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.EmojiAdapter
import com.example.photoeditor.Adapter.EmojiAdapter.EmojiAdapterListener
import com.example.photoeditor.Interface.EmojiFragmentListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.PhotoEditor

class EmojiFragment : BottomSheetDialogFragment(), EmojiAdapterListener {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var recyler_emoji: RecyclerView? = null
    var listener: EmojiFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val itemView = inflater.inflate(R.layout.fragment_emoji, container, false)
        recyler_emoji = itemView.findViewById<View?>(R.id.recycler_emoji) as RecyclerView?
        recyler_emoji?.setHasFixedSize(true)
        recyler_emoji?.layoutManager = GridLayoutManager(activity, 5)
        val adapter = EmojiAdapter(context!!, PhotoEditor.getEmojis(context), this)
        recyler_emoji?.setAdapter(adapter)
        return itemView
    }

    override fun onEmojiItemSelected(emoji: String?) {
        listener?.onEmojiSelected(emoji!!)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"
        private var instance: EmojiFragment? = null
        fun getInstance(): EmojiFragment? {
            if (instance == null) instance = EmojiFragment()
            return instance
        }

        fun setListener(emojiFragment: EmojiFragment, listener: EmojiFragmentListener?) {
            emojiFragment.listener = listener
        }
    }
}