package com.evgo.sample.utils

import android.view.View

/**
 * ViewUtility for generic view helpers
 */

class ViewUtils{

  companion object{
    fun toggleProgressIndicator(view: View?,show: Boolean = false) {
      when {
        show -> view?.visibility = View.VISIBLE
        else -> view?.visibility = View.GONE
      }
    }
  }


}
