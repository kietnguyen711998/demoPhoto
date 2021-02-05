package com.example.photoeditor.Interface


interface BrushFragmentListener {
    fun onBrushSizeChangeListener(size: Float)
    fun onBrushOpacityChangeListener(opacity: Int)
    fun onBrushColorChangeListener(color: Int)
    fun onBrushStateChangeListener(isEraser: Boolean)
}