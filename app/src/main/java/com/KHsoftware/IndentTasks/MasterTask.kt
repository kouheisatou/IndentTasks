package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.DrawableContainer
import androidx.core.view.children
import com.jmedeisis.draglinearlayout.DragLinearLayout

@SuppressLint("StaticFieldLeak")
class MasterTask(
    done: Boolean,
    contents: String,
    fold: Boolean
):
    Task(
        done,
        contents,
        mutableListOf(),
        0,
        null,
        fold,
        null
    ){

    /** 作成したサブタスクの総数を保持 **/
    var taskNum: Int = 0
    /** 選択中のタスク **/
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

    fun foldAllTask(fold: Boolean){
        for(subtask in subTasks){
            subtask.foldSubtasks(fold, true)
        }
    }

    /**
     * 選択されたタスクのサブタスクとして新規タスクを追加する
     */
    fun addTaskToSelected(contents: String){
        val selectedTask = selectedTask ?: this
        selectedTask.addSubtask(contents, this)
        setFoldButton()
    }

    fun removeSelectedTask(){
        val selectedTask = this.selectedTask ?: return
        if(selectedTask.id == 0){
            subTasks.clear()
            subtaskLinearLayout.removeAllViews()
            initUI(subtaskLinearLayout)
            unselectAllSubtasks()
        }else{
            deleteSubtaskById(selectedTask.id)
        }
        setFoldButton()
    }
}