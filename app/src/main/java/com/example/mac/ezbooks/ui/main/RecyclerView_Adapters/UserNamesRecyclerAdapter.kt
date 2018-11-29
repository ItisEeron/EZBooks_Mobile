package com.example.mac.ezbooks.ui.main.RecyclerView_Adapters

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.Potential_Buyer
import com.example.mac.ezbooks.ui.main.Searched_Textbooks
import com.example.mac.ezbooks.ui.main.Textbooks
import kotlinx.android.synthetic.main.other_users_card_layout.view.*

class UserNamesRecyclerAdapter(val fragment: Fragment, private val booksViewModel: MainViewModel, textbooks : Textbooks) :  RecyclerView.Adapter<UserNamesRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView: TextView
        var buyerList : MutableList<Potential_Buyer>?
        var sText : Searched_Textbooks
        var databaseManager = FirebaseDatabaseManager()
        init {
            nameView = itemView.findViewById(R.id.buyer_name)
            var textbook = booksViewModel.selected_selling
            sText = Searched_Textbooks(booksViewModel.user_account.user_id, textbook.book_id,
                    booksViewModel.user_account.user_name, textbook.affiliated_account?.email_address,
                    textbook.affiliated_account?.phone_number, textbook.Title, textbook.isbn,
                    textbook.course, textbook.instructor, textbook.book_img, textbook.potential_buyers)
            buyerList = booksViewModel.selected_selling.potential_buyers


            //Short click to approve
            itemView.setOnClickListener{view ->
                var position: Int = getAdapterPosition()

               if(buyerList!![position].approved){
                   booksViewModel.selected_selling.potential_buyers!![position].approved = false
                   buyerList!![position].approved = false
                   itemView.card_view.setCardBackgroundColor(Color.RED)
               }else{
                   booksViewModel.selected_selling.potential_buyers!![position].approved = true
                   buyerList!![position].approved = true
                   itemView.card_view.setCardBackgroundColor(Color.GREEN)
               }
                databaseManager.toggleRequestApproval(booksViewModel, sText, buyerList!![position].approved, buyerList!![position])

            }

            //Long Click to Remove user
            itemView.setOnLongClickListener{view ->
                var position: Int = getAdapterPosition()

                databaseManager.removeOtherUser(booksViewModel, sText, buyerList!![position])
                buyerList!!.removeAt(position)

                //Upadtes the recycler view!!!!
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, buyerList!!.size)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.other_users_card_layout, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.nameView.text = viewHolder.buyerList?.get(i)?.account_name

        viewHolder.itemView.card_view.setCardBackgroundColor(Color.RED)
        //Sets the buyer card to green if they have been approved to see information
        if(viewHolder.buyerList!!.get(i).approved)
            viewHolder.itemView.card_view.setCardBackgroundColor(Color.GREEN)
        //Log.i("Eeron Log: " , viewHolder.buyerList[i].second)
    }

    override fun getItemCount(): Int {
        return if(booksViewModel.selected_selling.potential_buyers == null) 0 else
            booksViewModel.selected_selling.potential_buyers!!.size
    }
}