package com.evgo.sample.viewmodel

import androidx.lifecycle.ViewModel
import com.evgo.sample.EvgoApp

/**
 * The SearchViewModel holds the reference to SearchLiveData
 * Key advantage of ViewModel it survives Android configuration changes
 * so data is preserved and restored out of the box.
 * ViewModel are part of Android architectural components
 */
class SearchViewModel : ViewModel() {

  var searchLiveData: SearchLiveData? = null

  fun setSearchLiveData(app: EvgoApp) {
    searchLiveData = SearchLiveData(app)
  }

}

