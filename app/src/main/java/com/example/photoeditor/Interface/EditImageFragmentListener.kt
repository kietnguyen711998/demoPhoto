package com.example.photoeditor.Interface


interface EditImageFragmentListener {
    fun onBrightnessChanged(brightness: Int)
    fun onSaturationChanged(saturation: Float)
    fun onConstraintChanged(constraint: Float)
    fun onEditStarted()
    fun onEditCompleted()
}