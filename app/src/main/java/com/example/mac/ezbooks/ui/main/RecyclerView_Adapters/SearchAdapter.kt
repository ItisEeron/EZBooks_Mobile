package com.example.mac.ezbooks.ui.main.RecyclerView_Adapters

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.detail_fragments.RequestedBookDetailFragment
import com.example.mac.ezbooks.detail_fragments.SearchedTextbookFragment
import com.example.mac.ezbooks.ui.main.MainViewModel
import android.os.Bundle
import com.example.mac.ezbooks.ui.main.Searched_Textbooks


class SearchAdapter (fragment : Fragment, searchedQuery : ArrayList<Searched_Textbooks>): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var fragment: Fragment? = null
    var searchedQuery : ArrayList<Searched_Textbooks> = ArrayList()

    init{
        this.fragment = fragment
        this.searchedQuery = searchedQuery
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemTitle: TextView
        var itemCourse: TextView
        //var itemInstructor : TextView

        init {
            itemImage = itemView.findViewById(R.id.searched_Image)
            itemTitle = itemView.findViewById(R.id.searched_Title)
            itemCourse = itemView.findViewById(R.id.searched_Course)
            //itemInstructor  = itemView.findViewById(R.id.item_account)

            itemView.setOnClickListener{view ->
                var position: Int = adapterPosition
                //TODO: Call Fragment that shows detailed information based on the book
                /*Toast.makeText(context,
                        "You have clicked on  item" + position,
                        Toast.LENGTH_LONG).show()*/
                val b = Bundle()
                b.putSerializable("textbook", searchedQuery[position])
                var newFragment = SearchedTextbookFragment()
                newFragment.arguments = b
                fragment?.fragmentManager?.beginTransaction()?.
                        setCustomAnimations(R.anim.design_snackbar_in, R.anim.design_snackbar_out)?.replace(R.id.flContent,
                        newFragment)?.addToBackStack("search_Detail")?.commit()
            }
        }
    }

    override fun onCreateViewHolder (viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.search_shop_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        //viewHolder.itemInstructor.text = instructorList[i]
        viewHolder.itemTitle.text = searchedQuery[i].title
        viewHolder.itemCourse.text = searchedQuery[i].course

        if(searchedQuery[i].book_img != null) {
            var bitmap = BitmapFactory.decodeByteArray(searchedQuery[i].book_img,
                        0, searchedQuery[i].book_img!!.size)
            viewHolder.itemImage.setImageBitmap(bitmap)

        } else {
            viewHolder.itemImage.setImageResource(R.drawable.android_image_5)
        }
    }

    override fun getItemCount(): Int {
        return searchedQuery.size
    }



}
