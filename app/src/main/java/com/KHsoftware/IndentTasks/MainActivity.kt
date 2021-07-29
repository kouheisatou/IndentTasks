package com.KHsoftware.IndentTasks

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sample = "[ ]Task1\n" +
                "\t[ ]SubTask1\n" +
                "\t[ ]SubTask2\n" +
                "\t\t[ ]SubSubTask1\n" +
                "\t[ ]SubTask3\n" +
                "\t\t[ ]SubSubTask2\n" +
                "[x]SubTask2\n" +
                "\t[x]SubTask1\n" +
                "[x]Task3\n" +
                "[ ]Task4\n" +
                "\t[x]SubTask1\n" +
                "\t[ ]SubTask2\n" +
                "\t\t[ ]SubSubTask1\n" +
                "\t\t\t[ ]SubSubSubTask1\n" +
                "\t\t\t\t[ ]Sub4Task1\n" +
                "\t\t\t\t[ ]Sub4Task2\n" +
                "\t\t\t\t\t[ ]Sub5Task1\n" +
                "\t\t\t\t\t\t[ ]Sub6Task1\n" +
                "\t\t\t\t\t\t\t[x]Sub7Task1\n" +
                "\t\t\t\t\t\t\t[x]Sub7Task2\n" +
                "\t\t\t\t[ ]Sub4Task3\n" +
                "\t\t\t\t[ ]Sub4Task4\n" +
                "\t\t\t\t\t[x]Sub5Task2"

        val taskList = debugBuilder(sample)
        var printText = ""
        for(i in taskList){
            printText += i.export()
        }
        text.text = printText
        text.typeface = Typeface.MONOSPACE

//        for(i in taskList){
//            i.initUI(taskContainer, this)
//        }

    }
}