package com.KHsoftware.IndentTasks

import android.content.DialogInterface
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.jmedeisis.draglinearlayout.DragLinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.coroutineScope
import java.io.File
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

    }
}