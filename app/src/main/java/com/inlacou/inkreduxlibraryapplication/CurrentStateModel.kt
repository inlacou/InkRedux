package com.inlacou.inkreduxlibraryapplication

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy

class CurrentStateModel: ViewModel() {
	val mValueLiveData = LiveDataReactiveStreams.fromPublisher(GlobalStore.getSubject().map { it.value }.toFlowable(BackpressureStrategy.BUFFER))
}