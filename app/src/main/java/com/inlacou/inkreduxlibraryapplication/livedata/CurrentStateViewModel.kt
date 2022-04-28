package com.inlacou.inkreduxlibraryapplication.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.inlacou.inkreduxlibraryapplication.GlobalStore

class CurrentStateViewModel: ViewModel() {
	val state: LiveData<Int> = Transformations.map(GlobalStore.getLiveData()) { it.value }
}