package com.example.mac.ezbooks

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.DrawerLayout.DrawerListener
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.loginAccount.ChangePasswordActivity
import com.example.mac.ezbooks.loginAccount.ChangePasswordFragment
import com.example.mac.ezbooks.loginAccount.VerifyAccount
import com.example.mac.ezbooks.ui.main.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*



/*
The Main Activity. Currently, the only part of the firebase database that is read is currently
a single account from a user. You should be able to edit this. If you rerun the app, you will notice
that the credentials of the account will have changed to the new account.

Textbooks are builtin in, they still need to be populated into the database. I also still need
to set these values into the database.

Also, I still need to implemented a login feature and a create account feature. The account currently
viewed is one already inputted into the database.
 */

class MainActivity : AppCompatActivity()  {
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
    private val database = FirebaseDatabase.getInstance()

    //This is for fragments!!!!!
    private lateinit var mainDrawerLayout: DrawerLayout
    private lateinit var booksViewModel: MainViewModel
    private lateinit var databaseManager: FirebaseDatabaseManager
    private lateinit var navigationView : NavigationView
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mAuth = FirebaseAuth.getInstance()
        val mUser = mAuth!!.currentUser
        databaseManager = FirebaseDatabaseManager()
        //This sets up the layout for the items in the drawer
        mainDrawerLayout = findViewById(R.id.drawer_layout)

