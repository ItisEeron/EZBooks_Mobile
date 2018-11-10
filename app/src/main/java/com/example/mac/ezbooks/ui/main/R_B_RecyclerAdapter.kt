package com.example.mac.ezbooks.ui.main

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.graphics.BitmapFactory
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.mac.ezbooks.HomeFragment
import com.example.mac.ezbooks.R


//Passes in Fragment in order to determine which List to use
//If the Fragment is the HomeFragment, we only want the views to populate the with at most
//5 Books
//Other instances (Requested Books, Search, and Uploaded) will show maximum
//Search will have filtering options and will actually only display the first 40 books
class R_B_RecyclerAdapter (val fragment: Fragment , private val viewModel : MainViewModel): RecyclerView.Adapter<R_B_RecyclerAdapter.ViewHolder>() {


    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)

            itemView.setOnClickListener{view ->
                var position: Int = getAdapterPosition()

                //TODO: Call Fragment that shows detailed information based on the book
                Snackbar.make(view, "Click detected on item $position",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.requested_book_card_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        //TODO: ADD CONDITIONS FOR SELLING TEXTBOOKS AS WELL
        if(fragment is HomeFragment) {
            viewHolder.itemTitle.text = viewModel.recent_requested_Textbooks[i].Title
            viewHolder.itemDetail.text = viewModel.recent_requested_Textbooks[i].isbn
            if(viewModel.recent_requested_Textbooks[i].book_img != null){
                var bitmap = BitmapFactory.
                        decodeByteArray(viewModel.recent_requested_Textbooks[i].book_img,
                                0, viewModel.recent_requested_Textbooks[i].book_img!!.size)
                viewHolder.itemImage.setImageBitmap(bitmap)

            }else{
                viewHolder.itemImage.setImageResource(R.drawable.android_image_5)
            }
        }
        else{
            viewHolder.itemTitle.text = viewModel.requested_textbooks[i].Title
            viewHolder.itemDetail.text = viewModel.requested_textbooks[i].isbn
            if(viewModel.requested_textbooks[i].book_img != null){
                var bitmap = BitmapFactory.
                        decodeByteArray(viewModel.requested_textbooks[i].book_img,
                                0, viewModel.requested_textbooks[i].book_img!!.size)
                viewHolder.itemImage.setImageBitmap(bitmap)

            }else{
                viewHolder.itemImage.setImageResource(R.drawable.android_image_5)
            }
        }
    }

    override fun getItemCount(): Int {
        return if(fragment is HomeFragment) viewModel.recent_requested_Textbooks.size else viewModel.requested_textbooks.size
    }


}