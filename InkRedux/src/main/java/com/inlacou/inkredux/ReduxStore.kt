package com.inlacou.inkredux;

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ReduxStore<State: ReduxState, Action: ReduxAction> {
  fun applyAction(action: Action)
  fun applyReducers(state: State, action: Action): State
  fun applyMiddleware(state: State, action: Action, dispatchAction: DispatchAction<Action>)

  val currentState: State
  fun getActionHistory(): List<Pair<Long, Action>>
  fun getExhaustiveActionHistory(): List<Triple<Long, Action, Boolean>>

  fun getSubject(): PublishSubject<State>
  fun getActionHistorySubject(): PublishSubject<List<Pair<Long, Action>>>
  fun getExhaustiveActionHistorySubject(): PublishSubject<List<Triple<Long, Action, Boolean>>>

  fun getLiveData(): LiveData<State>
  fun getActionHistoryLiveData(): LiveData<List<Pair<Long, Action>>>
  fun getExhaustiveActionHistoryLiveData(): LiveData<List<Triple<Long, Action, Boolean>>>

  fun getFlow(): Flow<State>
  fun getActionHistoryFlow(): Flow<List<Pair<Long, Action>>>
  fun getExhaustiveActionHistoryFlow(): Flow<List<Triple<Long, Action, Boolean>>>

  fun addSubscriber(subscriber: ReduxStoreSubscriber<State>): Boolean
  fun removeSubscriber(subscriber: ReduxStoreSubscriber<State>): Boolean
}

