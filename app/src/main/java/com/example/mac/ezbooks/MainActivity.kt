package com.example.mac.ezbooks

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.DrawerLayout.DrawerListener
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import com.example.mac.ezbooks.ui.main.*
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    //This is for fragments!!!!!
    private lateinit var mainDrawerLayout: DrawerLayout
    private lateinit var booksViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //This sets up the layout for the items in the drawer
        mainDrawerLayout = findViewById(R.id.drawer_layout)

        //Sets up very first viewModel for all fragments to reference
        booksViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)


        //Now time to initialize the navigation click events, will not present anything!!!!
        val navigationView : NavigationView = nav_view
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

        //Sets Up the Home page as the default fragment
        if (savedInstanceState == null) {
            //Defaults home item to checked!!!
            var menuItem = navigationView.menu.getItem(0).subMenu.getItem(0)
            navigationView.setCheckedItem(menuItem)
            onNavigationItemSelected(menuItem)
        }

        search_Textbook_fab.setOnClickListener {
            var menuItem = navigationView.menu.getItem(0).subMenu.getItem(4)
            navigationView.setCheckedItem(menuItem)
            onNavigationItemSelected(menuItem)
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
        //TODO: ADD MORE TO THE displayScreen function

        //This pops all fragments for a clean navigation from the home!!!
        var fragEntryCount = supportFragmentManager.backStackEntryCount
        while(fragEntryCount > 1) {
            supportFragmentManager.popBackStack()
            fragEntryCount--
        }


        val fragment = when (id) {

            R.id.nav_edit_credentials->{
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                        EditAccountFragment()).addToBackStack("editAccout").commit()
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
                supportFragmentManager.beginTransaction().
                        setCustomAnimations( R.anim.abc_fade_in,R.anim.abc_fade_out,
                                R.anim.abc_fade_in,R.anim.abc_fade_out).
                        replace(R.id.flContent,
                        SearchFragment()).addToBackStack("searchShop").commit()
            }
            R.id.nav_change_password->{

            }
            R.id.nav_verify_account->{

            }
            R.id.nav_sign_out->{

            }
            else -> {
                if (supportFragmentManager.backStackEntryCount == 0) {
                    supportFragmentManager.beginTransaction().replace(R.id.flContent,
                            HomeFragment()).addToBackStack("homeFrag").commit()
                }
                else
                    title = "EZ-Books Home"
                    return
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

        }

        //displayScreen(item.itemId)
        mainDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}//Main Activity
