package com.example.mac.ezbooks.detail_fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.example.mac.ezbooks.R
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.UserAccount
import kotlinx.android.synthetic.main.report_user_layout.*
import kotlinx.android.synthetic.main.report_user_layout.view.*


class ReportUserFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var spinner : Spinner
    private lateinit var reportString : String
    private lateinit var booksViewModel : MainViewModel
    private lateinit var reported_Account : UserAccount
    private var firebaseDatabaseManager = FirebaseDatabaseManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.report_user_layout, container, false)
        val RECENTS_SIZE = 5
        //Always get the main viewModel
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

        //Initialize the Spinner
        spinner = view.findViewById(R.id.reported_reasons_spinner)
        spinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
                view.context,
                R.array.report_reasons_array,
                android.R.layout.simple_spinner_item

        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        //Time to initialze the remaining view
        //Add to the name and the image. That is all!!
        view.reported_user_name.text = booksViewModel.selected_requested.user_name
        view.reported_user_image.setImageDrawable(resources.getDrawable(R.mipmap.ic_launcher_round))
        firebaseDatabaseManager.getAccountImg(booksViewModel.selected_requested.userid!!, view.reported_user_image)

        //Now time to initialize the buttons
        view.submit_report_button.setOnClickListener {
            if (reportString != null) {
                var other_reason = view.other_explaination.text.toString()

                firebaseDatabaseManager.reportUser(booksViewModel, reportString,
                        if(other_reason.isEmpty()) null else other_reason)
                firebaseDatabaseManager.removeRequest(booksViewModel, booksViewModel.selected_requested)

                //Now Remove the Book So the User does not have to deal with an unwanted seller!!
                /*if(booksViewModel.recent_requested_Textbooks.contains(booksViewModel.selected_requested)){
                    booksViewModel.recent_requested_Textbooks.remove(booksViewModel.selected_requested)

                    if(booksViewModel.requested_textbooks.size > RECENTS_SIZE ){
                        booksViewModel.recent_requested_Textbooks.add(booksViewModel.requested_textbooks[RECENTS_SIZE-1])
                    }
                }
                booksViewModel.requested_textbooks.remove(booksViewModel.selected_requested)*/

                Toast.makeText(activity,
                        "You have reported " + booksViewModel.selected_requested.user_name + ".",
                        Toast.LENGTH_LONG).show()

                fragmentManager?.popBackStack()
                fragmentManager?.popBackStack()

            }//if
            else{
                Toast.makeText(activity,
                        "Please choose a reason why you are reporting " +
                                booksViewModel.selected_requested.user_name +"." , Toast.LENGTH_LONG).show()}
        }

        view.cancel_report_button.setOnClickListener{
            fragmentManager?.popBackStack()
        }
        return view
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        reportString = parent.getItemAtPosition(pos) as String

        //Show other_explanation if neccessary. Otherwise, do not include it in the view
        other_explaination.visibility = if (reportString  == "Other")
           View.VISIBLE else View.GONE
        if_other_explain_label.visibility = if (reportString  == "Other")
            View.VISIBLE else View.GONE

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
        other_explaination.visibility = View.GONE
        if_other_explain_label.visibility = View.GONE
    }

}