package com.example.mac.ezbooks.detail_fragments

import android.arch.lifecycle.ViewModelProviders
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
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
import com.example.mac.ezbooks.ui.main.Potential_Buyer
import com.example.mac.ezbooks.ui.main.RecyclerView_Adapters.UserNamesRecyclerAdapter
import com.example.mac.ezbooks.ui.main.Searched_Textbooks
import com.example.mac.ezbooks.ui.main.Textbooks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.detail_requested_books_layout.view.*
import kotlinx.android.synthetic.main.detail_selling_books_layout.*
import kotlinx.android.synthetic.main.detail_selling_books_layout.view.*

class SellingBookDetailFragment : Fragment() {

    private val KEY_ACCOUNT = "account"
    private val KEY_TEXTBOOK_REF = "textbook_ref"
    private val KEY_TEXTBOOK = "textbook"
    private val KEY_REPORTS = "reports"
    private val KEY_CLASS = "class_standing"
    private val KEY_EMAIL = "email"
    private val KEY_NAME = "name"
    private val KEY_ID = "id"
    private val KEY_PHONE = "phone_number"
    private val KEY_PROF = "profile_img"
    private val KEY_REQ_BOOKS = "req_books"

    private val KEY_BOOK_ID = "book_id"
    private val KEY_TITLE = "title"
    private val KEY_ISBN = "isbn"
    private val KEY_INSTRUCTOR = "instructor"
    private val KEY_COURSE = "course"
    private val KEY_BOOK_IMG = "book_img"
    private val KEY_POTENTIAL_BUYERS = "potential_buyers"
    private val KEY_BUYER_ID = "buyer_id"
    private val KEY_BUYER_NAME = "buyer_name"
    private val KEY_BUYER_APPROVAL = "buyer_approval"


    lateinit var image: ImageView
    private lateinit var booksViewModel: MainViewModel
    private var RECENTS_SIZE = 5
    private var MIN_SIZE = 0
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
            var bitmap = BitmapFactory.decodeByteArray(booksViewModel.selected_selling.book_img,
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
            var textbook : Textbooks = booksViewModel.selected_selling.copy()
            var sText = Searched_Textbooks(booksViewModel.user_account.user_id, textbook.book_id,
                    booksViewModel.user_account.user_name, booksViewModel.user_account.email_address,
                    booksViewModel.user_account.phone_number, textbook.Title, textbook.isbn,
                    textbook.course, textbook.instructor, textbook.book_img, textbook.potential_buyers)

            databaseManager.removeTextbook(sText)

            booksViewModel.selling_textbooks.remove(booksViewModel.selected_selling)



            //This should remove all associated users from the selected selling, then delete all occurances of the textbook

            for(buyer in sText.potential_buyers!!)
                databaseManager.removeOtherUser(booksViewModel, sText, buyer)

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
        UserNamesadapter = UserNamesRecyclerAdapter(this, booksViewModel, booksViewModel.selected_selling)
        UserNamesRecyclerview.adapter = UserNamesadapter
        UserNameslayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        UserNamesRecyclerview.layoutManager = UserNameslayoutManager

        ///Testing to see if the database works with child listener, want to see what happens when a new
        //potential buy is added
        /*
        var myRef = database.getReference(KEY_TEXTBOOK_REF)
                .child(booksViewModel.selected_selling.affiliated_account?.user_id.toString() +
                        booksViewModel.selected_selling.book_id.toString())
                .child(KEY_POTENTIAL_BUYERS)
        myRef.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var buyerName = p0.child(KEY_BUYER_NAME)?.value.toString()
                var buyerID = p0.child(KEY_BUYER_ID)?.value as Long
                var buyerApproval = p0.child(KEY_BUYER_APPROVAL)?.value as Boolean

                var size = booksViewModel.selected_selling.potential_buyers!!.size
                var exit_val = false
                if(size > 0) {
                    for (buyer in 0..size) {
                        if (booksViewModel.selected_selling.potential_buyers!![buyer]?.account_id == buyerID) {
                            exit_val = true
                            break
                        }//if
                    }//for
                }

                if(!exit_val) {
                    var newBuyer = Potential_Buyer(buyerID.toString().toLong(), buyerName, buyerApproval)
                    booksViewModel.selected_selling.potential_buyers?.add(newBuyer)
                    UserNamesadapter.notifyDataSetChanged()

                    var ft: FragmentTransaction = this@SellingBookDetailFragment.activity!!.supportFragmentManager.beginTransaction()
                    ft.detach(this@SellingBookDetailFragment)
                    ft.attach(this@SellingBookDetailFragment)
                    ft.commit()
                }

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                var buyerID = p0.child(KEY_BUYER_ID).value as Long
                var buyerApproval = p0.child(KEY_BUYER_APPROVAL).value as Boolean

                var size = booksViewModel.selected_selling.potential_buyers!!.size

                for(buyer in 0 ..size){
                    if(booksViewModel.selected_selling.potential_buyers!![buyer].account_id == buyerID){
                        booksViewModel.selected_selling.potential_buyers!![buyer].approved = buyerApproval
                        break
                    }//if
                }//for

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                databaseManager = FirebaseDatabaseManager()
                databaseManager.retrieveSellingTextbookList(booksViewModel, null)
                UserNamesadapter.notifyDataSetChanged()
                var ft: FragmentTransaction = this@SellingBookDetailFragment.activity!!.supportFragmentManager.beginTransaction()
                ft.detach(this@SellingBookDetailFragment)
                ft.attach(this@SellingBookDetailFragment)
                ft.commit()
            }
        })
        */
    }
}