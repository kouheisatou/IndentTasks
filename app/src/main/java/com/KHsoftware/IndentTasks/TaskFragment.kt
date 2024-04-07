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
import com.KHsoftware.IndentTasks.databinding.TaskFragmentBinding
import kotlinx.coroutines.launch

class TaskFragment : Fragment() {

    companion object {
        fun newInstance() = TaskFragment()
    }

    private lateinit var viewModel: TaskViewModel
    lateinit var binding: TaskFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskFragmentBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        setListeners()
        return binding.root
    }

    fun getFilesName(): MutableList<String>{
        val files: Array<String> = requireContext().fileList()
        val arr = mutableListOf<String>()
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
        binding.loadSpinner.adapter = adapter
    }

    fun loadFile(filename: String){
        binding.taskContainer.removeAllViews()
        taskBuilder = TaskBuilder(filename, requireContext(), binding.taskContainer, viewModel)
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
            .setPositiveButton("作成") { _, _ ->
                loadFile("${title.text}.txt")
                updateSpinner("${title.text}.txt")
                selectedTaskList = title.text.toString()
            }
            .setNegativeButton("キャンセル") { _, _ ->
                val temp = selectedTaskList
                updateSpinner(temp)
                selectedTaskList = title.text.toString()
            }
            .show()
    }

    fun setListeners(){
        updateSpinner(null)
        binding.loadSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        binding.expandBtn.setOnClickListener(){
            taskBuilder.masterTask.foldSubtasks(false, true)
        }

        binding.foldBtn.setOnClickListener(){
            taskBuilder.masterTask.foldSubtasks(true, true)
        }


        // タスク追加ボタン
        binding.addButton.setOnClickListener(){
            taskBuilder.masterTask.addSubtask(binding.editText.text.toString(), taskBuilder.masterTask, taskBuilder)
            binding.editText.setText("")
        }

        // タスク削除ボタン
        binding.deleteBtn.setOnClickListener(){
            if(taskBuilder.masterTask.selectedTask != null){
                taskBuilder.masterTask.deleteSubtaskById(taskBuilder.masterTask.selectedTask!!.id)
            }
        }

        // エンターキーでタスク追加
        binding.editText.inputType = InputType.TYPE_CLASS_TEXT
        binding.editText.setOnKeyListener { _, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                taskBuilder.masterTask.addSubtask(binding.editText.text.toString(), taskBuilder.masterTask, taskBuilder)
                binding.editText.setText("")
            }
            false
        }
    }

}