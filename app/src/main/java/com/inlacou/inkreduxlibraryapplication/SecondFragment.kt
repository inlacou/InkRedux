package com.inlacou.inkreduxlibraryapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import io.reactivex.rxjava3.disposables.Disposable

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
	
	private val disposables = mutableListOf<Disposable?>()
	private var tv: TextView? = null
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_second, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		tv = view.findViewById(R.id.textview_value)
		
		disposables.add(GlobalStore.getSubject().map { it.value }.subscribe {
			tv?.text = it.toString()
		})
		tv?.text = GlobalStore.state.value.toString()
		
		view.findViewById<Button>(R.id.button_second).setOnClickListener {
			findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		disposables.forEach { it?.dispose() }
	}
}