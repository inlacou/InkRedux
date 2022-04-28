package com.inlacou.inkreduxlibraryapplication.livedata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.inlacou.inkreduxlibraryapplication.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CurrentStateFragment : Fragment() {
	
	private val viewModel = CurrentStateViewModel()
	private var tv: TextView? = null
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
		= inflater.inflate(R.layout.fragment_first, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		tv = view.findViewById(R.id.textview_value)

		viewModel.state.observe(requireActivity()) { tv?.text = it.toString() }

		view.findViewById<Button>(R.id.button_first).setOnClickListener {
			findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
		}
	}
}