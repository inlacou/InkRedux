package com.inlacou.inkreduxlibraryapplication.utils

import android.content.Context
import com.inlacou.inkreduxlibraryapplication.R
import java.util.*

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
