package com.evgo.sample.ui

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.SpaceLaunchesQuery.Data
import com.apollographql.apollo.SpaceLaunchesQuery.Launch
import com.evgo.sample.EvgoApp
import com.evgo.sample.R
import com.evgo.sample.R.string
import com.evgo.sample.utils.StringUtils.Companion.EMPTY
import com.evgo.sample.utils.StringUtils.Companion.isNumeric
import com.evgo.sample.utils.ViewUtils.Companion.toggleProgressIndicator
import com.evgo.sample.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_main.empty_results
import kotlinx.android.synthetic.main.activity_main.main_progress
import kotlinx.android.synthetic.main.activity_main.search_recyclerview
import timber.log.Timber

/**
 * This Activity provides the core SpaceX Search UI
 * Utilizes search icon in Android toolbar
 * Also uses native Android SearchView widget
 */
class SpaceXSearchActivity : AppCompatActivity() {
  private var evgoApp: EvgoApp? = null
  private var searchRecyclerView: RecyclerView? = null
  private var searchAdapterSearch: SearchCardAdapter? = null
  private var searchQuery: String? = null

  private var priceListObserver: Observer<Data>? = null
  protected var searchViewModel: SearchViewModel? = null

  companion object {
    private val TAG = SpaceXSearchActivity::class.java.simpleName
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    evgoApp = application as EvgoApp
    setupUi()

    //initializing the search view model & live data observer
    initSearchViewModel()
    setUpSearchLiveDataObserver()

    //intro message on first launch
    empty_results.text = getString(R.string.intro_message)
    empty_results.visibility = View.VISIBLE
  }

  /**
   * Setting up LiveData Observer which observes all the search live data updates
   * This also populates and updates the UI accordingly,
   * when new searches are conducted by the user
   */
  private fun setUpSearchLiveDataObserver() {
    priceListObserver = Observer {
      if (it == null) {
        toggleProgressIndicator(main_progress)
        Timber.e("no search results fetched")
        resetUi()
        toggleProgressIndicator(main_progress)
        empty_results.visibility = View.VISIBLE

      }
      it?.launches()?.let { launches ->
        when {
          launches.isEmpty() -> {
            resetUi()
            toggleProgressIndicator(main_progress)
            empty_results.visibility = View.VISIBLE
          }
          else -> {
            launches.let { launchesList ->
              toggleProgressIndicator(main_progress)
              val searchResult = arrayListOf<Launch>()
              empty_results.visibility = View.GONE
              searchResult.addAll(launchesList)

              //sort search results by launch date
              searchResult.sortByDescending { date -> date.launch_date_unix().toString() }

              populateSearchUi(searchResult)
              Timber.i("success api reponse: $searchResult")

            }
          }
        }
      }
    }
    searchViewModel?.searchLiveData?.observe(this,
        priceListObserver as Observer<Data>)
  }

  /**
   * Setting up the UI with the searchRecyclerView (search list)
   */
  private fun setupUi() {
    searchRecyclerView = search_recyclerview
    searchRecyclerView?.layoutManager = LinearLayoutManager(this)
  }

  /**
   * This method handles the search query flow in the native Android search view in the toolbar
   */
  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    // Verify the action and get the query
    when (Intent.ACTION_SEARCH) {
      intent?.action -> {
        intent.getStringExtra(SearchManager.QUERY)?.also { searchQuery ->
          if (searchQuery.isNotEmpty()) {
            this.searchQuery = searchQuery
            //mission name or rocket name search
            when {
              !isNumeric(searchQuery) -> {
                showMissionOrRocketNameDialog()
              }
              else -> {
                //year search
                toggleProgressIndicator(main_progress, true)
                searchViewModel?.searchLiveData?.fetchSpaceLaunches(EMPTY, EMPTY, searchQuery)
              }
            }
          }
        }
      }
    }
  }

  /**
   * Populating the searchRecyclerView adapter with the search result data
   */
  private fun populateSearchUi(
      launchesPast: ArrayList<Launch>) {
    searchAdapterSearch = SearchCardAdapter(this, launchesPast)
    searchRecyclerView?.adapter = searchAdapterSearch
  }

  /**
   * Creating the search menu icon on the toolbar & relevant search wire-up logic
   */
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {

    val inflater = menuInflater
    inflater.inflate(R.menu.search_menu, menu)

    // Get the SearchView and set the searchable configuration
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    (menu?.findItem(R.id.search)?.actionView as SearchView).apply {
      // Assumes current activity is the searchable activity
      setSearchableInfo(searchManager.getSearchableInfo(componentName))
      setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
    }
    return true
  }


  /**
   * Resetting the UI here
   */
  private fun resetUi() {
    searchAdapterSearch?.searchResult?.clear()
    searchRecyclerView?.recycledViewPool?.clear();
    searchRecyclerView?.adapter?.notifyDataSetChanged()
  }

  /**
   * Show the dialog whether user prefers to search by mission name or rocket name
   * based on user selection we can trigger either a mission name or rocket name search to fetch results
   */
  private fun showMissionOrRocketNameDialog() {
    val options = arrayOf(getString(string.mission_name), getString(string.rocket_name))
    var selectedItem = 0
    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(string.search_options))
    builder.setSingleChoiceItems(options
        , 0) { dialogInterface: DialogInterface, item: Int ->
      selectedItem = item
    }
    builder.setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, p1: Int ->
      when {
        options[selectedItem].equals(getString(string.mission_name)) -> {
          // search mission name
          toggleProgressIndicator(main_progress, true)
          searchViewModel?.searchLiveData?.fetchSpaceLaunches(this.searchQuery.toString())

        }
        else -> {
          // search rocket name
          toggleProgressIndicator(main_progress, true)
          searchViewModel?.searchLiveData?.fetchSpaceLaunches(EMPTY, this.searchQuery.toString())

        }
      }
      empty_results.visibility = View.GONE
      empty_results.text = getString(string.empty_results)
      dialogInterface.dismiss()
    }
    builder.create()
    builder.show();

  }

  /**
   * Initializing the Search ViewModel and associated SearchLiveData
   */
  private fun initSearchViewModel() {
    searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
    evgoApp?.let { searchViewModel?.setSearchLiveData(it) }
  }

}