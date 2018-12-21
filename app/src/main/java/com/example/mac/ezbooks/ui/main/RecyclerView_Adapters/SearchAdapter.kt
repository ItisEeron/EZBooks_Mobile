package com.example.mac.ezbooks.ui.main.RecyclerView_Adapters


import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.detail_fragments.SearchedTextbookFragment
import android.os.Bundle
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.Searched_Textbooks


class SearchAdapter (fragment : Fragment, searchedQuery : ArrayList<Searched_Textbooks>): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var fragment: Fragment? = null
    var searchedQuery : ArrayList<Searched_Textbooks> = ArrayList()
    var databaseManager = FirebaseDatabaseManager()

    init{
        this.fragment = fragment
        this.searchedQuery = searchedQuery
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemTitle: TextView
        var itemCourse: TextView

        init {
            itemImage = itemView.findViewById(R.id.searched_Image)
            itemTitle = itemView.findViewById(R.id.searched_Title)
            itemCourse = itemView.findViewById(R.id.searched_Course)

            itemView.setOnClickListener{view ->
                var position: Int = adapterPosition
                val b = Bundle()
                b.putSerializable("textbook", searchedQuery[position])
                b.putInt("position",position)

                var TAG = searchedQuery[position].userid + searchedQuery[position].bookid.toString() +
                        "_detail"

                //Prevents fragment from being recreated multiple times
                var newFragment = fragment?.activity?.supportFragmentManager?.findFragmentByTag(TAG)
                if(newFragment == null) {
                    newFragment = SearchedTextbookFragment()
                    newFragment.arguments = b
                }
                fragment?.fragmentManager?.beginTransaction()?.
                        setCustomAnimations(R.anim.design_snackbar_in, R.anim.design_snackbar_out)?.replace(R.id.flContent,
                        newFragment, TAG)?.addToBackStack(TAG)?.commit()
            }
        }
    }

    override fun onCreateViewHolder (viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.search_shop_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = searchedQuery[i].title
        viewHolder.itemCourse.text = searchedQuery[i].course
        viewHolder.itemImage.setImageResource(R.drawable.android_image_5)

        databaseManager.getTextbookImg(searchedQuery[i].bookid.toString(),searchedQuery[i].userid!!,
                viewHolder.itemImage, searchedQuery[i].thumbnailURL)

    }

    override fun getItemCount(): Int {
        return searchedQuery.size
    }



}
