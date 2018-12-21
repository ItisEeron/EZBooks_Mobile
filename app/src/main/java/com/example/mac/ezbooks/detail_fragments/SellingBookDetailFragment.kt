package com.example.mac.ezbooks.detail_fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.RecyclerView_Adapters.UserNamesRecyclerAdapter
import com.example.mac.ezbooks.ui.main.Searched_Textbooks
import com.example.mac.ezbooks.ui.main.Textbooks
import kotlinx.android.synthetic.main.detail_selling_books_layout.*
import kotlinx.android.synthetic.main.detail_selling_books_layout.view.*

class SellingBookDetailFragment : Fragment() {


    lateinit var image: ImageView
    private lateinit var booksViewModel: MainViewModel
    private var RECENTS_SIZE = 5


    private var databaseManager = FirebaseDatabaseManager()

    private lateinit var UserNameslayoutManager: RecyclerView.LayoutManager
    private lateinit var UserNamesadapter: RecyclerView.Adapter<UserNamesRecyclerAdapter.ViewHolder>
    private lateinit var UserNamesRecyclerview : RecyclerView
    private lateinit var selectedTextbook : Textbooks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        selectedTextbook = booksViewModel.selected_selling
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detail_selling_books_layout, container, false)

        //Set up information
        view.detail_selling_book_title.text = selectedTextbook.Title
        view.detail_selling_book_isbn.text = selectedTextbook.isbn
        view.detail_selling_book_course.text = selectedTextbook.course
        view.detail_selling_book_instructor.text = selectedTextbook.instructor

        databaseManager.getTextbookImg(selectedTextbook.book_id.toString(), selectedTextbook.affiliated_account?.user_id!!,
                view.detail_selling_book_image, selectedTextbook.thumbnailURL)

        view.edit_listing_button.setOnClickListener{

            var TAG = selectedTextbook.affiliated_account?.user_id + selectedTextbook.book_id.toString() +
                    "_edit"

            //Prevents fragment from being recreated multiple times
            var newFragment = activity?.supportFragmentManager?.findFragmentByTag(TAG)
            if(newFragment == null) {
                newFragment = EditListingFragment()
            }
            activity?.supportFragmentManager?.beginTransaction()?.
                    setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                    newFragment, TAG)?.addToBackStack(TAG)?.commit()
        }

        view.remove_listing_button.setOnClickListener{
            //Update the home page view...Remove the book if it is found in the homepage recycler view, also adjust the view when removed
            booksViewModel.recent_selling_Textbooks.clear()
            if(booksViewModel.recent_selling_Textbooks.contains(selectedTextbook)){
                booksViewModel.recent_selling_Textbooks.remove(selectedTextbook)

                if(booksViewModel.selling_textbooks.size > RECENTS_SIZE ){
                    booksViewModel.recent_selling_Textbooks.add(booksViewModel.selling_textbooks[RECENTS_SIZE-1])
                }
            }

            databaseManager = FirebaseDatabaseManager()
            val textbook : Textbooks = selectedTextbook.copy()
            val sText = Searched_Textbooks(booksViewModel.user_account.user_id, textbook.book_id,
                    booksViewModel.user_account.user_name, booksViewModel.user_account.email_address,
                    booksViewModel.user_account.phone_number, textbook.Title, textbook.isbn,
                    textbook.course, textbook.instructor, textbook.potential_buyers)

            databaseManager.removeTextbook(sText)

            booksViewModel.selling_textbooks.remove(selectedTextbook)



            //This should remove all associated users from the selected selling, then delete all occurances of the textbook

            for(buyer in sText.potential_buyers!!)
                databaseManager.removeOtherUser(sText, buyer)

            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.popBackStack()

            Toast.makeText(activity,
                    "You have successfully removed your textbook!",
                    Toast.LENGTH_LONG).show()
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        UserNamesRecyclerview = recyclerView2
        UserNamesadapter = UserNamesRecyclerAdapter(this, booksViewModel)
        UserNamesRecyclerview.adapter = UserNamesadapter
        UserNameslayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        UserNamesRecyclerview.layoutManager = UserNameslayoutManager

    }

}