package com.evgo.sample.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.SpaceLaunchesQuery
import com.apollographql.apollo.SpaceLaunchesQuery.Data
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.evgo.sample.EvgoApp
import com.evgo.sample.utils.StringUtils
import com.evgo.sample.utils.StringUtils.Companion
import timber.log.Timber

/**
 * SearchLiveData manages the API data handling
 * Allowing to decouple the API logic from the UI layer
 */
class SearchLiveData(application: EvgoApp) : LiveData<Data>(), ViewModelInterface {

  private var evgoApp: EvgoApp = application
  private var error: Any? = null

  fun fetchSpaceLaunches(missionName: String = Companion.EMPTY, rocketName: String = Companion.EMPTY,
      launchYear: String = Companion.EMPTY) {

    val builder: ApolloQueryCall<Data>? = buildSearchQuery(missionName, rocketName, launchYear)

    builder?.enqueue(object : ApolloCall.Callback<Data>() {

      override fun onFailure(e: ApolloException) {
        Timber.d("failure: %s", e.message.toString())
        postValue(null)
      }

      override fun onResponse(response: Response<Data>) {
        Timber.d("success:  %s", response.data()?.launches())
        postValue(response.data())
      }
    })
  }

  private fun buildSearchQuery(
      missionName: String = StringUtils.EMPTY, rocketName: String = StringUtils.EMPTY,
      launchYear: String = StringUtils.EMPTY): ApolloQueryCall<Data>? {
    return evgoApp.getApolloClient()?.query(SpaceLaunchesQuery
        .builder()
        .missionName(missionName)
        .rocketName(rocketName)
        .launchYear(launchYear)
        .limit(20)
        .build())
  }

  override fun setError(error: Any?) {
    this.error = error
  }

  override fun getError(): Any? {
    return error
  }

}