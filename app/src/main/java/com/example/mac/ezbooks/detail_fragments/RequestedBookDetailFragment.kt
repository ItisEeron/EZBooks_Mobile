package com.example.mac.ezbooks.detail_fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.Potential_Buyer
import com.example.mac.ezbooks.ui.main.Textbooks
import kotlinx.android.synthetic.main.detail_requested_books_layout.view.*

class RequestedBookDetailFragment : Fragment() {
    lateinit var image: ImageView
    private lateinit var booksViewModel: MainViewModel
    private lateinit var textbook: Textbooks
    private var RECENTS_SIZE = 5
    private var MIN_SIZE = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detail_requested_books_layout, container, false)

        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Set up information
        view.detail_requested_book_title.text = booksViewModel.selected_requested.Title
        view.detail_requested_book_isbn.text = booksViewModel.selected_requested.isbn
        view.detail_requested_book_course.text = booksViewModel.selected_requested.course
        view.detail_requested_book_instructor.text = booksViewModel.selected_requested.instructor


        view.detail_requested_book_seller.text = booksViewModel.selected_requested.affiliated_account.user_name

        //First check to see if the selected list of buyers is null (it should not!!)
        var isAproved = false
        if(booksViewModel.selected_requested.potential_buyers != null){
            //Find your id
            for( potentialbuyeraccount : Potential_Buyer in booksViewModel.selected_requested.potential_buyers!!) {
                if(potentialbuyeraccount.account_id == booksViewModel.user_account.user_id){//When found check if your id has been approved
                    isAproved = potentialbuyeraccount.approved
                    break
                }
            }
            if(isAproved) {//If Id
                view.detail_requested_book_phone_number.text = booksViewModel.selected_requested.affiliated_account.phone_number
                view.detail_requested_book_email.text = booksViewModel.selected_requested.affiliated_account.email_address
            }else{
                view.detail_requested_book_phone_number.text = "Not Authorized to View Information"
                view.detail_requested_book_email.text = "Not Authorized to View Information"
            }
        }
        else {//If null for whatever reason, show not authorized
            view.detail_requested_book_phone_number.text = "Not Authorized to View Information"
            view.detail_requested_book_email.text = "Not Authorized to View Information"
        }

        view.remove_button.setOnClickListener{
         //TODO: remove this referenced listing in the data base. Any connection made between this textbook and user should be removed

            //Update the home page view...Remove the book if it is found in the homepage recycler view, also adjust the view when removed
            if(booksViewModel.recent_requested_Textbooks.contains(booksViewModel.selected_requested)){
                booksViewModel.recent_requested_Textbooks.remove(booksViewModel.selected_requested)

                if(booksViewModel.requested_textbooks.size > RECENTS_SIZE ){
                    booksViewModel.recent_requested_Textbooks.add(booksViewModel.requested_textbooks[RECENTS_SIZE-1])
                }
            }
            booksViewModel.requested_textbooks.remove(booksViewModel.selected_requested)

            //Remove if the id of the current user
            if(booksViewModel.selected_requested.potential_buyers != null) {
                booksViewModel.selected_requested.potential_buyers!!.removeIf { Potential_Buyer ->
                    Potential_Buyer.account_id == booksViewModel.user_account.user_id
                }
            }

            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.popBackStack()

            Toast.makeText(activity,
                    "You have successfully removed the requested textbook!",
                    Toast.LENGTH_LONG).show()
        }

        view.edit_button.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.
                    setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                    ReportUserFragment())?.addToBackStack(null)?.commit()
        }

        return view
    }
}