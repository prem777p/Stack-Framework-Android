package com.prem.stackframework.stackframework

import android.view.View
import android.view.ViewGroup

interface StackAdapter {
    fun onCreateView(parent: ViewGroup, viewType: Int): Pair<View, View>
    fun onBindView(collapsedView: View, expandedView: View, position: Int)
    fun getItemCount(): Int
}
