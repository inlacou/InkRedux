package com.inlacou.inkreduxlibraryapplication.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.inlacou.inkreduxlibraryapplication.GlobalStore

class HistoryViewModel: ViewModel() {
	val state: LiveData<List<Pair<Long, GlobalStore.Actions>>> = GlobalStore.getActionHistoryLiveData()
}