package com.example.mac.ezbooks.detail_fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class UploadUserImageFragment : Fragment() {
    lateinit var image: ImageView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}