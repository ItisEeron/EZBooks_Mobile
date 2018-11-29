package com.example.mac.ezbooks.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.ui.main.RecyclerView_Adapters.UploadBooksRecyclerAdapter
import kotlinx.android.synthetic.main.books_for_sale_layout.*

class BooksForSaleFragment : Fragment() {
    private lateinit var booksViewModel : MainViewModel
    private lateinit var main_Account: UserAccount

    private lateinit var uploadedBookslayoutManager: RecyclerView.LayoutManager
    private lateinit var uploadedBooksadapter: RecyclerView.Adapter<UploadBooksRecyclerAdapter.ViewHolder>
    private lateinit var uploadedBooksRecyclerview : RecyclerView

    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        activity?.title = "Uploaded Textbook Listing"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.books_for_sale_layout, container, false)

        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

        main_Account = booksViewModel.user_account

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        uploadedBooksRecyclerview = bfs_page_recycler_view
        uploadedBooksadapter = UploadBooksRecyclerAdapter(this, booksViewModel)
        uploadedBooksRecyclerview.adapter = uploadedBooksadapter
        uploadedBookslayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        uploadedBooksRecyclerview.layoutManager = uploadedBookslayoutManager
    }

}