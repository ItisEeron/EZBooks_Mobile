package com.example.mac.ezbooks.ui.main.RecyclerView_Adapters

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
import com.example.mac.ezbooks.detail_fragments.SellingBookDetailFragment
import com.example.mac.ezbooks.ui.main.MainViewModel

//Passes in Fragment in order to determine which List to use
//If the Fragment is the HomeFragment, we only want the views to populate the with at most
//5 Books
//Other instances (Requested Books, Search, and Uploaded) will show maximum
//Search will have filtering options and will actually only display the first 40 books
class UploadBooksRecyclerAdapter (val fragment: Fragment, private val viewModel : MainViewModel): RecyclerView.Adapter<UploadBooksRecyclerAdapter.ViewHolder>() {


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
        //TODO: ADD CONDITIONS FOR SELLING TEXTBOOKS AS WELL
        if(fragment is HomeFragment) {
            viewHolder.itemTitle.text = viewModel.recent_selling_Textbooks[i].Title
            viewHolder.itemISBN.text = viewModel.recent_selling_Textbooks[i].isbn
            viewHolder.itemAccount.text = viewModel.recent_selling_Textbooks[i].affiliated_account.user_name
            if(viewModel.recent_selling_Textbooks[i].book_img != null){
                var bitmap = BitmapFactory.
                        decodeByteArray(viewModel.recent_selling_Textbooks[i].book_img,
                                0, viewModel.recent_selling_Textbooks[i].book_img!!.size)
                viewHolder.itemImage.setImageBitmap(bitmap)

            }else{
                viewHolder.itemImage.setImageResource(R.drawable.android_image_5)
            }
        }
        else{
            viewHolder.itemTitle.text = viewModel.selling_textbooks[i].Title
            viewHolder.itemISBN.text = viewModel.selling_textbooks[i].isbn
            viewHolder.itemAccount.text = viewModel.selling_textbooks[i].affiliated_account.user_name
            if(viewModel.selling_textbooks[i].book_img != null){
                var bitmap = BitmapFactory.
                        decodeByteArray(viewModel.selling_textbooks[i].book_img,
                                0, viewModel.selling_textbooks[i].book_img!!.size)
                viewHolder.itemImage.setImageBitmap(bitmap)

            }else{
                viewHolder.itemImage.setImageResource(R.drawable.android_image_5)
            }
        }
    }

    override fun getItemCount(): Int {
        return if(fragment is HomeFragment) viewModel.recent_selling_Textbooks.size
        else viewModel.selling_textbooks.size
    }


}