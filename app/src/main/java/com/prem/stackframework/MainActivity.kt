package com.prem.stackframework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.prem.stackframework.databinding.ActivityMainBinding
import com.prem.stackframework.databinding.ItemExpandedBinding
import com.prem.stackframework.model.StackItemData
import com.prem.stackframework.stackframework.StackAdapter
import com.prem.stackframework.viewmodel.StackViewModel
import com.prem.stackframework.stackframework.StackView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StackViewModel by viewModels()
    private lateinit var expandedBinding: ItemExpandedBinding
    private var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*       Handel API Data by Viewmodel

                viewModel.stackData.observe(this, Observer { stackData ->
                    binding.stackView.removeAllViews()

                    stackData.forEach { item ->
                        // Inflate the collapsed layout for each item
                        val collapsedBinding = ItemCollapsedBinding.inflate(layoutInflater, binding.stackView, false)
                        collapsedBinding.tvAmount.text = item.amount

                        // Inflate the expanded layout for each item and set data
                        val expandedBinding = ItemExpandedBinding.inflate(layoutInflater, binding.stackView, false)
                        expandedBinding.tvAmountExpanded.text = item.amount
                        expandedBinding.tvEmiDetails.text = item.emi

                        // Add collapsed view to StackView, setting it up to expand on click
                        binding.stackView.addStackItem(collapsedBinding.root)
                    }
                })
        */

        // Set up the toggle listener to perform operation when toggle state change
        binding.stackView.onViewToggleListener = object : StackView.OnViewToggleListener {
            override fun onViewExpanded(position: Int) {
                // observe and preform operation on view Expend
            }

            override fun onViewCollapsed(position: Int) {
                // observe and preform operation on view Collapse
            }
        }


        val stackData = mutableListOf(
            StackItemData(
                1,
                "Loan Amount : ₹150,000",
                "EMI: ₹4,247 / month for 12 months",
                "kotak Mahindra Bank"
            )
        )


        binding.closeAppBtn.setOnClickListener {
            onDestroy()
        }

        binding.aboutBtn.setOnClickListener {
            Toast.makeText(this, "Perform operation for about app", Toast.LENGTH_SHORT).show()

        }


        // Add a callback to the Activity's onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this) {
            if (!binding.stackView.collapseAllViews()) {
                super.finish()
            }
        }

        // hard codded implementation due to lack of moc API
        var isButtonClickedZero = false
        var isButtonClickedFirst = false
        var isButtonClickedSecond = false

        binding.stackView.setAdapter(object : StackAdapter {
            override fun onCreateView(parent: ViewGroup, viewType: Int): Pair<View, View> {
                val collapsedView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_collapsed, parent, false)
                val expandedView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_expanded, parent, false)
                return Pair(collapsedView, expandedView)
            }

            override fun onBindView(collapsedView: View, expandedView: View, position: Int) {
                val item = stackData[position]
                collapsedView.findViewById<TextView>(R.id.tv_amount).text = item.amount

                expandedView.findViewById<TextView>(R.id.tv_amount_expanded).text = item.amount
                expandedView.findViewById<TextView>(R.id.tv_emi_details).text = item.emi
                expandedView.findViewById<ImageView>(R.id.collpase_view_down_btn)
                    .setOnClickListener {
                        binding.stackView.collapseView(expandedView, collapsedView)
                    }


                val operationBtn = expandedView.findViewById<Button>(R.id.operation_btn)
                operationBtn.setOnClickListener {
                    when (position) {
                        0 -> {
                            if (isButtonClickedZero) {
                                Toast.makeText(
                                    baseContext,
                                    "Can perform any task",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isButtonClickedZero = true
                                stackData.add(
                                    StackItemData(
                                        2,
                                        "Loan Amount : ₹120,000",
                                        "EMI: ₹4,247 / month for 12 months",
                                        "UCO Bank"
                                    )
                                )
                                binding.stackView.addNewItem()
                            }
                        }

                        1 -> {
                            if (isButtonClickedFirst) {
                                Toast.makeText(
                                    baseContext,
                                    "Can perform any task",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isButtonClickedFirst = true
                                stackData.add(
                                    StackItemData(
                                        3,
                                        "Loan Amount : ₹190,000",
                                        "EMI: ₹4,247 / month for 12 months",
                                        "SBI Bank"
                                    )
                                )
                                binding.stackView.addNewItem()
                            }
                        }

                        2 -> {
                            if (isButtonClickedSecond) {
                                Toast.makeText(
                                    baseContext,
                                    "Can perform any task",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isButtonClickedSecond = true
                                stackData.add(
                                    StackItemData(
                                        4,
                                        "Loan Amount : ₹250,000",
                                        "EMI: ₹4,247 / month for 12 months",
                                        "Yes Bank"
                                    )
                                )
                                binding.stackView.addNewItem()
                            }
                        }

                        3 -> {
                            Toast.makeText(baseContext, "Max 4 View Can Add", Toast.LENGTH_SHORT)
                                .show()
                            operationBtn.visibility = View.GONE
                        }
                    }
                }
            }

            override fun getItemCount(): Int {
                return stackData.size
            }
        })
    }
}