package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.view.children

@SuppressLint("StaticFieldLeak")
object MasterTask: Task(false, "", mutableListOf(), 0){
    var taskNum: Int = 0
    var selectedTask: Task? = null

    override fun generateId() = 0

    override fun findTaskById(id: Int): Task?{
        var result: Task? = null
        for(task in subTasks){
            result = task.findTaskById(id)
            if(result != null) return result
        }
        return result
    }

    fun addTaskToSelected(context: Context, contents: String){
        val selectedTask = selectedTask ?: MasterTask
        selectedTask.addSubtask(context, contents)
    }

    fun removeSelectedTask(context: Context){
        val selectedTask = this.selectedTask ?: return
        if(selectedTask.id == 0){
            subTasks.clear()
            subtaskLinearLayout.removeAllViews()
            initUI(context, subtaskLinearLayout)
            unselectAll()
        }else{
            deleteSubtaskById(selectedTask.id, context)
        }
    }

}