package com.evgo.sample.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.apollographql.apollo.SpaceLaunchesQuery.Launch
import com.evgo.sample.R
import com.evgo.sample.ui.SearchCardAdapter.SearchResultsViewHolder
import com.evgo.sample.utils.StringUtils
import java.util.ArrayList

/**
 * The SearchCardAdapter item adapter class extends RecyclerView.Adapter
 */

class SearchCardAdapter(private val context: Context,
    var searchResult: ArrayList<Launch>) : Adapter<SearchResultsViewHolder>() {

  /**
   * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
   * an item.
   *
   *
   * This new ViewHolder should be constructed with a new View that can represent the items
   * of the given type. You can either create a new View manually or inflate it from an XML
   * layout file.
   *
   *
   * The new ViewHolder will be used to display items of the adapter using
   * [.onBindViewHolder]. Since it will be re-used to display
   * different items in the data set, it is a good idea to cache references to sub views of
   * the View to avoid unnecessary [View.findViewById] calls.
   *
   * @param parent The ViewGroup into which the new View will be added after it is bound to
   * an adapter position.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View of the given view type.
   * @see .getItemViewType
   * @see .onBindViewHolder
   *
   */
  override fun onCreateViewHolder(parent: ViewGroup,
      viewType: Int): SearchResultsViewHolder {
    val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent,
        false)
    return SearchResultsViewHolder(view)
  }

  override fun onBindViewHolder(holder: SearchResultsViewHolder,
      position: Int) {
    val launchesPast = searchResult[position]
    holder.setDetails(launchesPast)

    // This will launch the youtube video when any item with a valid video link in the search list is clicked
    holder.itemView.setOnClickListener {
      context.startActivity(
          Intent(Intent.ACTION_VIEW, Uri.parse(searchResult[position].links()?.video_link())))
    }
  }

  override fun getItemCount(): Int {
    return searchResult.size
  }

  /**
   * Content ViewHolder containing search results item elements
   */
  inner class SearchResultsViewHolder(
      itemView: View) : ViewHolder(itemView) {
    val missionName: TextView = itemView.findViewById(R.id.mission_name)
    val rocketName: TextView = itemView.findViewById(R.id.rocket_name)
    val launchDate: TextView = itemView.findViewById(R.id.launch_date)
    val videoLink: TextView = itemView.findViewById(R.id.video_link)

    fun setDetails(searchResult: Launch) {

      val missionNameLabel = "Mission Name: "
      missionName.text = StringUtils.styleText(missionNameLabel,
          searchResult.mission_name().toString())

      val rocketNameLabel = context.getString(R.string.rocket_name) + ": "
      rocketName.text = StringUtils.styleText(rocketNameLabel,
          searchResult.rocket()?.rocket_name().toString())

      val launchDateLabel = "Launch Date: "
      val formattedDate = StringUtils.formatDate(
          searchResult.launch_date_unix().toString()).toString()
      launchDate.text = StringUtils.styleText(launchDateLabel, formattedDate)

      val videoLinkLabel = context.getString(R.string.video_link_label)
      videoLink.text = StringUtils.styleText(videoLinkLabel,
          searchResult.links()?.video_link().toString())
    }

  }

}