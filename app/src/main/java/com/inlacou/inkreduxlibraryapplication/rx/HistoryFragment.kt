package com.inlacou.inkreduxlibraryapplication.rx

import android.content.Context
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
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HistoryFragment : Fragment() {
	
	private var tv: TextView? = null
	private var disposables: MutableList<Disposable?> = mutableListOf()

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

		disposables.add(GlobalStore.getActionHistorySubject().subscribe({
			tv?.text = it
				.map { "\n${it.first.toCalendar().toDateTime(requireActivity(), showSeconds = true)}: ${it.second.javaClass.simpleName}" }
				.toString().replace("[", "").replace("]", "").replace(",", "")
		}, { Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show() }))

		tv?.text = GlobalStore.getActionHistory()
				.map { "\n${it.first.toCalendar().toDateTime(requireActivity(), showSeconds = true)}: ${it.second.javaClass.simpleName}" }
				.toString().replace("[", "").replace("]", "").replace(",", "")
		
		view.findViewById<Button>(R.id.button_second).setOnClickListener {
			findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
		}
	}

	fun Long.toCalendar(): Calendar = Calendar.getInstance().setMilliseconds(this)
	
	fun Calendar.setMilliseconds(millis: Long): Calendar {
		this.timeInMillis = millis
		return this }
	
	fun Calendar?.toDateTime(context: Context, separator: String = ", ", dayOfWeek: Boolean = false, monthAsNumber: Boolean = true, showSeconds: Boolean = false): String {
		if (this == null) {
			return ""
		}
		return if(dayOfWeek) {
			if(monthAsNumber){
				if(showSeconds) String.format(context.resources.getString(R.string.datetime_day_of_week_month_as_number_with_seconds), this, separator)
				else String.format(context.resources.getString(R.string.datetime_day_of_week_month_as_number), this, separator)
			}else{
				if(showSeconds) String.format(context.resources.getString(R.string.datetime_day_of_week_with_seconds), this, separator)
				else String.format(context.resources.getString(R.string.datetime_day_of_week), this, separator)
			}
		}else if(monthAsNumber) {
			if(showSeconds) String.format(context.resources.getString(R.string.datetime_month_as_number_with_seconds), this, separator)
			else String.format(context.resources.getString(R.string.datetime_month_as_number), this, separator)
		}else{
			if(showSeconds) String.format(context.resources.getString(R.string.datetime_with_seconds), this, separator)
			else String.format(context.resources.getString(R.string.datetime), this, separator)
		}
	}

	override fun onDestroy() {
		disposables.forEach { it?.dispose() }
		super.onDestroy()
	}
}