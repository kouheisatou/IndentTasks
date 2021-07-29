package com.KHsoftware.IndentTasks

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class TaskBuilder(private val file: File) {

    fun build(): MutableList<Task>{
        if(!file.exists()) return mutableListOf()

        val fileReader = FileReader(file)
        val bufferdReader = BufferedReader(fileReader)

        val taskList = mutableListOf<Task>()

        var preAddTask: Task
        while(true){
            val line = bufferdReader.readLine() ?: break
            /** 階層の深さ **/
            val indentCount = line.lastIndexOf('\t') + 1
            /** タスクの状態(完了or未完了) **/
            val isDone = line.startsWith("[x]", indentCount)
            /** タスクの内容 **/
            val content = line.replace("\t", "").substring(3, line.length)

            if(indentCount == 0){
                val task = Task(isDone, content, mutableListOf(), indentCount)
                taskList.add(task)
            }else{
                taskList.last().addSubtask(isDone, content, indentCount)
            }
        }

        return taskList
    }
}

fun debugBuilder(text: String): MutableList<Task>{
    val lines = text.split("\n")
    val taskList = mutableListOf<Task>()

    for(line in lines){

        /** 階層の深さ **/
        val indentCount = line.lastIndexOf('\t') + 1
        Log.d("builder", indentCount.toString())

        /** タスクの状態(完了or未完了) **/
        val isDone = line.startsWith("[x]", indentCount)
        Log.d("builder", isDone.toString())

        /** タスクの内容 **/
        val content = line.replace("\t", "").substring(3, line.length-indentCount)
        Log.d("builder", content.toString())

        if(indentCount == 0){
            val task = Task(isDone, content, mutableListOf(), indentCount)
            taskList.add(task)
        }else{
            taskList.last().addSubtask(isDone, content, indentCount)
        }
    }
    return taskList
}