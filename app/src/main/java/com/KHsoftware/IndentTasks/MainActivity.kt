package com.KHsoftware.IndentTasks

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.jmedeisis.draglinearlayout.DragLinearLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sample = "+[ ]Title\n" +
                "\t-[ ]Task1\n" +
                "\t\t+[ ]SubTask1\n" +
                "\t\t+[ ]SubTask2\n" +
                "\t\t\t+[ ]SubSubTask1\n" +
                "\t\t+[ ]SubTask3\n" +
                "\t\t\t+[ ]SubSubTask2\n" +
                "\t+[x]SubTask2\n" +
                "\t\t+[x]SubTask1\n" +
                "\t+[x]Task3\n" +
                "\t+[ ]Task4\n" +
                "\t\t+[x]SubTask1\n" +
                "\t\t+[ ]SubTask2\n" +
                "\t\t\t+[ ]SubSubTask1\n" +
                "\t\t\t\t+[ ]SubSubSubTask1\n" +
                "\t\t\t\t\t+[ ]Sub4Task1\n" +
                "\t\t\t\t\t+[ ]Sub4Task2\n" +
                "\t\t\t\t\t\t-[ ]Sub5Task1\n" +
                "\t\t\t\t\t\t\t-[ ]Sub6Task1\n" +
                "\t\t\t\t\t\t\t\t+[x]Sub7Task1\n" +
                "\t\t\t\t\t\t\t\t+[x]Sub7Task2\n" +
                "\t\t\t\t\t+[ ]Sub4Task3\n" +
                "\t\t\t\t\t-[ ]Sub4Task4\n" +
                "\t\t\t\t\t\t+[x]Sub5Task2"


        // テキストからタスクを生成
        debugBuilder(sample)

        // 全タスク初期化
        MasterTask.initUI(this, taskContainer, taskContainer)

        // タスク追加ボタン
        addButton.setOnClickListener(){
            MasterTask.addTaskToSelected(this, editText.text.toString())
            editText.setText("")
        }

        // タスク削除ボタン
        deleteBtn.setOnClickListener(){
            MasterTask.removeSelectedTask(this)
        }

        undoBtn.setOnClickListener(){
            Log.d("exportText", MasterTask.export())
        }

        redoBtn.setOnClickListener(){
            Log.d("exportSelectedTask", MasterTask.selectedTask?.export() ?: "何も選択されていません")
        }

        expandBtn.setOnClickListener(){
            MasterTask.foldAllTask(false)
        }

        foldBtn.setOnClickListener(){
            MasterTask.foldAllTask(true)
        }





//        // DragLinearLayout同士の入れ子状態で行入れ替えできるかテスト
//        val dragLinearLayout1 = DragLinearLayout(this)
//        val dragLinearLayout2 = DragLinearLayout(this)
//        var dragLinearLayout31 = DragLinearLayout(this)
//        var dragLinearLayout32 = DragLinearLayout(this)
//        var dragLinearLayout33 = DragLinearLayout(this)
//        val textView1 = TextView(this)
//        textView1.text = "1"
//        val textView2 = TextView(this)
//        textView2.text = "2"
//        val textView3 = TextView(this)
//        textView3.text = "3"
//        val textView4 = TextView(this)
//        textView1.text = "4"
//        val textView5 = TextView(this)
//        textView2.text = "5"
//        val textView6 = TextView(this)
//        textView3.text = "6"
//        val textView7 = TextView(this)
//        textView1.text = "7"
//        val textView8 = TextView(this)
//        textView2.text = "8"
//        val textView9 = TextView(this)
//        textView3.text = "9"
//
//        dragLinearLayout31.addView(textView1)
//        dragLinearLayout31.addView(textView2)
//        dragLinearLayout31.addView(textView3)
//        dragLinearLayout32.addView(textView4)
//        dragLinearLayout32.addView(textView5)
//        dragLinearLayout32.addView(textView6)
//        dragLinearLayout33.addView(textView7)
//        dragLinearLayout33.addView(textView8)
//        dragLinearLayout33.addView(textView9)
//        dragLinearLayout2.addView(dragLinearLayout31)
//        dragLinearLayout2.addView(dragLinearLayout32)
//        dragLinearLayout2.addView(dragLinearLayout33)
//        dragLinearLayout1.addView(dragLinearLayout2)
//        taskContainer.addView(dragLinearLayout1)
//
//        for(i in 0..2){
//            val child = dragLinearLayout2.getChildAt(i)
//            dragLinearLayout2.setViewDraggable(child, child)
//        }
//        for(i in 1..2){
//            val child1 = dragLinearLayout31.getChildAt(i)
//            dragLinearLayout31.setViewDraggable(child1, child1)
//            val child2 = dragLinearLayout32.getChildAt(i)
//            dragLinearLayout32.setViewDraggable(child2, child2)
//            val child3 = dragLinearLayout33.getChildAt(i)
//            dragLinearLayout33.setViewDraggable(child3, child3)
//
//        }


//        // ドラッグ無効化 むり
//        deleteBtn.setOnClickListener(){
//            dragLinearLayout2.removeAllViews()
//            dragLinearLayout3.removeAllViews()
//            dragLinearLayout3 = DragLinearLayout(this)
//            dragLinearLayout3.addView(textView1)
//            dragLinearLayout3.addView(textView2)
//            dragLinearLayout3.addView(textView3)
//            dragLinearLayout2.addView(dragLinearLayout3)
//        }





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