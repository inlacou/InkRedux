package com.inlacou.inkreduxlibraryapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class HistoryViewModel: ViewModel() {
	val state: LiveData<List<Pair<Long, GlobalStore.Actions>>> = GlobalStore.getActionHistoryLiveData()
}