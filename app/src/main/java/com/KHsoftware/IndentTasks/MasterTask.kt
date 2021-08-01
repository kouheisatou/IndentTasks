package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.DrawableContainer
import androidx.core.view.children
import com.jmedeisis.draglinearlayout.DragLinearLayout

@SuppressLint("StaticFieldLeak")
object MasterTask: Task(false, "", mutableListOf(), 0, null, false){

    /** 作成したインスタンスの総数を保持 **/
    var taskNum: Int = 0
    /** 大元の親レイアウト **/
    lateinit var taskContainer: DragLinearLayout
    /** 選択中のタスク **/
    var selectedTask: Task? = null

    lateinit var context: Context
    var initialized = false

    fun init(done: Boolean, contents: String, fold: Boolean, context: Context, taskContainer: DragLinearLayout){
        if(!initialized){
            this.done = done
            this.contents = contents
            this.fold = fold
            this.context = context
            this.taskContainer = taskContainer
            this.initialized = true
        }
    }

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
        val selectedTask = selectedTask ?: MasterTask
        selectedTask.addSubtask(contents)
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