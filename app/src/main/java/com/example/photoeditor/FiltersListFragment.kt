package com.example.photoeditor

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditor.Adapter.ThumbnailAdapter
import com.example.photoeditor.Interface.FiltersListFragmentListener
import com.example.photoeditor.MainActivity.Companion.pictureName
import com.example.photoeditor.Utils.BitmapUtils
import com.example.photoeditor.Utils.SpacesItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import java.util.*

class FiltersListFragment : BottomSheetDialogFragment(), FiltersListFragmentListener {
    var recyclerView: RecyclerView? = null
    var adapter: ThumbnailAdapter? = null
    var thumbnailItems: MutableList<ThumbnailItem>? = null
    var listener: FiltersListFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val itemView = inflater.inflate(R.layout.fragment_filters_list, container, false)
        thumbnailItems = ArrayList()
        adapter = ThumbnailAdapter(thumbnailItems as ArrayList<ThumbnailItem>, this, requireContext())
        recyclerView = itemView.findViewById<View?>(R.id.recycler_view) as RecyclerView?
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics) as Int
        recyclerView?.addItemDecoration(SpacesItemDecoration(space))
        recyclerView?.adapter = adapter
        displayThumbnail(bitmap)
        return itemView
    }

    private fun displayThumbnail(bitmap: Bitmap?) {
        val r = Runnable {
            val thumbImg: Bitmap? = if (bitmap == null) BitmapUtils.getBitmapFromAssets(requireContext(), pictureName!!, 100, 100) else Bitmap.createScaledBitmap(bitmap, 100, 100, false)
            if (thumbImg == null) return@Runnable
            ThumbnailsManager.clearThumbs()
            thumbnailItems?.clear()
            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImg
            thumbnailItem.filterName = "Normal"
            ThumbnailsManager.addThumb(thumbnailItem)
            val filters = FilterPack.getFilterPack(activity)
            for (filter in filters) {
                val tI = ThumbnailItem()
                tI.image = thumbImg
                tI.filter = filter
                tI.filterName = filter.name
                ThumbnailsManager.addThumb(tI)
            }
            thumbnailItems?.addAll(ThumbnailsManager.processThumbs(activity))
            activity?.runOnUiThread { adapter?.notifyDataSetChanged() }
        }
        Thread(r).start()
    }

    override fun onFilterSelected(filter: Filter) {
        listener?.onFilterSelected(filter)
    }

    companion object {
        var instance: FiltersListFragment? = null
        var bitmap: Bitmap? = null
        fun getInstance(bitmapSave: Bitmap?): FiltersListFragment? {
            bitmap = bitmapSave
            if (instance == null) {
                instance = FiltersListFragment()
            }
            return instance
        }

        fun setListener(filtersListFragment: FiltersListFragment, listener: FiltersListFragmentListener?) {
            filtersListFragment.listener = listener
        }
    }
}