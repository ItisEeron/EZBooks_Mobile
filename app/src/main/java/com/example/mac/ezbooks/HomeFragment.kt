package com.example.mac.ezbooks

import android.arch.lifecycle.ViewModelProviders
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.*
import com.example.mac.ezbooks.ui.main.RecyclerView_Adapters.R_B_RecyclerAdapter
import com.example.mac.ezbooks.ui.main.RecyclerView_Adapters.UploadBooksRecyclerAdapter
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.view.*


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var booksViewModel: MainViewModel
    private val storage = FirebaseStorage.getInstance()
    var storageRef = storage.getReference()
    private val databaseManager = FirebaseDatabaseManager()
    private val ACCOUNT_IMG_HEADER = "images/accounts/"

    //For first Requested Books Recycler View!!!!
    private lateinit var requestedbookslayoutManager: RecyclerView.LayoutManager
    private lateinit var requestedbooksadapter: RecyclerView.Adapter<R_B_RecyclerAdapter.ViewHolder>
    private lateinit var requestedBooksRecyclerview : RecyclerView

    //For second Recycler View Posted Listings
    private lateinit var uploadedBookslayoutManager: RecyclerView.LayoutManager
    private lateinit var uploadedBooksadapter: RecyclerView.Adapter<UploadBooksRecyclerAdapter.ViewHolder>
    private lateinit var uploadedBooksRecyclerview : RecyclerView

    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        //Changes Title
        // activity?.title = "EZ-Books Home"
        //Creates the viewModel neccessary for maintaining the data.
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.home_fragment, container, false)

        view.home_card1.card_user_name.text = booksViewModel.user_account.user_name
        view.home_card1.card_user_phone_number.text = booksViewModel.user_account.phone_number
        view.home_card1.card_user_email.text = booksViewModel.user_account.email_address

        databaseManager.getAccountImg(booksViewModel.user_account.user_id!!, view.card_user_image)

        /*
        var load = storageRef.child(ACCOUNT_IMG_HEADER+booksViewModel.user_account.user_id)
        load.downloadUrl.addOnSuccessListener {
            Picasso.with(view.context).load(it.toString()).into(view.card_user_image)
        }.addOnFailureListener {
            Toast.makeText(view.context, "There was an error loading the users image", Toast.LENGTH_SHORT)
        }*/
        //This Shows the Current Accounts Status and Changes the values as necessary
        when(booksViewModel.user_account.account_status){
            1 ->{
                view.home_card1.card_user_status.text = "Good"
                view.home_card1.card_user_status.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            }
            0 ->{
                view.home_card1.card_user_status.text = "Normal"
                view.home_card1.card_user_status.setTextColor(resources.getColor(android.R.color.holo_purple))
            }
            else ->{
                view.home_card1.card_user_status.text = "Bad"
                view.home_card1.card_user_status.setTextColor(resources.getColor(R.color.colorAccent))
            }

        }

        //activity?.title ="Home:"
        view.home_card1.setOnClickListener{
            val fragmentManager = activity?.supportFragmentManager

            //This line allows for the activity to update its navigation drawer to the correctly checked menuItem
            activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_edit_credentials)
            //

            fragmentManager?.beginTransaction()?.
                    setCustomAnimations(R.anim.design_snackbar_in,R.anim.design_snackbar_out)?.replace(R.id.flContent,
                    EditAccountFragment())?.addToBackStack(null)?.commit()
        }

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //This sets up the first set of cards underneath the Requested Books
        requestedBooksRecyclerview = home_requested_books_recyclerview1
        requestedbooksadapter = R_B_RecyclerAdapter(this, booksViewModel)
        requestedBooksRecyclerview.adapter = requestedbooksadapter
        //How to make the Recyclerview horizontal!!!!
        requestedbookslayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        requestedBooksRecyclerview.layoutManager = requestedbookslayoutManager


        //The second set of cards is set up beneath the first set
        uploadedBooksRecyclerview = home_uploaded_books_recyclerview1
        uploadedBooksadapter = UploadBooksRecyclerAdapter(this, booksViewModel)
        uploadedBooksRecyclerview.adapter = uploadedBooksadapter
        uploadedBookslayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        uploadedBooksRecyclerview.layoutManager = uploadedBookslayoutManager

    }

}