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
import com.example.mac.ezbooks.ui.main.RecyclerView_Adapters.R_B_RecyclerAdapter
import kotlinx.android.synthetic.main.books_for_sale_layout.*
import kotlinx.android.synthetic.main.books_for_sale_layout.view.*

class RequestedBooksFragment : Fragment() {

    private lateinit var booksViewModel: MainViewModel
    private lateinit var requestedBookslayoutManager: RecyclerView.LayoutManager
    private lateinit var requestedBooksRecyclerView: RecyclerView
    private lateinit var requestedBooksRecyclerAdapter: R_B_RecyclerAdapter

    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        //activity?.title = "Textbooks You have Requested"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.books_for_sale_layout, container, false)

        view.uploaded_listings_label1.text = "Requested Books:"
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestedBooksRecyclerView = bfs_page_recycler_view
        requestedBooksRecyclerAdapter = R_B_RecyclerAdapter(this, booksViewModel)
        requestedBooksRecyclerView.adapter = requestedBooksRecyclerAdapter
        requestedBookslayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        requestedBooksRecyclerView.layoutManager = requestedBookslayoutManager
    }

}