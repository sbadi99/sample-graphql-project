package com.evgo.sample.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * StringUtils for generic String helpers
 */
class StringUtils {

  companion object {

    const val YEAR_MONTH_MONTHDAY = "yyyy-MM-dd"
    const val EMPTY = ""

    fun isNumeric(args: String): Boolean {
      return args.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    fun formatDate(unixTime: String): String? {
      val date = Date(unixTime.toLong() * 1000)
      // format of the date
      val simpleDateFormat = SimpleDateFormat(YEAR_MONTH_MONTHDAY, Locale.US)
      return simpleDateFormat.format(date)
    }

    fun styleText(styleString: String, normalString: String): SpannableString {
      val spannableString = SpannableString(styleString + normalString)
      spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, styleString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      return spannableString
    }

  }

}

