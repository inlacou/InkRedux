package com.inlacou.inkreduxlibraryapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class CurrentStateViewModel: ViewModel() {
	val state: LiveData<Int> = Transformations.map(GlobalStore.getLiveData()) { it.value }
}