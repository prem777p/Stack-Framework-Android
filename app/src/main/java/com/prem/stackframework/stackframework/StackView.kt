package com.prem.stackframework.stackframework

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible


class StackView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private var adapter: StackAdapter? = null
    private val stackItems = mutableListOf<Pair<View, View>>()
    private var currentlyExpandedView: Pair<View, View>? = null
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("StackViewPrefs", Context.MODE_PRIVATE)

    var onViewToggleListener: OnViewToggleListener? = null // Listener for view toggling

    init {
        orientation = VERTICAL
    }

    fun setAdapter(adapter: StackAdapter){
        this.adapter = adapter
        populateStackItems()
    }

    // Populate stack items based on the adapter's data
    private fun populateStackItems() {
        adapter?.let {
            removeAllViews()
            stackItems.clear()

            val itemCount = adapter!!.getItemCount()
            for (position in 0 until itemCount) {
                addStackItem(position)
            }
        }
    }

    // Adds a new item to the stack
    private fun addStackItem(position: Int){
        adapter?.let {
            val (collapsedView, expandedView) = adapter!!.onCreateView(this, position)
            adapter!!.onBindView(collapsedView, expandedView, position)

            // Collapse any currently expanded view before adding a new one
            currentlyExpandedView?.let {
                val expandedIndex = stackItems.indexOf(it)
                if (expandedIndex != -1) {
                    collapseView(it.second, it.first)
                    sharedPreferences.edit().putBoolean("expanded_$expandedIndex", false).apply()
                    onViewToggleListener?.onViewCollapsed(expandedIndex) // Notify listener
                }
                currentlyExpandedView = null // Reset currently expanded view
            }

            // Set initial visibility of new item
            collapsedView.isVisible = false
            expandedView.isVisible = true
            currentlyExpandedView = Pair(collapsedView, expandedView) // Track as expanded


            // Set initial visibility of views
            collapsedView.isVisible = false
            expandedView.isVisible = true

            // Toggle views on click
            collapsedView.setOnClickListener {
                toggleView(
                    position,
                    collapsedView,
                    expandedView
                )
            }

            // Create a container for the views to handle overlapping
            val expandedViewContainer = expandedView.apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                ).apply {
                    // Apply negative top margin for overlap
                    if (stackItems.isNotEmpty()) topMargin = -40

                    elevation = (position.toFloat() + 1)* 15f
                }
            }

            val collapsedViewContainer = collapsedView.apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    // Apply negative top margin for overlap
                    if (stackItems.isNotEmpty()) topMargin = -40
                }
            }

            // Add container to the layout and track in the list
            stackItems.add(Pair(collapsedView, expandedView))
            addView(collapsedViewContainer)
            addView(expandedViewContainer)

            // Save expanded state to SharedPreferences
            sharedPreferences.edit().putBoolean("expanded_$position", true).apply()
            onViewToggleListener?.onViewExpanded(position) // Notify listener

        }
    }

    // Dynamically add a new item to the stack by notifying the adapter
    fun addNewItem() {
        adapter?.let { adapter ->
            val newItemPosition = adapter.getItemCount() - 1
            addStackItem(newItemPosition)
        }
    }

    private fun toggleView(position: Int, clickedView: View, targetView: View) {

        if (currentlyExpandedView?.second == clickedView) {
            // Collapse the current expanded view and update preference
            collapseView(clickedView, targetView)
            sharedPreferences.edit().putBoolean("expanded_$position", false).apply()
            currentlyExpandedView = null
            onViewToggleListener?.onViewCollapsed(position) // Notify listener

        } else {
            // Collapse any previously expanded view
            currentlyExpandedView?.let {
                val expandedIndex = stackItems.indexOf(it)
                if (expandedIndex != -1) {
                    collapseView(it.second, it.first)
                    sharedPreferences.edit().putBoolean("expanded_$expandedIndex", false).apply()
                    onViewToggleListener?.onViewCollapsed(expandedIndex) // Notify listener
                }
            }

            // Expand the new view and update preference
            expandView(clickedView, targetView)
            sharedPreferences.edit().putBoolean("expanded_$position", true).apply()
            currentlyExpandedView = Pair(clickedView, targetView)
            onViewToggleListener?.onViewExpanded(position) // Notify listener
        }
    }

    private fun expandView(collapsedView: View, expandedView: View) {
        collapsedView.isVisible = false
        expandedView.isVisible = true
    }

     fun collapseView(expandedView: View, collapsedView: View) {
        expandedView.isVisible = false
        collapsedView.isVisible = true
    }

    // Collapse any expanded view when the back button is pressed
    fun collapseAllViews(): Boolean {

        currentlyExpandedView?.let {
            val expandedIndex = stackItems.indexOf(it)
            if (expandedIndex != -1) {
                collapseView(it.second, it.first)
                sharedPreferences.edit().putBoolean("expanded_$expandedIndex", false).apply()
                onViewToggleListener?.onViewCollapsed(expandedIndex) // Notify listener
            }
            currentlyExpandedView = null
            return true // Indicates that a collapse was performed
        }

        return false // No view was collapsed
    }

    // Listener interface for view toggling
    interface OnViewToggleListener {
        fun onViewExpanded(position: Int)
        fun onViewCollapsed(position: Int)
    }


}

