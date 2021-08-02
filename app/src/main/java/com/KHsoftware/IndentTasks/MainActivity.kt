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
import java.io.File

class MainActivity : AppCompatActivity() {

    // 表示中のタスク
    lateinit var taskBuilder: TaskBuilder

    // 選択されているタスクリストの名前
    var selectedTaskList = ""

    // スピナーのアダプタ
    lateinit var adapter: ArrayAdapter<String>

    // デバイス内に保存されたテキストファイル名リスト
    var files = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        setListeners()

    }

    fun getFilesName(): MutableList<String>{
        var files: Array<String> = this.fileList()
        var arr = mutableListOf<String>()
        arr.addAll(files)
        arr.add("新規作成")
        return arr
    }

    /**
     * @param top spinnerの一番上に持ってくる要素
     */
    fun updateSpinner(top: String?){
        files = getFilesName()
        if(top != null){
            files.remove(top)
            files.add(0, top)
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, files)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loadSpinner.adapter = adapter
    }

    fun loadFile(filename: String){
        taskContainer.removeAllViews()
        taskBuilder = TaskBuilder(filename, this, taskContainer)
        taskBuilder.build()
    }

    fun createNewTaskListDialog(){
        // todo から文字,改行,スペースNG
        val title = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("タイトル入力")
            .setMessage("タスクリストのタイトルを入力してください")
            .setView(title)
            .setPositiveButton("作成", DialogInterface.OnClickListener(){dialog, which ->
                loadFile("${title.text}.txt")
                updateSpinner("${title.text}.txt")
                selectedTaskList = title.text.toString()
            })
            .setNegativeButton("キャンセル", DialogInterface.OnClickListener(){dialog, which ->
                val temp = selectedTaskList
                updateSpinner(temp)
                selectedTaskList = title.text.toString()
            })
            .show()
    }

    fun setListeners(){
        updateSpinner(null)
        loadSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == files.size -1){
                    createNewTaskListDialog()
                }else{
                    if(files[position] != selectedTaskList){
                        updateSpinner(files[position])
                    }
                    loadFile(files[position])
                    selectedTaskList = files[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
//
//        undoBtn.setOnClickListener(){
//            Log.d("exportText", taskBuilder.masterTask.export())
//        }
//
//        redoBtn.setOnClickListener(){
//            Log.d("exportSelectedTask", taskBuilder.masterTask.selectedTask?.export() ?: "何も選択されていません")
//        }

        expandBtn.setOnClickListener(){
            taskBuilder.masterTask.foldAllTask(false)
        }

        foldBtn.setOnClickListener(){
            taskBuilder.masterTask.foldAllTask(true)
        }


        // タスク追加ボタン
        addButton.setOnClickListener(){
            taskBuilder.masterTask.addTaskToSelected(editText.text.toString())
            editText.setText("")
        }

        // タスク削除ボタン
        deleteBtn.setOnClickListener(){
            taskBuilder.masterTask.removeSelectedTask()
        }

        saveBtn.setOnClickListener(){
            taskBuilder.saveFile(applicationContext, selectedTaskList)
        }

        // エンターキーでタスク追加
        editText.maxLines = 1
        editText.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                taskBuilder.masterTask.addTaskToSelected(editText.text.toString())
                editText.setText("")
            }
            false
        }
    }
}