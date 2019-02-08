package com.deucate.earntobank

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deucate.earntobank.alert.Alert

class HomeViewModel:ViewModel(){
    val alerts = MutableLiveData<ArrayList<Alert>>()
}