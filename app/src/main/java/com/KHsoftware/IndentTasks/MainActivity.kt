package com.KHsoftware.IndentTasks

import android.content.DialogInterface
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.jmedeisis.draglinearlayout.DragLinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    // 表示中のタスク
    lateinit var taskBuilder: TaskBuilder

    var selectedTaskList = ""

    lateinit var adapter: ArrayAdapter<String>
    var files = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            .setNegativeButton("キャンセル", null)
            .show()
    }

    fun setListeners(){
        updateSpinner(null)
        loadSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == files.size -1){
                    createNewTaskListDialog()
                }else{
                    loadFile(files[position])
                    selectedTaskList = files[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        undoBtn.setOnClickListener(){
            Log.d("exportText", taskBuilder.masterTask.export())
        }

        redoBtn.setOnClickListener(){
            Log.d("exportSelectedTask", taskBuilder.masterTask.selectedTask?.export() ?: "何も選択されていません")
        }

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

    }
}