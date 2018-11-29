package com.example.mac.ezbooks.detail_fragments

import android.arch.lifecycle.ViewModelProviders
import android.graphics.BitmapFactory
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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.detail_selling_books_layout.*
import kotlinx.android.synthetic.main.detail_selling_books_layout.view.*

class SellingBookDetailFragment : Fragment() {


    lateinit var image: ImageView
    private lateinit var booksViewModel: MainViewModel
    private var RECENTS_SIZE = 5
    private lateinit var database : FirebaseDatabase


    private lateinit var databaseManager: FirebaseDatabaseManager
    private lateinit var UserNameslayoutManager: RecyclerView.LayoutManager
    private lateinit var UserNamesadapter: RecyclerView.Adapter<UserNamesRecyclerAdapter.ViewHolder>
    private lateinit var UserNamesRecyclerview : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detail_selling_books_layout, container, false)

        database = FirebaseDatabase.getInstance()


        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Set up information
        view.detail_selling_book_title.text = booksViewModel.selected_selling.Title
        view.detail_selling_book_isbn.text = booksViewModel.selected_selling.isbn
        view.detail_selling_book_course.text = booksViewModel.selected_selling.course
        view.detail_selling_book_instructor.text = booksViewModel.selected_selling.instructor

        if(booksViewModel.selected_selling.book_img != null) {
            val bitmap = BitmapFactory.decodeByteArray(booksViewModel.selected_selling.book_img,
                    0, booksViewModel.selected_selling.book_img!!.size)
            view.detail_selling_book_image.setImageBitmap(bitmap)
        }

        view.edit_listing_button.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.
                    setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                    EditListingFragment())?.addToBackStack(null)?.commit()
        }

        view.remove_listing_button.setOnClickListener{
            //Update the home page view...Remove the book if it is found in the homepage recycler view, also adjust the view when removed
            booksViewModel.recent_selling_Textbooks.clear()
            if(booksViewModel.recent_selling_Textbooks.contains(booksViewModel.selected_selling)){
                booksViewModel.recent_selling_Textbooks.remove(booksViewModel.selected_selling)

                if(booksViewModel.selling_textbooks.size > RECENTS_SIZE ){
                    booksViewModel.recent_selling_Textbooks.add(booksViewModel.selling_textbooks[RECENTS_SIZE-1])
                }
            }

            databaseManager = FirebaseDatabaseManager()
            val textbook : Textbooks = booksViewModel.selected_selling.copy()
            val sText = Searched_Textbooks(booksViewModel.user_account.user_id, textbook.book_id,
                    booksViewModel.user_account.user_name, booksViewModel.user_account.email_address,
                    booksViewModel.user_account.phone_number, textbook.Title, textbook.isbn,
                    textbook.course, textbook.instructor, textbook.book_img, textbook.potential_buyers)

            databaseManager.removeTextbook(sText)

            booksViewModel.selling_textbooks.remove(booksViewModel.selected_selling)



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