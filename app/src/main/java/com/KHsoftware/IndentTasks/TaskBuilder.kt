package com.KHsoftware.IndentTasks

import android.content.Context
import android.util.Log
import com.jmedeisis.draglinearlayout.DragLinearLayout
import java.io.*

class TaskBuilder(private val fileName: String, private val context: Context, private val taskContainer: DragLinearLayout) {

    fun build(){
        val br = readFile(context, fileName)

        var line = br?.readLine()

        while(line != null){

            Log.d("loadFile", line)

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

            line = br?.readLine()
        }
        MasterTask.initUI(taskContainer)
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

fun saveFile(context: Context, fileName: String){

    val file = File(context.filesDir, fileName)

    val text = MasterTask.export()

    try {
        FileWriter(file).use { writer -> writer.write(text) }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun readFile(context: Context, fileName: String): BufferedReader? {
    val file = File(context.filesDir, fileName)

    var br: BufferedReader? = null
    try {
        br = BufferedReader(FileReader(file))
    } catch (e: IOException) {
        Log.d("fileLoadError", e.stackTrace.toString())
    }

    return br
}