package com.example.projectmanage.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap


object Constants {
    const val USERS = "Users"
    const val BOARDS = "Boards"

    const val BOARD_DETAILS = "Board_Details"
    const val IMAGE = "image"
    const val MOBILE = "mobile"
    const val NAME="name"
    const val EMAIL = "email"
    const val ASSIGNED_TO = "assignedTo"
    const val DOCUMENT_ID = "documentId"
    const val TASK_LIST = "taskList"
    const val ID = "id"
    const val BOARD_MEMBERS_DETAILS = "board_members_details"

    const val ADD_TASK_CODE = 1
    const val EDIT_TASK_CODE = 2
    const val DELETE_TASK_CODE= 3

    const val TASK_LIST_POSITION = "taskListPosition"
    const val CARD_LIST_POSITION= "cardListPosition"

    const val SELECT = "select"
    const val UNSELECT = "unSelect"

    const val NOTIFICATION_CHANNEL_ID = "project_manage_notification_channel_id"
    const val PROJECT_MANAGE_PREFERENCES = "project_manage_preferences"
    const val FCM_TOKEN_UPDATED = "fcm_token_updated"
    const val FCM_TOKEN = "fcmToken"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAAOnkcs_M:APA91bH8fyL6btQVgb9HvDRkuU5PqgY3nL--JjhPFQri10FIWhdqa1P_lcImocEidbki-9kJGrvApw_wb58SipKFmlRl4_c7WR60lAGKrioh1Z88psuXhU8tHDQLwNBQ5FbG7tyD6AjV"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun getFileTypeFromUri(activity:Activity,uri: Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }

    fun getColorsList():ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

}