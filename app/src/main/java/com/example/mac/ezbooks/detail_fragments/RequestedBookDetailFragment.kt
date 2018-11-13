package com.example.mac.ezbooks.detail_fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.mac.ezbooks.HomeFragment
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.ui.main.MainViewModel
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
        view.detail_requested_book_phone_number.text = booksViewModel.selected_requested.affiliated_account.phone_number
        view.detail_requested_book_email.text = booksViewModel.selected_requested.affiliated_account.email_address

        view.unrequest_button.setOnClickListener{
         //TODO: remove this referenced listing in the data base. Any connection made between this textbook and user should be removed

            //Update the home page view...Remove the book if it is found in the homepage recycler view, also adjust the view when removed
            if(booksViewModel.recent_requested_Textbooks.contains(booksViewModel.selected_requested)){
                booksViewModel.recent_requested_Textbooks.remove(booksViewModel.selected_requested)

                if(booksViewModel.requested_textbooks.size > RECENTS_SIZE ){
                    booksViewModel.recent_requested_Textbooks.add(booksViewModel.requested_textbooks[RECENTS_SIZE-1])
                }
            }
            booksViewModel.requested_textbooks.remove(booksViewModel.selected_requested)

            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.popBackStack()

            Toast.makeText(activity,
                    "You have successfully removed the requested textbook!",
                    Toast.LENGTH_LONG).show()        }

        view.report_button.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.
                    setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                    ReportUserFragment())?.addToBackStack(null)?.commit()
        }

        return view
    }
}