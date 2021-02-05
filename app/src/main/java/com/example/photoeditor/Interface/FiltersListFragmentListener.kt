package com.example.photoeditor.Interface

import com.zomato.photofilters.imageprocessors.Filter

interface FiltersListFragmentListener {
    fun onFilterSelected(filter: Filter)
}