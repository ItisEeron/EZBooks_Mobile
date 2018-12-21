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
import com.example.mac.ezbooks.ui.main.Searched_Textbooks
import kotlinx.android.synthetic.main.report_user_layout.*
import kotlinx.android.synthetic.main.report_user_layout.view.*
import java.io.Serializable


class ReportUserFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var spinner : Spinner
    private lateinit var reportString : String
    private lateinit var booksViewModel : MainViewModel
    private var firebaseDatabaseManager = FirebaseDatabaseManager()
    private var textbook : Searched_Textbooks? = null
    private var textbook_arg : Serializable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")
        textbook_arg = arguments?.getSerializable("textbook")
        if(textbook_arg != null)
            textbook = textbook_arg as Searched_Textbooks
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.report_user_layout, container, false)

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
        if(textbook != null){
            view.reported_user_name.text = textbook!!.user_name
            view.reported_user_image.setImageDrawable(resources.getDrawable(R.mipmap.ic_launcher_round))
            firebaseDatabaseManager.getAccountImg(textbook!!.userid!!, view.reported_user_image)
        }


        //Now time to initialize the buttons
        view.submit_report_button.setOnClickListener {
            if (reportString != null) {
                var other_reason = view.other_explaination.text.toString()
                booksViewModel.reportedBooks.add(textbook?.userid + textbook?.bookid.toString())
                firebaseDatabaseManager.reportUser(textbook!!, reportString,
                        if(other_reason.isEmpty()) null else other_reason, booksViewModel.user_account)
                firebaseDatabaseManager.removeRequest(booksViewModel, textbook!!)

                Toast.makeText(activity,
                        "You have reported " + textbook!!.user_name + ".",
                        Toast.LENGTH_LONG).show()

                fragmentManager?.popBackStack()
                fragmentManager?.popBackStack()

            }//if
            else{
                Toast.makeText(activity,
                        "Please choose a reason why you are reporting " +
                                textbook!!.user_name +"." , Toast.LENGTH_LONG).show()}
        }

        view.cancel_report_button.setOnClickListener{
            fragmentManager?.popBackStack()
        }
        return view
    }

    //Since View may be already created, a view of null will be passed if this view is already
    //created
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
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