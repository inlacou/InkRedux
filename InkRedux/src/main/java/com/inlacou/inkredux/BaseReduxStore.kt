package com.inlacou.inkredux

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

abstract class BaseReduxStore<State: ReduxState, Action: ReduxAction>(
        initialState: State,
        private val reducers: List<ReduxStateReducer<State, Action>>,
        private val middleware: List<ReduxMiddleware<State, Action>>
) : ReduxStore<State, Action> {

  private val rxPresent = try { Class.forName(PublishSubject::class.java.name); println("rx present"); true } catch (cnfe: NoClassDefFoundError) { println("rx not present"); false }
  private val liveDataPresent = try { Class.forName(LiveData::class.java.name); println("liveData present"); true } catch (cnfe: NoClassDefFoundError) { println("liveData not present"); false }
  private val flowPresent = try { Class.forName(Flow::class.java.name); println("flow present"); true } catch (cnfe: NoClassDefFoundError) { println("flow not present"); false }

  //Coroutine style subscription
  private val mFlow by lazy { MutableSharedFlow<State>() }
  private val mActionHistoryFlow by lazy { MutableSharedFlow<List<Pair<Long, Action>>>() }
  private val mExhaustiveActionHistoryFlow by lazy { MutableSharedFlow<List<Triple<Long, Action, Boolean>>>() }
  override fun getFlow(): Flow<State> = mFlow
  override fun getActionHistoryFlow(): Flow<List<Pair<Long, Action>>> = mActionHistoryFlow
  override fun getExhaustiveActionHistoryFlow(): Flow<List<Triple<Long, Action, Boolean>>> = mExhaustiveActionHistoryFlow

  //LiveData style subscription
  private val liveData by lazy { MutableLiveData<State>() }
  private val actionHistoryLiveData by lazy { MutableLiveData<List<Pair<Long, Action>>>() }
  private val exhaustiveActionHistoryLiveData by lazy { MutableLiveData<List<Triple<Long, Action, Boolean>>>() }
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
      if(liveDataPresent) liveData.value = value
      if(flowPresent) mFlow.tryEmit(value)
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
        if(liveDataPresent) actionHistoryLiveData.value = actionHistory
        if(flowPresent) mActionHistoryFlow.tryEmit(actionHistory)
      }
      exhaustiveActionHistory.add(Triple(System.currentTimeMillis(), newAction, changed))
      if(rxPresent) mExhaustiveActionHistorySubject.onNext(exhaustiveActionHistory)
      if(liveDataPresent) exhaustiveActionHistoryLiveData.value = exhaustiveActionHistory
      if(flowPresent) mExhaustiveActionHistoryFlow.tryEmit(exhaustiveActionHistory)
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