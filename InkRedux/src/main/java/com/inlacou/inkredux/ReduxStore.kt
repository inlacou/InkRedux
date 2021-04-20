package com.inlacou.inkredux;

import io.reactivex.rxjava3.subjects.PublishSubject

interface ReduxStore <State: ReduxState, Action: ReduxAction> {
  fun applyAction(action: Action)
  fun applyReducers(state: State, action: Action): State
  fun applyMiddleware(state: State, action: Action, dispatchAction: DispatchAction<Action>)
  fun getActionHistory(): List<Pair<Long, Action>>
  fun getExhaustiveActionHistory(): List<Triple<Long, Action, Boolean>>
  fun getSubject(): PublishSubject<State>
  fun getActionHistorySubject(): PublishSubject<List<Pair<Long, Action>>>
  fun getExhaustiveActionHistorySubject(): PublishSubject<List<Triple<Long, Action, Boolean>>>
  fun addSubscriber(subscriber: ReduxStoreSubscriber<State>): Boolean
  fun removeSubscriber(subscriber: ReduxStoreSubscriber<State>): Boolean
  val currentState: State
}

