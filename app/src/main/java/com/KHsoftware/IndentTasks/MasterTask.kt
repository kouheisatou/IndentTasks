package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.DrawableContainer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import com.jmedeisis.draglinearlayout.DragLinearLayout
import java.io.File
import java.io.IOException

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
        if(contents == ""){
            selectedTask.addSubtask("未入力タスク", this)
        }else{
            selectedTask.addSubtask(contents, this)
        }
        setFoldButton()
    }

    fun removeSelectedTask(){
        val selectedTask = this.selectedTask ?: return
        if(selectedTask.id == 0){
//            subTasks.clear()
//            subtaskLinearLayout.removeAllViews()
//            initUI(subtaskLinearLayout)
//            unselectAllSubtasks()
            deleteSelectedFile(contents)
        }else{
            deleteSubtaskById(selectedTask.id)
        }
        setFoldButton()
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
                (TaskBuilder.context as MainActivity).updateSpinner(null)
            })
            .setNegativeButton("キャンセル", null)
            .show()
    }

}