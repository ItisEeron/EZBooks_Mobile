package com.example.mac.ezbooks.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac.ezbooks.R

class RequestedBooksFragment : Fragment() {

    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        activity?.title = "Check the textbooks you have requested"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.requested_books_page_layout, container, false)
        return view
    }

}