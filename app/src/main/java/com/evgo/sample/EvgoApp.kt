package com.evgo.sample

import android.app.Application
import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient.Builder
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY

/**
 * EvGo application the purpose of this application is to provide SpaceX launch searches
 * The user can currently search by either entering Mission name or Rocket name or Launch Year
 * The application also uses Apollo framework to streamline code generation in terms of graphql e.t.c.
 */
class EvgoApp : Application() {

  private val BASE_URL = "https://api.spacex.land/graphql"
  private var apolloClient: ApolloClient? = null

  override fun onCreate() {
    super.onCreate()
    setUpApolloClient()
  }

  /**
   * This method setups Apollo client
   */
  private fun setUpApolloClient() {
    val logging = HttpLoggingInterceptor()
    val okHttpClient = Builder()
        .addInterceptor(logging)
        .build()
    logging.setLevel(BODY)

    apolloClient = ApolloClient.builder()
        .serverUrl(BASE_URL)
        .okHttpClient(okHttpClient)
        .build()
  }

  fun getApolloClient(): ApolloClient? {
    return apolloClient
  }

}