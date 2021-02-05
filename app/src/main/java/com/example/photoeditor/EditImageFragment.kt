package com.example.photoeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.photoeditor.Interface.EditImageFragmentListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditImageFragment : BottomSheetDialogFragment(), OnSeekBarChangeListener {
    private var listener: EditImageFragmentListener? = null
    var seekbar_brightness: SeekBar? = null
    var seekbar_constraint: SeekBar? = null
    var seekbar_saturation: SeekBar? = null
    fun setListener(listener: EditImageFragmentListener?) {
        this.listener = listener
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val itemView = inflater.inflate(R.layout.fragment_edit_image, container, false)
        seekbar_brightness = itemView.findViewById<View?>(R.id.seekbar_brightness) as SeekBar?
        seekbar_constraint = itemView.findViewById<View?>(R.id.seekbar_constraint) as SeekBar?
        seekbar_saturation = itemView.findViewById<View?>(R.id.seekbar_saturation) as SeekBar?
        seekbar_brightness?.max = 200
        seekbar_brightness?.progress = 100
        seekbar_constraint?.max = 20
        seekbar_constraint?.progress = 0
        seekbar_saturation?.max = 30
        seekbar_saturation?.progress = 10
        seekbar_brightness?.setOnSeekBarChangeListener(this)
        seekbar_constraint?.setOnSeekBarChangeListener(this)
        seekbar_saturation?.setOnSeekBarChangeListener(this)
        return itemView
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        var progress = progress
        if (seekBar.id == R.id.seekbar_brightness) {
            listener?.onBrightnessChanged(progress - 100)
        } else if (seekBar.id == R.id.seekbar_constraint) {
            progress += 10
            val value = .10f * progress
            listener?.onConstraintChanged(value)
        } else if (seekBar.id == R.id.seekbar_saturation) {
            val value = .10f * progress
            listener?.onSaturationChanged(value)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        listener?.onEditStarted()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        listener?.onEditCompleted()
    }

    fun resetControls() {
        seekbar_brightness?.progress = 100
        seekbar_constraint?.progress = 0
        seekbar_saturation?.progress = 10
    }

    companion object {
        private var instance: EditImageFragment? = null
        fun getInstance(): EditImageFragment? {
            if (instance == null) instance = EditImageFragment()
            return instance
        }

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1: String? = "param1"
        private val ARG_PARAM2: String? = "param2"

        fun newInstance(param1: String?, param2: String?): EditImageFragment? {
            val fragment = EditImageFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}