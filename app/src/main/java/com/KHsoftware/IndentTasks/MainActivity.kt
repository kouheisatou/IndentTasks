package com.KHsoftware.IndentTasks

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

// todo 折り畳み,スワイプで

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sample = "[ ]Title\n" +
                "\t[ ]Task1\n" +
                "\t\t[ ]SubTask1\n" +
                "\t\t[ ]SubTask2\n" +
                "\t\t\t[ ]SubSubTask1\n" +
                "\t\t[ ]SubTask3\n" +
                "\t\t\t[ ]SubSubTask2\n" +
                "\t[x]SubTask2\n" +
                "\t\t[x]SubTask1\n" +
                "\t[x]Task3\n" +
                "\t[ ]Task4\n" +
                "\t\t[x]SubTask1\n" +
                "\t\t[ ]SubTask2\n" +
                "\t\t\t[ ]SubSubTask1\n" +
                "\t\t\t\t[ ]SubSubSubTask1\n" +
                "\t\t\t\t\t[ ]Sub4Task1\n" +
                "\t\t\t\t\t[ ]Sub4Task2\n" +
                "\t\t\t\t\t\t[ ]Sub5Task1\n" +
                "\t\t\t\t\t\t\t[ ]Sub6Task1\n" +
                "\t\t\t\t\t\t\t\t[x]Sub7Task1\n" +
                "\t\t\t\t\t\t\t\t[x]Sub7Task2\n" +
                "\t\t\t\t\t[ ]Sub4Task3\n" +
                "\t\t\t\t\t[ ]Sub4Task4\n" +
                "\t\t\t\t\t\t[x]Sub5Task2"

        // テキストからタスクを生成
        debugBuilder(sample)

        // 全タスク初期化
        MasterTask.initUI(this, taskContainer)

        // タスク追加ボタン
        addButton.setOnClickListener(){
            MasterTask.addTaskToSelected(this, editText.text.toString())
            editText.setText("")
        }

        // タスク削除ボタン
        deleteBtn.setOnClickListener(){
            MasterTask.removeSelectedTask(this)
        }

        Log.d("exportText", MasterTask.export())





//        var printText = ""
//        for(i in taskList){
//            printText += i.export()
//        }

//        text.text = printText
//        text.typeface = Typeface.MONOSPACE


//        for(i in taskList){
//            i.initUI(this, taskContainer)
//        }



    }
}