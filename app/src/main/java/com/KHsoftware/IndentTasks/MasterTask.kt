package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint

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
}