package com.example.projectmanage.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanage.R
import com.example.projectmanage.adapters.MemberItemAdapter
import com.example.projectmanage.databinding.ActivityMembersBinding
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.Board
import com.example.projectmanage.models.User
import com.example.projectmanage.utils.Constants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private var binding:ActivityMembersBinding? = null

    private lateinit var mBoard:Board
    private lateinit var mAssignedMembers:ArrayList<User>

    private var isMemberAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoard = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
            FirestoreClass().getBoardMembersDetails(this,mBoard.assignedTo)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarMembersActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setTitle(R.string.members)

        binding?.toolbarMembersActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                openAddMemberDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(isMemberAdded){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

    fun setupRecyclerView(users:ArrayList<User>) {
        mAssignedMembers = users
        val adapter = MemberItemAdapter(this,mAssignedMembers)
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding?.rvMembersList?.setHasFixedSize(true)
        binding?.rvMembersList?.adapter  = adapter
    }

    private fun openAddMemberDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_member)

        val tvAdd = dialog.findViewById<TextView>(R.id.tv_add_member)
        val tvCancel =dialog.findViewById<TextView>(R.id.tv_cancel_member)
        val etEmailSearchMember =dialog.findViewById<EditText>(R.id.et_email_search_member)

        tvAdd.setOnClickListener {
            if(etEmailSearchMember.text.isNullOrEmpty()){
                Toast.makeText(this,"Please enter something",Toast.LENGTH_LONG).show()
            }
            else{
                FirestoreClass().getUserByEmail(this,etEmailSearchMember.text.toString())
                dialog.dismiss()
            }
        }

       tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun assignUserToBoard(user: User?){
        mBoard.assignedTo.add(user!!.id)
        FirestoreClass().addUserToBoard(this,mBoard,user)
    }

    fun addUserToBoardSuccess(user: User?) {
        mAssignedMembers.add(user!!)
        binding?.rvMembersList?.adapter?.notifyItemInserted(mAssignedMembers.size-1)
        isMemberAdded = true
        SendNotificationToUserAsyncTask(mBoard.name, user.fcmToken).execute()
    }

    fun addUserToBoardFailure(){
        mBoard.assignedTo.removeAt(mBoard.assignedTo.size-1)
    }

//    * “A nested class marked as inner can access the members of its outer class.
//    * Inner classes carry a reference to an object of an outer class:”
//    * source: https://kotlinlang.org/docs/reference/nested-classes.html
//    *
//    * This is the background class is used to execute background task.
//    *
//    * For Background we have used the AsyncTask
//    *
//    * Asynctask : Creates a new asynchronous task. This constructor must be invoked on the UI thread.
//    */
    @SuppressLint("StaticFieldLeak")
    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String) :
        AsyncTask<Any, Void, String>() {

        /**
         * This function is for the task which we wants to perform before background execution.
         * Here we have shown the progress dialog to user that UI is not freeze but executing something in background.
         */
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressBar()
        }

        /**
         * This function will be used to perform background execution.
         */
        override fun doInBackground(vararg params: Any): String {
            var result: String

            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL) // Base Url
                connection = url.openConnection() as HttpURLConnection

                /**
                 * A URL connection can be used for input and/or output.  Set the DoOutput
                 * flag to true if you intend to use the URL connection for output,
                 * false if not.  The default is false.
                 */
                connection.doOutput = true
                connection.doInput = true

                /**
                 * Sets whether HTTP redirects should be automatically followed by this instance.
                 * The default value comes from followRedirects, which defaults to true.
                 */
                connection.instanceFollowRedirects = false

                /**
                 * Set the method for the URL request, one of:
                 *  POST
                 */
                connection.requestMethod = "POST"

                /**
                 * Sets the general request property. If a property with the key already
                 * exists, overwrite its value with the new value.
                 */
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                // TODO (Step 3: Add the firebase Server Key.)
                // START
                // In order to find your Server Key or authorization key, follow the below steps:
                // 1. Goto Firebase Console.
                // 2. Select your project.
                // 3. Firebase Project Setting
                // 4. Cloud Messaging
                // 5. Finally, the SerkeyKey.
                // For Detail understanding visit the link: https://android.jlelse.eu/android-push-notification-using-firebase-and-advanced-rest-client-3858daff2f50
                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )
                // END

                /**
                 * Some protocols do caching of documents.  Occasionally, it is important
                 * to be able to "tunnel through" and ignore the caches (e.g., the
                 * "reload" button in a browser).  If the UseCaches flag on a connection
                 * is true, the connection is allowed to use whatever caches it can.
                 *  If false, caches are to be ignored.
                 *  The default value comes from DefaultUseCaches, which defaults to
                 * true.
                 */
                connection.useCaches = false

                /**
                 * Creates a new data output stream to write data to the specified
                 * underlying output stream. The counter written is set to zero.
                 */
                val wr = DataOutputStream(connection.outputStream)

                // TODO (Step 4: Create a notification data payload.)
                // START
                // Create JSONObject Request
                val jsonRequest = JSONObject()

                // Create a data object
                val dataObject = JSONObject()
                // Here you can pass the title as per requirement as here we have added some text and board name.
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")
                // Here you can pass the message as per requirement as here we have added some text and appended the name of the Board Admin.
                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "You have been assigned to the new board by ${mAssignedMembers[0].name}"
                )

                // Here add the data object and the user's token in the jsonRequest object.
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)
                // END

                /**
                 * Writes out the string to the underlying output stream as a
                 * sequence of bytes. Each character in the string is written out, in
                 * sequence, by discarding its high eight bits. If no exception is
                 * thrown, the counter written is incremented by the
                 * length of s.
                 */
                wr.writeBytes(jsonRequest.toString())
                wr.flush() // Flushes this data output stream.
                wr.close() // Closes this output stream and releases any system resources associated with the stream

                val httpResult: Int =
                    connection.responseCode // Gets the status code from an HTTP response message.

                if (httpResult == HttpURLConnection.HTTP_OK) {

                    /**
                     * Returns an input stream that reads from this open connection.
                     */
                    val inputStream = connection.inputStream

                    /**
                     * Creates a buffering character-input stream that uses a default-sized input buffer.
                     */
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {
                        /**
                         * Reads a line of text.  A line is considered to be terminated by any one
                         * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                         * followed immediately by a linefeed.
                         */
                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            /**
                             * Closes this input stream and releases any system resources associated
                             * with the stream.
                             */
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {
                    /**
                     * Gets the HTTP response message, if any, returned along with the
                     * response code from a server.
                     */
                    result = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }

            // You can notify with your result to onPostExecute.
            return result
        }

        /**
         * This function will be executed after the background execution is completed.
         */
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            hideProgressBar()

            // JSON result is printed in the log.
            Log.e("JSON Response Result", result)
        }
    }
}