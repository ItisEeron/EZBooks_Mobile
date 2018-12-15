package com.example.mac.ezbooks


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.mac.ezbooks.ui.main.RecyclerView_Adapters.SearchAdapter
import com.google.firebase.auth.FirebaseUser
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.Potential_Buyer
import com.example.mac.ezbooks.ui.main.Searched_Textbooks


class SearchFragment :  Fragment() {

    private val KEY_ACCOUNT = "account"
    private val KEY_TEXTBOOK_REF = "textbook_ref"
    private val KEY_BOOK_ID = "book_id"
    private val KEY_NAME = "name"
    private val KEY_TITLE = "title"
    private val KEY_ISBN = "isbn"
    private val KEY_INSTRUCTOR = "instructor"
    private val KEY_COURSE = "course"
    private val KEY_BOOK_IMG = "book_img"
    private val KEY_POTENTIAL_BUYERS = "potential_buyers"
    private val KEY_BUYER_ID = "buyer_id"
    private val KEY_BUYER_NAME = "buyer_name"
    private val KEY_BUYER_APPROVAL = "buyer_approval"
    private val KEY_PHONE = "phone_number"
    private val KEY_EMAIL = "email"


    var search_edit_text: EditText? = null
    var recyclerView: RecyclerView? = null
    var databaseReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var searchedQuery : ArrayList<Searched_Textbooks>? = null
    lateinit var booksViewModel : MainViewModel

    var searchAdapter: SearchAdapter? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        //Changes Title

        //activity?.title = "Search for a Book"
        //Creates the viewModel neccessary for maintaining the data.
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_search, container, false)


        search_edit_text = view.findViewById(R.id.search_edit_text)
        recyclerView = view.findViewById(R.id.recyclerView)

        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(this.context))
        recyclerView?.addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))

        /*
        * Create a array list for each node you want to use
        * */
        searchedQuery = ArrayList()


        recyclerView?.adapter = SearchAdapter(this, searchedQuery!!)


        search_edit_text?.addTextChangedListener( object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString())
                } else {
                    /*
                    * Clear the list when editText is empty
                    * */
                    searchedQuery?.clear()
                }
            }

        })
        return view
    }

    private fun setAdapter(searchString : String ){
        databaseReference?.child(KEY_TEXTBOOK_REF)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                /*
                * Clear the list for every new search
                * */
                searchedQuery?.clear()

                recyclerView?.removeAllViews()

                var counter = 0
                var iter = snapshot.children
                for (textbook in iter){
                    var exit = false

                    val userid = textbook.child(KEY_ACCOUNT).value.toString()
                    val user_email = textbook.child(KEY_EMAIL).getValue(String::class.java)
                    val user_phone = textbook.child(KEY_PHONE).getValue(String::class.java)
                    val bookid = textbook.child(KEY_BOOK_ID).value as Long
                    val user_name = textbook.child(KEY_NAME).getValue(String::class.java)
                    val title = textbook.child(KEY_TITLE).getValue(String::class.java)
                    val isbn = textbook.child(KEY_ISBN).getValue(String::class.java)
                    val course = textbook.child(KEY_COURSE).getValue(String::class.java)
                    val instructor = textbook.child(KEY_INSTRUCTOR).getValue(String::class.java)
                    val book_img_string = textbook.child(KEY_BOOK_IMG).getValue(String::class.java)
                    var book_img : ByteArray? = null

                    if(book_img_string != null)
                        book_img =  Base64.decode(book_img_string, Base64.DEFAULT)

                    if(userid == booksViewModel.user_account.user_id)
                        exit = true

                    val potential_Buyer : MutableList<Potential_Buyer> = mutableListOf()

                    val buyers_iter = textbook.child(KEY_POTENTIAL_BUYERS).children
                    for (buyer in buyers_iter) {
                        var buyerName : String?
                        var buyerID : String?
                        var buyerApproval : Boolean = false

                        buyerID = buyer.child(KEY_BUYER_ID).value.toString()
                        buyerName =  buyer.child(KEY_BUYER_NAME).value as String
                        buyerApproval =  buyer.child(KEY_BUYER_APPROVAL).value as Boolean

                        if(buyerID == booksViewModel.user_account.user_id)
                            exit = true

                        potential_Buyer.add(Potential_Buyer(buyerID!!, buyerName!!, buyerApproval!!))
                    }

                    //TODO: MAKE IT SO ALREADY REQUESTED ITEMS DONT QUEREY ALSO FOR ITEMS BELONGING TO OWNER
                    if(title!!.toLowerCase().contains(searchString.toLowerCase()) ||
                            isbn!!.toLowerCase().contains(searchString.toLowerCase()) ||
                            course!!.toLowerCase().contains(searchString.toLowerCase())||
                            instructor!!.toLowerCase().contains(searchString.toLowerCase())) {
                        if(exit == false) {
                            searchedQuery?.add(Searched_Textbooks(userid, bookid, user_name, user_email, user_phone, title, isbn,
                                    course, instructor, potential_Buyer))
                            counter++
                        }

                    }
                    if (counter == 100)
                        break

                }
                searchAdapter = SearchAdapter(this@SearchFragment, searchedQuery!!)
                recyclerView?.setAdapter(searchAdapter)
            }

        })

    }

}

