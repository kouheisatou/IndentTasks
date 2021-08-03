package com.KHsoftware.IndentTasks

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.task_fragment.*
import kotlinx.coroutines.launch

class TaskFragment : Fragment() {

    companion object {
        fun newInstance() = TaskFragment()
    }

    private lateinit var viewModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        setListeners()
    }

    fun getFilesName(): MutableList<String>{
        var files: Array<String> = requireContext().fileList()
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
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, files)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loadSpinner.adapter = adapter
    }

    fun loadFile(filename: String){
        taskContainer.removeAllViews()
        taskBuilder = TaskBuilder(filename, requireContext(), taskContainer, viewModel, this)
        viewModel.viewModelScope.launch {
            taskBuilder.build()
        }
    }

    fun createNewTaskListDialog(){
        // todo から文字,改行,スペースNG
        val title = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("タイトル入力")
            .setMessage("タスクリストのタイトルを入力してください")
            .setView(title)
            .setPositiveButton("作成", DialogInterface.OnClickListener(){ dialog, which ->
                loadFile("${title.text}.txt")
                updateSpinner("${title.text}.txt")
                selectedTaskList = title.text.toString()
            })
            .setNegativeButton("キャンセル", DialogInterface.OnClickListener(){ dialog, which ->
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
            taskBuilder.masterTask.foldSubtasks(false, true)
        }

        foldBtn.setOnClickListener(){
            taskBuilder.masterTask.foldSubtasks(true, true)
        }


        // タスク追加ボタン
        addButton.setOnClickListener(){
            taskBuilder.masterTask.addSubtask(editText.text.toString(), taskBuilder.masterTask, taskBuilder)
            editText.setText("")
        }

        // タスク削除ボタン
        deleteBtn.setOnClickListener(){
            if(taskBuilder.masterTask.selectedTask != null){
                taskBuilder.masterTask.deleteSubtaskById(taskBuilder.masterTask.selectedTask!!.id)
            }
        }

        // エンターキーでタスク追加
        editText.inputType = InputType.TYPE_CLASS_TEXT
        editText.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                taskBuilder.masterTask.addSubtask(editText.text.toString(), taskBuilder.masterTask, taskBuilder)
                editText.setText("")
            }
            false
        }
    }

}