        //Sets up very first viewModel for all fragments to reference
        booksViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        booksViewModel.getAllTextbooks(mUser!!.uid, mUser!!.displayName, mUser!!.email)
        //Now time to initialize the navigation click events, will not present anything!!!!
        navigationView = nav_view
        //Sets up the navigation menu from main Drawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            //TODO: Set the home button off when the first value clicked is not home!!!
            onNavigationItemSelected(menuItem)
            mainDrawerLayout.closeDrawers()
            true
        }//navigationView.setNavigationItemSelectedListener

        //Sets up Action bar at top of homepage.. You can add more to this
        //TODO: Add more to the Action bar if you wish---Erase when finished
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar : ActionBar? = supportActionBar
        actionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
            //TODO: Change this Icon to navigation drawer
            setHomeAsUpIndicator(R.drawable.if_menu_alt_134216)
        }//actionBar?.apply

        mainDrawerLayout.addDrawerListener(
                object : DrawerListener {
                    override fun onDrawerStateChanged(p0: Int) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDrawerSlide(p0: View, p1: Float) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDrawerClosed(p0: View) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDrawerOpened(p0: View) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }
        )

        search_Textbook_fab.setOnClickListener {
            var menuItem = navigationView.menu.getItem(0).subMenu.getItem(4)
            navigationView.setCheckedItem(menuItem)
            onNavigationItemSelected(menuItem)
        }
        //Testing Database info
        //databaseManager.retrieveAccount(104955L, booksViewModel)

        //Testing Database info
       //databaseManager.retrieveRequestedTextbookList(104955L, booksViewModel, null, null)
        var myRef1 = database.getReference(KEY_ACCOUNT)
                .child(mUser.uid).child(KEY_REQ_BOOKS)

        myRef1.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var requested_book_ids : ArrayList<String> = arrayListOf()
                var id_iter = snapshot.children
                for (textbook in id_iter){
                    requested_book_ids.add(textbook.key!!)
                }
                var newRef = database.getReference(KEY_TEXTBOOK_REF)
                newRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        booksViewModel.recent_requested_Textbooks.clear()
                        booksViewModel.requested_textbooks.clear()
                        var textbook_iter = snapshot.children
                        for(textbook in textbook_iter){
                            if(requested_book_ids.contains(textbook.key)){
                                val userid = textbook.child(KEY_ACCOUNT).value.toString()
                                val bookid = textbook.child(KEY_BOOK_ID).value as Long
                                val user_name = textbook.child(KEY_NAME).getValue(String::class.java)
                                val title = textbook.child(KEY_TITLE).getValue(String::class.java)
                                val isbn = textbook.child(KEY_ISBN).getValue(String::class.java)
                                val course = textbook.child(KEY_COURSE).getValue(String::class.java)
                                val instructor = textbook.child(KEY_INSTRUCTOR).getValue(String::class.java)
                                val user_email = textbook.child(KEY_EMAIL).getValue(String::class.java)
                                val user_phone = textbook.child(KEY_PHONE).getValue(String::class.java)
                                var potential_Buyer : MutableList<Potential_Buyer> = mutableListOf()
                                val book_img_string = textbook.child(KEY_BOOK_IMG).getValue(String::class.java)
                                var book_img : ByteArray? = null
                                if(book_img_string != null){
                                    book_img = Base64.decode(book_img_string, Base64.DEFAULT)
                                }

                                val buyers_iter = textbook.child(KEY_POTENTIAL_BUYERS).children
                                for (buyer in buyers_iter) {
                                    if(buyer != null) {
                                        var buyerName: String?
                                        var buyerID: String?
                                        var buyerApproval: Boolean = false

                                        buyerID = buyer.child(KEY_BUYER_ID).value.toString()
                                        buyerName = buyer.child(KEY_BUYER_NAME).value as String
                                        buyerApproval = buyer.child(KEY_BUYER_APPROVAL).value as Boolean

                                        potential_Buyer.add(Potential_Buyer(buyerID!!, buyerName!!, buyerApproval!!))
                                    }
                                }
                                booksViewModel.requested_textbooks.add(Searched_Textbooks(userid, bookid, user_name, user_email, user_phone,title, isbn,
                                        course, instructor, book_img, potential_Buyer))
                                Log.i("Eeron Size: " , potential_Buyer.size.toString())
                            }
                        }

                        booksViewModel.requested_textbooks.reverse()

                        if(booksViewModel.requested_textbooks.size > 5) {
                            booksViewModel.recent_requested_Textbooks.addAll(booksViewModel.requested_textbooks
                                    .subList(0, 5))
                        }
                        else {
                            booksViewModel.recent_requested_Textbooks.addAll(booksViewModel.requested_textbooks)
                        }

                        if(supportFragmentManager.findFragmentById(R.id.flContent) == null) {
                            var menuItem = navigationView.menu.getItem(0).subMenu.getItem(0)
                            navigationView.setCheckedItem(menuItem)
                            onNavigationItemSelected(menuItem)
                        } else{

                            var fragment = supportFragmentManager.findFragmentById(R.id.flContent)
                            var ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                            ft.detach(fragment!!)
                            ft.attach(fragment!!)
                            ft.commit()
                        }

                    }
                })

            }
        })

       //databaseManager.retrieveSellingTextbookList(104955L, booksViewModel, null)
        var myRef2 = database.getReference(KEY_ACCOUNT)
                .child(mUser.uid).child(KEY_TEXTBOOK)
        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot != null){
                    booksViewModel.recent_selling_Textbooks.clear()
                    booksViewModel.selling_textbooks.clear()
                    val textbook_snap = snapshot.children
                    var iter = textbook_snap.iterator()

                    for (item in iter) {
                        //val textbooks = item.children
                        //var textbookIter = textbooks.iterator()
                        //for(textbook in textbookIter) {
                        var potential_buyers : MutableList<Potential_Buyer>? = mutableListOf()
                        println("Eeron ${item}")
                        //val userid = item.child(KEY_ACCOUNT).value as Long
                        val bookid = item.child(KEY_BOOK_ID).value as Long
                        //val user_name = item.child(KEY_NAME).getValue(String::class.java)
                        val title = item.child(KEY_TITLE).getValue(String::class.java)
                        val isbn = item.child(KEY_ISBN).getValue(String::class.java)
                        val course = item.child(KEY_COURSE).getValue(String::class.java)
                        val instructor = item.child(KEY_INSTRUCTOR).getValue(String::class.java)
                        val book_img_string = item.child(KEY_BOOK_IMG).getValue(String::class.java)

                        var book_img : ByteArray? = null
                        if (book_img_string != null) {
                            book_img = Base64.decode(book_img_string, Base64.DEFAULT)
                        }
                        //val user_email = textbook.child(KEY_EMAIL).getValue(String::class.java)
                        //val user_phone = textbook.child(KEY_PHONE).getValue(String::class.java)

                        val buyers_iter = item.child(KEY_POTENTIAL_BUYERS).children
                        for (buyer in buyers_iter) {
                            if(buyer != null) {
                                var buyerName: Any?
                                var buyerID: Any?
                                var buyerApproval: Boolean = false

                                buyerID = buyer.child(KEY_BUYER_ID).value
                                buyerName = buyer.child(KEY_BUYER_NAME).value
                                buyerApproval = buyer.child(KEY_BUYER_APPROVAL).value as Boolean

                                if(buyerID != null && buyerName != null) {
                                    potential_buyers?.add(Potential_Buyer(buyerID.toString(), buyerName.toString(), buyerApproval!!))
                                }
                            }
                        }
                        var aTextbook : Textbooks? = null
                        if(bookid != null && title != null && isbn != null){
                            aTextbook = Textbooks( bookid, title!!, isbn!!, book_img, instructor, course,
                                    booksViewModel.user_account, potential_buyers)
                            booksViewModel.selling_textbooks.add(aTextbook)
                        }
                        //}
                    }//for

                    booksViewModel.selling_textbooks.reverse()

                    if(booksViewModel.selling_textbooks.size > 5) {
                        booksViewModel.recent_selling_Textbooks.addAll(booksViewModel.selling_textbooks
                                .subList(0, 5))
                    }
                    else {
                        booksViewModel.recent_selling_Textbooks.addAll(booksViewModel.selling_textbooks)
                    }

                    if(supportFragmentManager.findFragmentById(R.id.flContent) == null) {
                        var menuItem = navigationView.menu.getItem(0).subMenu.getItem(0)
                        navigationView.setCheckedItem(menuItem)
                        onNavigationItemSelected(menuItem)
                    } else{

                        var fragment = supportFragmentManager.findFragmentById(R.id.flContent)
                        var ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                        ft.detach(fragment!!)
                        ft.attach(fragment!!)
                        ft.commit()
                    }

                }
            }

        })

    //Keeps the navigation drawer in the correct placement when back is pressed
    supportFragmentManager.addOnBackStackChangedListener {
        var fragment = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount -1).name
        when (fragment){
            "homeFrag"->{
                var menuItem = navigationView.menu.getItem(0).subMenu.getItem(0)
                navigationView.setCheckedItem(menuItem)
            }
            "uploadBook"->{
                var menuItem = navigationView.menu.getItem(0).subMenu.getItem(1)
                navigationView.setCheckedItem(menuItem)
            }
            "requestedBooks"->{
                var menuItem = navigationView.menu.getItem(0).subMenu.getItem(2)
                navigationView.setCheckedItem(menuItem)
            }
            "booksforSale"->{
                var menuItem = navigationView.menu.getItem(0).subMenu.getItem(3)
                navigationView.setCheckedItem(menuItem)
            }
            "searchShop"->{
                var menuItem = navigationView.menu.getItem(0).subMenu.getItem(4)
                navigationView.setCheckedItem(menuItem)
            }
            "editAccount"->{
                var menuItem = navigationView.menu.getItem(1).subMenu.getItem(0)
                navigationView.setCheckedItem(menuItem)
            }
            "verifyAccount"->{
                var menuItem = navigationView.menu.getItem(1).subMenu.getItem(1)
                navigationView.setCheckedItem(menuItem)
            }
            "changePassword"->{
                var menuItem = navigationView.menu.getItem(1).subMenu.getItem(2)
                navigationView.setCheckedItem(menuItem)
            }
        }

    }


    }//onCreate

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                mainDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }//return
    }//onOptionsItemSelected


    //This function will display the different screens stemming from the main activity
    fun displayScreen(id: Int) {

       when (id) {

            R.id.nav_edit_credentials->{
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                        EditAccountFragment()).addToBackStack("editAccount").commit()
            }
            R.id.nav_add_listing->{
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                        R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                        UploadBookFragment()).addToBackStack("uploadBook").commit()
            }
            R.id.nav_requested_books->{
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                        RequestedBooksFragment()).addToBackStack("requestedBooks").commit()
            }
            R.id.nav_books_you_sell->{
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                        BooksForSaleFragment()).addToBackStack("booksforSale").commit()
            }
            R.id.nav_search_shop->{
                //startActivity(Intent(this, SearchActivity::class.java))
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                        SearchFragment()).addToBackStack("searchShop").commit()

            }
            R.id.nav_change_password->{
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                                ChangePasswordFragment()).addToBackStack("changePassword").commit()
            }
            R.id.nav_verify_account->{
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                                VerifyAccount()).addToBackStack("verifyAccount").commit()
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    this.finishAffinity()
                }
                else{
                    ActivityCompat.finishAffinity(this)
                }
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            else -> {
                supportFragmentManager.beginTransaction().replace(R.id.flContent,
                        HomeFragment()).addToBackStack("homeFrag").commit()
                title = "EZ-Books Home"


            }
        }

    }//displayScreen

    fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            //TODO: ADD different pages for main fragment
            R.id.nav_home -> {
                displayScreen(item.itemId)

            }
            R.id.nav_edit_credentials->{
                displayScreen(item.itemId)
            }
            R.id.nav_add_listing->{
                displayScreen(item.itemId)
            }
            R.id.nav_requested_books->{
                displayScreen(item.itemId)
            }
            R.id.nav_books_you_sell->{
                displayScreen(item.itemId)
            }
            R.id.nav_search_shop->{
                displayScreen(item.itemId)
            }
            R.id.nav_change_password-> {
                displayScreen(item.itemId)
            }
            R.id.nav_verify_account-> {
                displayScreen(item.itemId)
            }
            R.id.nav_sign_out->{
                displayScreen(item.itemId)
            }

        }

        //displayScreen(item.itemId)
        mainDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1){
            supportFragmentManager.popBackStack()
        }
        super.onBackPressed()
    }

}//Main Activity
