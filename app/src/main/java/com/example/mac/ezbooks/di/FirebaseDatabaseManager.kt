package com.example.mac.ezbooks.di

import android.app.Activity
import android.graphics.BitmapFactory
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.ui.main.*
import com.google.firebase.database.*


class FirebaseDatabaseManager (){
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
    private val KEY_OTHERS_LOG = "others_log"
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


    fun createUser(id: String, name: String, email: String) {
        val myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(id).child(KEY_ID).setValue(id)
        myRef.child(id).child(KEY_NAME).setValue(name)
        myRef.child(id).child(KEY_EMAIL).setValue(email)

    }

    fun createTextbook(user_account: UserAccount, textbook: Textbooks) {
        val book_id = textbook.book_id.toString()
        var myRef = database.getReference(KEY_ACCOUNT).child(user_account.user_id!!).child(KEY_TEXTBOOK)
        myRef.child(book_id).child(KEY_BOOK_ID).setValue(textbook.book_id)
        myRef.child(book_id).child(KEY_TITLE).setValue(textbook.Title)
        myRef.child(book_id).child(KEY_ISBN).setValue(textbook.isbn)
        myRef.child(book_id).child(KEY_INSTRUCTOR).setValue(textbook.instructor)
        myRef.child(book_id).child(KEY_COURSE).setValue(textbook.course)



        //convert book_img into something readable by firebase
        var book_img : String? = null
        if(textbook.book_img != null) {
            book_img = Base64.encodeToString(textbook.book_img, Base64.NO_WRAP)
        }
        myRef.child(book_id).child(KEY_BOOK_IMG).setValue(book_img)

        //Now create the reference for searching purposes
        myRef = database.getReference(KEY_TEXTBOOK_REF)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_ACCOUNT).setValue(user_account.user_id!!)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_NAME).setValue(textbook.affiliated_account!!.user_name)
        myRef.child(user_account.user_id.toString()+ textbook.book_id.toString()).child(KEY_BOOK_ID).setValue(textbook.book_id)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_TITLE).setValue(textbook.Title)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_ISBN).setValue(textbook.isbn)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_INSTRUCTOR).setValue(textbook.instructor)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_COURSE).setValue(textbook.course)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_BOOK_IMG).setValue(book_img)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_EMAIL).setValue(user_account.email_address)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_PHONE).setValue(user_account.phone_number)


    }

    fun updateAccount(account : UserAccount){
        val myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(account.user_id.toString())
                .child(KEY_NAME).setValue(account.user_name)
        myRef.child(account.user_id.toString())
                .child(KEY_EMAIL).setValue(account.email_address)
        myRef.child(account.user_id.toString())
                .child(KEY_CLASS).setValue(account.class_standing)
        myRef.child(account.user_id.toString())
                .child(KEY_PHONE).setValue(account.phone_number)

        //convert profile_image into something readable by firebase
        if(account.profile_img != null ) {
            val prof_image = Base64.encodeToString(account.profile_img, Base64.NO_WRAP)
            myRef.child(account.user_id.toString())
                    .child(KEY_PROF).setValue(prof_image)
        }
    }

    fun removeTextbook(textbook: Searched_Textbooks){
        var myRef = database.getReference(KEY_ACCOUNT).child(textbook.userid.toString()).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString())
        myRef.removeValue()

        myRef = database.getReference(KEY_TEXTBOOK_REF).child(textbook.userid.toString() + textbook.userid.toString())
        myRef.removeValue()
    }

    //Read from database
    fun retrieveAccount(id : String , viewModel: MainViewModel, navigationView: NavigationView?){
        val myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                inputAccount(snapshot, viewModel, null)

                //Set up Navigation Header//////////////////////////////////////////////////////////
                navigationView?.getHeaderView(0)?.
                        findViewById<TextView>(R.id.user_header_name)
                        ?.text = viewModel.user_account.user_name

                if(viewModel.user_account.profile_img != null){
                    var bitmap = BitmapFactory.
                            decodeByteArray(viewModel.user_account.profile_img,
                                    0, viewModel.user_account.profile_img!!.size)

                    navigationView?.getHeaderView(0)?.
                            findViewById<ImageView>(R.id.user_header_image)?.setImageBitmap(bitmap)
                }
                ////////////////////////////////////////////////////////////////////////////////////

            }
        })

    }

    /*
    fun retrieveRequestedTextbookList(user_id: String,viewModel: MainViewModel, fragment:Fragment?){
        var myRef = database.getReference(KEY_ACCOUNT)
                .child(user_id).child(KEY_REQ_BOOKS)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var requested_book_ids : ArrayList<String> = arrayListOf()
                var id_iter = snapshot.children
                viewModel.recent_requested_Textbooks.clear()
                viewModel.requested_textbooks.clear()
                for (textbook in id_iter){
                   requested_book_ids.add(textbook.key!!)
                }
                var newRef = database.getReference(KEY_TEXTBOOK_REF)
                newRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var textbook_iter = snapshot.children
                        for(textbook in textbook_iter){
                            if(requested_book_ids.contains(textbook.key)){
                                val userid = textbook.child(KEY_ACCOUNT).value as String
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

                                        buyerID = buyer.child(KEY_BUYER_ID).value as String
                                        buyerName = buyer.child(KEY_BUYER_NAME).value as String
                                        buyerApproval = buyer.child(KEY_BUYER_APPROVAL).value as Boolean

                                        potential_Buyer.add(Potential_Buyer(buyerID!!, buyerName!!, buyerApproval!!))
                                    }
                                }
                                viewModel.requested_textbooks.add(Searched_Textbooks(userid, bookid, user_name, user_email, user_phone,title, isbn,
                                        course, instructor, book_img, potential_Buyer))
                                Log.i("Eeron Size: " , potential_Buyer.size.toString())
                            }
                        }

                        viewModel.requested_textbooks.reverse()

                        if(viewModel.requested_textbooks.size > 5) {
                            viewModel.recent_requested_Textbooks.addAll(viewModel.requested_textbooks
                                    .subList(0, 5))
                        }
                        else {
                            viewModel.recent_requested_Textbooks.addAll(viewModel.requested_textbooks)
                        }

                        if (fragment != null) {
                            //recyclerAdapter!!.notifyDataSetChanged()
                            var ft: FragmentTransaction = fragment.activity!!.supportFragmentManager.beginTransaction()
                            ft.detach(fragment)
                            ft.attach(fragment)
                            ft.commit()
                        }

                    }
                })

            }
        })

    }

    fun retrieveSellingTextbookList(user_id: String, viewModel: MainViewModel, fragment: Fragment?, activity: Activity?) {
        var myRef = database.getReference(KEY_ACCOUNT)
                .child(user_id).child(KEY_TEXTBOOK)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot != null){
                    viewModel.recent_selling_Textbooks.clear()
                    viewModel.selling_textbooks.clear()
                    val textbook_snap = snapshot.children
                    var iter = textbook_snap.iterator()

                    for (item in iter) {
                        //val textbooks = item.children
                        //var textbookIter = textbooks.iterator()
                        //for(textbook in textbookIter) {
                            var potential_buyers : MutableList<Potential_Buyer>? = mutableListOf()
                            println("Eeron ${item}")
                            val userid = item.child(KEY_ACCOUNT).getValue(String::class.java)
                            val bookid = item.child(KEY_BOOK_ID).value as Long
                            val user_name = item.child(KEY_NAME).getValue(String::class.java)
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
                                viewModel.user_account, potential_buyers)
                                viewModel.selling_textbooks.add(aTextbook)
                            }
                        //}
                    }//for

                    viewModel.selling_textbooks.reverse()

                    if(viewModel.selling_textbooks.size > 5) {
                        viewModel.recent_selling_Textbooks.addAll(viewModel.selling_textbooks
                                .subList(0, 5))
                    }
                    else {
                        viewModel.recent_selling_Textbooks.addAll(viewModel.selling_textbooks)
                    }


                    if(fragment != null) {
                        var ft: FragmentTransaction = fragment.activity!!.supportFragmentManager.beginTransaction()
                        ft.detach(fragment)
                        ft.attach(fragment)
                        ft.commit()
                    }

                }
            }

        })
    }
*/

    fun sendRequest(user_name: String, user_id : String ,owner_id : String , book_id : Long){
        var myRef = database.getReference(KEY_TEXTBOOK_REF).child((owner_id + book_id.toString()))
        myRef.child(KEY_POTENTIAL_BUYERS).child(user_id).child(KEY_BUYER_APPROVAL).setValue(false)
        myRef.child(KEY_POTENTIAL_BUYERS).child(user_id).child(KEY_BUYER_NAME).setValue(user_name)
        myRef.child(KEY_POTENTIAL_BUYERS).child(user_id).child(KEY_BUYER_ID).setValue(user_id)


        myRef = database.getReference(KEY_ACCOUNT).child(owner_id).child(KEY_TEXTBOOK)
                .child(book_id.toString()).child(KEY_POTENTIAL_BUYERS)
        myRef.child(user_id).child(KEY_BUYER_APPROVAL).setValue(false)
        myRef.child(user_id).child(KEY_BUYER_NAME).setValue(user_name)
        myRef.child(user_id).child(KEY_BUYER_ID).setValue(user_id)

        //Now add the request to user account for reference
        myRef = database.getReference(KEY_ACCOUNT).child(user_id).child(KEY_REQ_BOOKS)
                .child(owner_id + book_id.toString())

        myRef.child(KEY_ID).setValue(owner_id)
        myRef.child(KEY_BOOK_ID).setValue(book_id)

        //Finally set the approval in the buyers account to false
        myRef = database.getReference(KEY_ACCOUNT).child(user_id).child(KEY_REQ_BOOKS)
                .child(owner_id + book_id.toString()).child(KEY_BUYER_APPROVAL)
        myRef.setValue(false)

    }

    fun inputAccount (snapshot: DataSnapshot, viewModel: MainViewModel, textbook: Textbooks?){
        val accounts = snapshot.children
        val iter = accounts.iterator()
        var id: String? = null
        var name: String? = null
        var phone_number: String? = null
        var email: String? = null
        var class_standing: String? = null
        var prof_img: ByteArray? = null
        var reported_flags = 0L
        for (item in iter) {
            println("Eeron ${item}")
            when (item.key) {
                KEY_ID -> {
                    id = item.value as String
                }
                KEY_PHONE -> {
                    phone_number = item.value.toString()
                }
                KEY_CLASS -> {
                    class_standing = item.value.toString()
                }
                KEY_EMAIL -> {
                    email = item.value as String
                }
                KEY_NAME -> {
                    name = item.value as String
                }
                KEY_PROF -> {
                    val prof_img_string = item.value.toString()
                    if(prof_img_string.isNotEmpty())
                        prof_img = Base64.decode(prof_img_string, Base64.DEFAULT)
                }
                KEY_REPORTS -> {
                    val reports = item.children
                    for(report in reports){
                        if(report.key != "others_log"){
                            reported_flags = reported_flags + report.value.toString().toLong()
                        }
                    }
                }
            }//when
        }//for


        if(textbook != null){
            textbook.affiliated_account = UserAccount(id, prof_img, name, email, phone_number,
                    "", if(reported_flags > 20) -1 else 1, class_standing)
        }else {
            viewModel.user_account = UserAccount(id, prof_img, name, email, phone_number,
                    "", if(reported_flags > 20) -1 else 1, class_standing)
        }

        Log.d("Eeron Tag: ", name + " " + email + " " + phone_number)

    }//input_account

    fun removeRequest(viewModel: MainViewModel, textbook: Searched_Textbooks){
        var myRef = database.getReference(KEY_ACCOUNT).child(textbook.userid.toString()).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString()).child(KEY_POTENTIAL_BUYERS)
                .child(viewModel.user_account.user_id.toString())
        myRef.removeValue()

        myRef = database.getReference(KEY_TEXTBOOK_REF).child(textbook.userid.toString() + textbook.bookid.toString())
                .child(KEY_POTENTIAL_BUYERS).child(viewModel.user_account.user_id.toString())
        myRef.removeValue()

        myRef = database.getReference(KEY_ACCOUNT).child(viewModel.user_account.user_id.toString())
                .child(KEY_REQ_BOOKS).child(textbook.userid.toString() + textbook.bookid.toString())
        myRef.removeValue()

    }

    fun removeOtherUser(textbook: Searched_Textbooks, removed : Potential_Buyer){
        var myRef = database.getReference(KEY_ACCOUNT).child(textbook.userid!!).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString()).child(KEY_POTENTIAL_BUYERS)
                .child(removed.account_id)
        myRef.removeValue()

        myRef = database.getReference(KEY_TEXTBOOK_REF).child(textbook.userid + textbook.bookid.toString())
                .child(KEY_POTENTIAL_BUYERS).child(removed.account_id)
        myRef.removeValue()

        myRef = database.getReference(KEY_ACCOUNT).child(removed.account_id)
                .child(KEY_REQ_BOOKS).child(textbook.userid + textbook.bookid.toString())
        myRef.removeValue()

    }

    fun toggleRequestApproval(viewModel: MainViewModel, textbook: Searched_Textbooks, approval : Boolean, buyer: Potential_Buyer){
        var myRef = database.getReference(KEY_ACCOUNT).child(viewModel.user_account.user_id!!).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString()).child(KEY_POTENTIAL_BUYERS)
                .child(buyer.account_id).child(KEY_BUYER_APPROVAL)
        myRef.setValue(approval)
        myRef = database.getReference(KEY_TEXTBOOK_REF).child(viewModel.user_account.user_id.toString() + textbook.bookid.toString())
                .child(KEY_POTENTIAL_BUYERS).child(buyer.account_id).child(KEY_BUYER_APPROVAL)
        myRef.setValue(approval)

        myRef = database.getReference(KEY_ACCOUNT).child(buyer.account_id).child(KEY_REQ_BOOKS)
                .child(viewModel.user_account.user_id!! + textbook.bookid.toString()).child(KEY_BUYER_APPROVAL)
        myRef.setValue(approval)

    }

    fun reportUser(viewModel: MainViewModel, report : String, other_reason : String?){
        val myRef = database.getReference(KEY_ACCOUNT).child(viewModel.selected_requested.userid!!)
                .child(KEY_REPORTS).child(report)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var report_count =  1L
                if(snapshot.value != null){
                    report_count = (snapshot.value as Long) + 1L
                }

                myRef.setValue(report_count)

                if(other_reason != null){
                    myRef.parent!!.child(KEY_OTHERS_LOG).child(report_count.toString())
                            .setValue(other_reason)
                }

            }
        })
    }

    fun getAccountImg(account_id: String, imageView: ImageView){
        var prof_img: ByteArray? = null
        val myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(account_id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val prof_img_string = snapshot.child(KEY_PROF).value.toString()
                if(prof_img_string.isNotEmpty())
                    prof_img = Base64.decode(prof_img_string, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(prof_img, 0, prof_img!!.size)
                    imageView.setImageBitmap(bitmap)
            }
        })
    }
}