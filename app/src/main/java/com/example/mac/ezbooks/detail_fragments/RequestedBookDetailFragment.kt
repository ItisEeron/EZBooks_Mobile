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
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.Potential_Buyer
import com.example.mac.ezbooks.ui.main.Searched_Textbooks
import kotlinx.android.synthetic.main.detail_requested_books_layout.view.*

class RequestedBookDetailFragment : Fragment() {
    lateinit var image: ImageView
    private lateinit var booksViewModel: MainViewModel
    private var RECENTS_SIZE = 5
    val databaseManager : FirebaseDatabaseManager = FirebaseDatabaseManager()
    lateinit var selectedTextbook : Searched_Textbooks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        selectedTextbook = booksViewModel.selected_requested
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detail_requested_books_layout, container, false)

        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        selectedTextbook = booksViewModel.selected_requested
        //Set up information
        view.detail_requested_book_title.text = selectedTextbook.title
        view.detail_requested_book_isbn.text = selectedTextbook.isbn
        view.detail_requested_book_course.text = selectedTextbook.course
        view.detail_requested_book_instructor.text = selectedTextbook.instructor

        databaseManager.getTextbookImg(selectedTextbook.bookid.toString(), selectedTextbook.userid!!,
                view.detail_requested_book_image, selectedTextbook.thumbnailURL)

        view.detail_requested_book_seller.text = selectedTextbook.user_name

        //First check to see if the selected list of buyers is null (it should not!!)
        var isAproved = false
        if(selectedTextbook.potential_buyers != null){
            //Find your id
            for( potentialbuyeraccount : Potential_Buyer in selectedTextbook.potential_buyers!!) {
                if(potentialbuyeraccount.account_id == booksViewModel.user_account.user_id){//When found check if your id has been approved
                    isAproved = potentialbuyeraccount.approved
                    break
                }
            }
            if(isAproved) {//If Id
                view.detail_requested_book_phone_number.text = selectedTextbook.user_phone
                view.detail_requested_book_email.text = selectedTextbook.user_email
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

            //Update the home page view...Remove the book if it is found in the homepage recycler view, also adjust the view when removed
            booksViewModel.recent_requested_Textbooks.clear()
            if(booksViewModel.recent_requested_Textbooks.contains(selectedTextbook)){
                booksViewModel.recent_requested_Textbooks.remove(selectedTextbook)

                if(booksViewModel.requested_textbooks.size > RECENTS_SIZE ){
                    booksViewModel.recent_requested_Textbooks.add(booksViewModel.requested_textbooks[RECENTS_SIZE-1])
                }
            }
            booksViewModel.requested_textbooks.remove(selectedTextbook)

            //Remove if the id of the current user
            if(selectedTextbook.potential_buyers != null) {
                selectedTextbook.potential_buyers!!.removeIf { Potential_Buyer ->
                    Potential_Buyer.account_id == booksViewModel.user_account.user_id
                }
            }

            var databaseManager = FirebaseDatabaseManager()
            databaseManager.removeRequest(booksViewModel, selectedTextbook)
            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.popBackStack()

            Toast.makeText(activity,
                    "You have successfully removed the requested textbook!",
                    Toast.LENGTH_LONG).show()
        }

        view.edit_button.setOnClickListener{

            val b = Bundle()
            b.putSerializable("textbook", selectedTextbook)

            var TAG = selectedTextbook.userid + selectedTextbook.bookid.toString() +
                    "_report"

            //Prevents fragment from being recreated multiple times
            var newFragment = activity?.supportFragmentManager?.findFragmentByTag(TAG)
            if(newFragment == null) {
                newFragment = ReportUserFragment()
            }
            newFragment.arguments = b
            activity?.supportFragmentManager?.beginTransaction()?.
                    setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                    newFragment, TAG)?.addToBackStack(TAG)?.commit()
        }

        return view
    }
}