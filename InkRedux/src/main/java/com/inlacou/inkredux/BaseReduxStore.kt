package com.inlacou.inkredux

import io.reactivex.rxjava3.subjects.PublishSubject

abstract class BaseReduxStore<State: ReduxState, Action: ReduxAction>(
        initialState: State,
        private val reducers: List<ReduxStateReducer<State, Action>>,
        private val middleware: List<ReduxMiddleware<State, Action>>
) : ReduxStore<State, Action> {
  
  //Rx style subscription
  private val subject = PublishSubject.create<State>()
  private val actionHistorySubject = PublishSubject.create<List<Pair<Long, Action>>>()
  private val exhaustiveActionHistorySubject = PublishSubject.create<List<Triple<Long, Action, Boolean>>>()
  override fun getSubject(): PublishSubject<State> = subject
  override fun getActionHistorySubject(): PublishSubject<List<Pair<Long, Action>>> = actionHistorySubject
  override fun getExhaustiveActionHistorySubject(): PublishSubject<List<Triple<Long, Action, Boolean>>> = exhaustiveActionHistorySubject
  
  //Callback style subscription
  private val subscribers = mutableSetOf<ReduxStoreSubscriber<State>>()
  override fun addSubscriber(subscriber: ReduxStoreSubscriber<State>) = subscribers.add(element = subscriber)
  override fun removeSubscriber(subscriber: ReduxStoreSubscriber<State>) = subscribers.remove(element = subscriber)
  
  override val currentState: State
    get() = state
  private var state: State = initialState
    private set(value) {
      field = value
      subscribers.forEach { it(value) }
      subject.onNext(value)
    }
  
  private val actionHistory = mutableListOf<Pair<Long, Action>>()
  private val exhaustiveActionHistory = mutableListOf<Triple<Long, Action, Boolean>>()
  override fun getActionHistory(): List<Pair<Long, Action>> = actionHistory.toList()
  override fun getExhaustiveActionHistory(): List<Triple<Long, Action, Boolean>> = exhaustiveActionHistory.toList()

  @Synchronized
  override fun applyAction(action: Action) {
    applyMiddleware(state, action) { newAction ->
      val newState = applyReducers(state, newAction)
      val changed = newState!=state
      if(changed) {
        state = newState
        actionHistory.add(Pair(System.currentTimeMillis(), newAction))
        actionHistorySubject.onNext(actionHistory)
      }
      exhaustiveActionHistory.add(Triple(System.currentTimeMillis(), newAction, changed))
      exhaustiveActionHistorySubject.onNext(exhaustiveActionHistory)
    }
  }

  @Synchronized
  override fun applyReducers(state: State, action: Action): State {
    var newState = state
    for (reducer in reducers) {
      newState = reducer(newState, action)
    }
    return newState
  }

  @Synchronized
  override fun applyMiddleware(state: State, action: Action, dispatchAction: DispatchAction<Action>) {
    next(0)(state, action, dispatchAction)
  }
  
  private fun next(index: Int): ReduxNextMiddleware<State, Action> {
    if (index == middleware.size) {
      // Last link of the chain. It just dispatches the action as is.
      return { _, action, dispatch -> dispatch.invoke(action) }
    }
    return { state, action, dispatch -> middleware[index].invoke(state, action, dispatch, next(index+1)) }
  }
}