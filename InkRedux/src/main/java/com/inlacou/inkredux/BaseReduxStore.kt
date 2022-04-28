package com.inlacou.inkredux

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class BaseReduxStore<State: ReduxState, Action: ReduxAction>(
        initialState: State,
        private val reducers: List<ReduxStateReducer<State, Action>>,
        private val middleware: List<ReduxMiddleware<State, Action>>
) : ReduxStore<State, Action> {

  private val rxPresent = try { Class.forName(PublishSubject::class.java.name); true } catch (cnfe: NoClassDefFoundError) { false }

  //LiveData style subscription
  private val liveData = MutableLiveData<State>()
  private val actionHistoryLiveData = MutableLiveData<List<Pair<Long, Action>>>()
  private val exhaustiveActionHistoryLiveData = MutableLiveData<List<Triple<Long, Action, Boolean>>>()
  override fun getLiveData(): LiveData<State> = liveData
  override fun getActionHistoryLiveData(): LiveData<List<Pair<Long, Action>>> = actionHistoryLiveData
  override fun getExhaustiveActionHistoryLiveData(): LiveData<List<Triple<Long, Action, Boolean>>> = exhaustiveActionHistoryLiveData

  //Rx style subscription
  private val mSubject by lazy { PublishSubject.create<State>() }
  private val mActionHistorySubject by lazy { PublishSubject.create<List<Pair<Long, Action>>>() }
  private val mExhaustiveActionHistorySubject by lazy { PublishSubject.create<List<Triple<Long, Action, Boolean>>>() }
  override fun getSubject(): PublishSubject<State> = mSubject
  override fun getActionHistorySubject(): PublishSubject<List<Pair<Long, Action>>> = mActionHistorySubject
  override fun getExhaustiveActionHistorySubject(): PublishSubject<List<Triple<Long, Action, Boolean>>> = mExhaustiveActionHistorySubject
  
  //Callback style subscription
  override val currentState: State
    get() = state
  private var state: State = initialState
    private set(value) {
      field = value
      subscribers.forEach { it(value) }
      if(rxPresent) mSubject.onNext(value)
      liveData.value = value
    }
  private val actionHistory = mutableListOf<Pair<Long, Action>>()
  private val exhaustiveActionHistory = mutableListOf<Triple<Long, Action, Boolean>>()
  private val subscribers = mutableSetOf<ReduxStoreSubscriber<State>>()
  override fun addSubscriber(subscriber: ReduxStoreSubscriber<State>) = subscribers.add(element = subscriber)
  override fun removeSubscriber(subscriber: ReduxStoreSubscriber<State>) = subscribers.remove(element = subscriber)
  override fun getActionHistory(): List<Pair<Long, Action>> = actionHistory.toList()
  override fun getExhaustiveActionHistory(): List<Triple<Long, Action, Boolean>> = exhaustiveActionHistory.toList()

  @Synchronized
  override fun applyAction(action: Action) {
    applyMiddleware(state, action) { newAction ->
      val newState = applyReducers(state, newAction)
      val changed = newState!==state
      if(changed) {
        state = newState
        actionHistory.add(Pair(System.currentTimeMillis(), newAction))
        if(rxPresent) mActionHistorySubject.onNext(actionHistory)
        actionHistoryLiveData.value = actionHistory
      }
      exhaustiveActionHistory.add(Triple(System.currentTimeMillis(), newAction, changed))
      if(rxPresent) mExhaustiveActionHistorySubject.onNext(exhaustiveActionHistory)
      exhaustiveActionHistoryLiveData.value = exhaustiveActionHistory
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