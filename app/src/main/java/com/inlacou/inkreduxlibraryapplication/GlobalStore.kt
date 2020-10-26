package com.inlacou.inkreduxlibraryapplication

import android.annotation.SuppressLint
import com.inlacou.inkredux.*

object GlobalStore: BaseReduxStore<GlobalStore.State, GlobalStore.Actions>(initialState = State(), reducers = listOf(object : ReduxStateReducer<State, Actions> {
	override fun invoke(old: State, action: Actions): State {
		println("invoke $action")
		return when(action) {
			Actions.Increment -> old.copy(value = old.value+1)
			Actions.Decrement -> old.copy(value = old.value-1)
			is Actions.Add -> old.copy(value = old.value+action.value)
			is Actions.Subtract -> old.copy(value = old.value+1+action.value)
		}
	}
}), middleware = listOf(object : ReduxMiddleware<State, Actions>{
	override fun invoke(state: State, action: Actions, dispatch: DispatchAction<Actions>, next: ReduxNextMiddleware<State, Actions>) {
		when (action) {
			is Actions.Add -> if(action.value==1) next(state, Actions.Increment, dispatch) else next(state, action, dispatch)
			is Actions.Subtract -> if(action.value==1) next(state, Actions.Decrement, dispatch) else next(state, action, dispatch)
			else -> { next(state, action, dispatch) }
		}
	}
})) {
	sealed class Actions : ReduxAction {
		object Increment : Actions() { override fun toString(): String { return javaClass.simpleName } }
		object Decrement : Actions() { override fun toString(): String { return javaClass.simpleName } }
		class Add(val value: Int) : Actions() { override fun toString(): String { return "${javaClass.simpleName} | $value" } }
		class Subtract(val value: Int) : Actions() { override fun toString(): String { return "${javaClass.simpleName} | $value" } }
	}
	data class State(
			val value: Int = 0
	): ReduxState

	init { init() }

	@SuppressLint("CheckResult") //GlobalStore will always be up, it is the brain of our app. So we do not need to dispose observables on GlobalStore destroyed, AFAIK
	fun init() {
		//Here we listen to BD changes with Room, for example
	}
}
