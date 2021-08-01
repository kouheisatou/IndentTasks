package com.KHsoftware.IndentTasks

import android.content.Context
import android.util.Log
import com.jmedeisis.draglinearlayout.DragLinearLayout
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class TaskBuilder(private val file: File) {

    fun build(){
        if(!file.exists()) return

        val fileReader = FileReader(file)
        val bufferdReader = BufferedReader(fileReader)

        while(true){
//            val line = bufferdReader.readLine() ?: break
//            /** 階層の深さ **/
//            val indentCount = line.lastIndexOf('\t') + 1
//            /** タスクの状態(完了or未完了) **/
//            val isDone = line.startsWith("[x]", indentCount)
//            /** タスクの内容 **/
//            val content = line.replace("\t", "").substring(3, line.length)
//
//            if(indentCount == 0){
//                MasterTask.contents = content
//                MasterTask.done = isDone
//            }else{
//                MasterTask.addSubtask(isDone, content, indentCount)
//            }
        }
    }
}

fun debugBuilder(text: String, context: Context, taskContainer: DragLinearLayout){
    val lines = text.split("\n")

    for(line in lines){

        /** 階層の深さ **/
        val indentCount = line.lastIndexOf('\t') + 1
        Log.d("builder", indentCount.toString())

        var fold = false
        if(line.replace("\t", "").startsWith("+")){
            fold = false
        }
        if(line.replace("\t", "").startsWith("-")){
            fold = true
        }

        /** タスクの状態(完了or未完了) **/
        val isDone = line.substring(1).startsWith("[x]", indentCount)
        Log.d("builder", isDone.toString())

        /** タスクの内容 **/
        val content = line.replace("\t", "").substring(4, line.length-indentCount)
        Log.d("builder", content)

        if(indentCount == 0){
            MasterTask.init(isDone, content, fold, context, taskContainer)
        }else{
            MasterTask.addSubtask(isDone, content, indentCount, fold)
        }
    }
    MasterTask.initUI(taskContainer)
}