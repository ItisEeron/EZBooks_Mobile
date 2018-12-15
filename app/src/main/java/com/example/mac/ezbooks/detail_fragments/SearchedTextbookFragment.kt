package com.example.mac.ezbooks.detail_fragments

import android.arch.lifecycle.ViewModelProviders
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.Searched_Textbooks
import kotlinx.android.synthetic.main.detail_requested_books_layout.view.*

class SearchedTextbookFragment : Fragment() {
    val databaseManager : FirebaseDatabaseManager = FirebaseDatabaseManager()
    lateinit var booksViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.detail_requested_books_layout, container, false)

        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val args = arguments
        val textbook = args!!.getSerializable("textbook") as Searched_Textbooks
        val request_button = view.remove_button

        request_button.text = "Request Textbook"

        //Set up information
        view.detail_requested_book_title.text = textbook.title
        view.detail_requested_book_isbn.text = textbook.isbn
        view.detail_requested_book_course.text = textbook.course
        view.detail_requested_book_instructor.text = textbook.instructor
        view.detail_requested_book_seller.text = textbook.user_name

        databaseManager.getTextbookImg(textbook.bookid.toString(), textbook.userid!!,
                view.detail_requested_book_image)

        //First check to see if the selected list of buyers is null (it should not!!)
        var isAproved = false

        view.detail_requested_book_phone_number.text = "Not Authorized to View Information"
        view.detail_requested_book_email.text = "Not Authorized to View Information"

        request_button.setOnClickListener{

            databaseManager.sendRequest(booksViewModel.user_account.user_name!!,
                    booksViewModel.user_account.user_id!!,textbook.userid!!, textbook.bookid!!)

            booksViewModel.requested_textbooks.add(0, textbook)
            booksViewModel.recent_requested_Textbooks.clear()

            if(booksViewModel.requested_textbooks.size > 5) {
                booksViewModel.recent_requested_Textbooks.addAll(booksViewModel.requested_textbooks
                        .subList(0, 5))
            }
            else {
                booksViewModel.recent_requested_Textbooks.addAll(booksViewModel.requested_textbooks)
            }

            Toast.makeText(activity,
                    "You have successfully requested the textbook!",
                    Toast.LENGTH_LONG).show()
        }

        view.edit_button.setOnClickListener{

            val b = Bundle()
            b.putSerializable("textbook", textbook)
            var newFragment = ReportUserFragment()
            newFragment.arguments = b
            activity?.supportFragmentManager?.beginTransaction()?.
                    setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                    newFragment)?.addToBackStack(null)?.commit()

        }

        return view
    }
}