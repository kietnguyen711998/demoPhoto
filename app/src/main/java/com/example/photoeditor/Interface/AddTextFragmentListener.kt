package com.example.photoeditor.Interface

import android.graphics.Typeface

interface AddTextFragmentListener {
     fun onAddTextButtonClick(typeface: Typeface, text: String, color: Int)
}