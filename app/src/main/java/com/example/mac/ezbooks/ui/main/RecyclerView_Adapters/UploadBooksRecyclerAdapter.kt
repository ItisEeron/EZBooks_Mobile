package com.example.mac.ezbooks.ui.main.RecyclerView_Adapters

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.mac.ezbooks.HomeFragment
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.detail_fragments.SellingBookDetailFragment
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.client.cache.Resource

//Passes in Fragment in order to determine which List to use
//If the Fragment is the HomeFragment, we only want the views to populate the with at most
//5 Books
//Other instances (Requested Books, Search, and Uploaded) will show maximum
//Search will have filtering options and will actually only display the first 40 books
class UploadBooksRecyclerAdapter (val fragment: Fragment, private val viewModel : MainViewModel): RecyclerView.Adapter<UploadBooksRecyclerAdapter.ViewHolder>() {
    private val storage = FirebaseStorage.getInstance()
    var storageRef = storage.getReference()
    private val TEXTBOOK_IMG_HEADER = "images/textbooks/"
    var databaseManager = FirebaseDatabaseManager()

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemTitle: TextView
        var itemISBN: TextView
        var itemAccount : TextView


        init { //Initalize variables for the holder
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemISBN = itemView.findViewById(R.id.item_isbn)
            itemAccount = itemView.findViewById(R.id.item_account)

            //This will allow the object to invoke an event when clicked on!!!
            itemView.setOnClickListener{view ->
                var position: Int = getAdapterPosition()

                viewModel.selected_selling = viewModel.selling_textbooks[position]
                fragment.activity?.supportFragmentManager?.beginTransaction()?.
                        setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                        SellingBookDetailFragment())?.addToBackStack(null)?.commit()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        //TODO: add uploaded_book_card_layout and replace request_book_card_layout with this
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.requested_book_card_layout, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val textbook = if (fragment is HomeFragment) viewModel.recent_selling_Textbooks[i]
        else viewModel.selling_textbooks[i]

        viewHolder.itemTitle.text = textbook.Title
        viewHolder.itemISBN.text = textbook.isbn
        viewHolder.itemAccount.text = textbook.affiliated_account?.user_name

        databaseManager.getTextbookImg(textbook.book_id.toString(),
                textbook.affiliated_account?.user_id!!, viewHolder.itemImage)

    }

    override fun getItemCount(): Int {
        return if(fragment is HomeFragment) viewModel.recent_selling_Textbooks.size
        else viewModel.selling_textbooks.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        if(holder is UploadBooksRecyclerAdapter.ViewHolder){
            var h = holder

            h.itemImage.setImageResource(0)

            h.itemTitle.text = null
            h.itemISBN.text = null
            h.itemAccount.text = null


            //This is also a good time to clear Picasso/Glide/... for the ImageView if you have used it to load an image in the first place

            //Picasso.with(h.itemImage.context).invalidate(h.image_path);
            //Glide.with(h.itemImage.context).clear(h.itemImage)
        }
        //onViewRecycled(holder)
    }


}