package com.prem.stackframework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prem.stackframework.network.RetrofitInstance
import com.prem.stackframework.model.StackItemData
import kotlinx.coroutines.launch

class StackViewModel : ViewModel() {
    private val _stackData = MutableLiveData<List<StackItemData>>()
    val stackData: LiveData<List<StackItemData>> get() = _stackData

    init {
        fetchStackData()
    }

    private fun fetchStackData() {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getStackData()
            if (response.isSuccessful) {
                _stackData.value = response.body()?.data?.take(4) // Only take the first 4 items
            } else {
                // Handle error (display message, log, etc.)
                Log.d("Error : viewModel API Response","failed to get response ")
            }
        }
    }
}