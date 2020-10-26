package com.inlacou.inkredux

typealias ReduxNextMiddleware<State, Action> = (State, Action, DispatchAction<Action>) -> Unit
typealias ReduxMiddleware<State, Action> = (State, Action, DispatchAction<Action>, ReduxNextMiddleware<State, Action>) -> Unit