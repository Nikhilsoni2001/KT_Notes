package com.nikhil.kt_notes.util

import android.content.Context
import com.nikhil.kt_notes.util.Constants.Companion.MODE
import com.nikhil.kt_notes.util.Constants.Companion.PREF_NAME
import com.nikhil.kt_notes.util.Constants.Companion.PRIVATE_MODE

class Preferences(context: Context) {

    private val sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private val editor = sharedPref.edit()

    fun setDark(mode: Int) = editor.putInt(MODE, mode).apply()

    fun getDark(): Int = sharedPref.getInt(MODE, 0)
}