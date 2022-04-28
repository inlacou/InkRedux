package com.inlacou.inkreduxlibraryapplication.rx

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.inlacou.inkreduxlibraryapplication.GlobalStore
import com.inlacou.inkreduxlibraryapplication.R
import io.reactivex.rxjava3.disposables.Disposable

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CurrentStateFragment : Fragment() {
	
	private var tv: TextView? = null
	private var disposables: MutableList<Disposable?> = mutableListOf()
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
		= inflater.inflate(R.layout.fragment_first, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		tv = view.findViewById(R.id.textview_value)

		disposables.add(GlobalStore.getSubject().map { it.value }.subscribe({ tv?.text = it.toString() }, { Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show() }))

		view.findViewById<Button>(R.id.button_first).setOnClickListener {
			findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
		}
	}

	override fun onDestroy() {
		disposables.forEach { it?.dispose() }
		super.onDestroy()
	}
}