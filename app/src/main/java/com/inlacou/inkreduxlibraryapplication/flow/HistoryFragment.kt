package com.inlacou.inkreduxlibraryapplication.flow

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.inlacou.inkreduxlibraryapplication.GlobalStore
import com.inlacou.inkreduxlibraryapplication.R
import com.inlacou.inkreduxlibraryapplication.utils.toCalendar
import com.inlacou.inkreduxlibraryapplication.utils.toDateTime
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HistoryFragment : Fragment() {

	private var tv: TextView? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_second, container, false)
	}

	@RequiresApi(Build.VERSION_CODES.N)
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		tv = view.findViewById(R.id.textview_value)

		lifecycleScope.launchWhenResumed {
			GlobalStore.getActionHistoryFlow()
				.catch { Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show() }
				.collect {
					Log.d("DEBUG", it.toString())
					tv?.text = it.get()
						.map { "\n${it.first.toCalendar().toDateTime(requireActivity(), showSeconds = true)}: ${it.second.javaClass.simpleName}" }
						.toString().replace("[", "").replace("]", "").replace(",", "")
				}
		}

		view.findViewById<Button>(R.id.button_second).setOnClickListener {
			findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
		}
	}
}