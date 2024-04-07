package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@SuppressLint("StaticFieldLeak")
class MasterTask(
    done: Boolean,
    contents: String,
    fold: Boolean,
    taskBuilder: TaskBuilder
):
    Task(
        done,
        contents,
        mutableListOf(),
        0,
        null,
        fold,
        null,
        taskBuilder
    ){

    /** 作成したサブタスクの総数を保持 **/
    var taskNum: Int = 0
    /** 選択中のタスク **/
    var selectedTask: Task? = null

    override fun generateId() = 0

    override fun selectAt(id: Int, select: Boolean) {
        super.selectAt(id, select)
    }

    override fun makeAllSubtaskSelected(select: Boolean){
        if(select){
            super.makeAllSubtaskSelected(select)
        }else{
            // 選択色を解除
            val color = Color.parseColor("#FFFFFF")
            masterTask?.selectedTask?.subtaskLinearLayout?.setBackgroundColor(color)

            // 全てのサブタスクで実行
            for(task in subTasks){
                task.subtaskLinearLayout.setBackgroundColor(color)
                task.selected = false
                task.makeAllSubtaskSelected(select)
            }

            this.selected = false
            masterTask?.selectedTask = null

            Log.d("selected", masterTask?.selectedTask?.id.toString())
        }
    }

    override fun deleteSubtaskById(id: Int) {

        if(id == 0){
            deleteSelectedFile(contents)
        }else{
            super.deleteSubtaskById(id)
        }
        setFoldButton()
        save()

    }

    override fun foldSubtasks(fold: Boolean, applyToSubtasks: Boolean) {
        for(subtask in subTasks){
            subtask.foldSubtasks(fold, true)
        }
        save()
    }

    override fun findTaskById(id: Int): Task?{
        var result: Task? = null
        for(task in subTasks){
            result = task.findTaskById(id)
            if(result != null) return result
        }
        return result
    }

    override fun export(): String {
        return super.export()
    }

    override fun setSubtaskDraggable(draggable: Boolean, selectedTask: Task) {
        super.setSubtaskDraggable(draggable, selectedTask)
    }

    /**
     * 選択されたタスクのサブタスクとして新規タスクを追加する
     */
    override fun addSubtask(contents: String, masterTask: MasterTask?, taskBuilder: TaskBuilder) {
        val selectedTask = selectedTask ?: this
        if(selectedTask != this){
            if(contents == ""){
                selectedTask.addSubtask("未入力タスク", masterTask, taskBuilder)
            }else{
                selectedTask.addSubtask(contents, masterTask, taskBuilder)
            }
        }else{
            if(contents == ""){
                super.addSubtask("未入力タスク", masterTask, taskBuilder)
            }else{
                super.addSubtask(contents, masterTask, taskBuilder)
            }
        }
        setFoldButton()
        save()
    }

    fun deleteSelectedFile(filename: String){
        AlertDialog.Builder(TaskBuilder.context)
            .setTitle("タスクリスト削除")
            .setMessage("本当に削除しますか？")
            .setPositiveButton("削除", DialogInterface.OnClickListener(){ dialog, which ->
                try {
                    val file = File(TaskBuilder.context.filesDir, filename)
                    file.delete()
                    subtaskLinearLayout.removeAllViews()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                (TaskBuilder.context as TaskFragment).updateSpinner(null)
            })
            .setNegativeButton("キャンセル", null)
            .show()
    }

    override fun save(){
        TaskBuilder.viewModel.viewModelScope.launch {
            taskBuilder.saveFile(TaskBuilder.context, contents)
        }
    }
}