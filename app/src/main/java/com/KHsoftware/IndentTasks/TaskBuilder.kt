package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.jmedeisis.draglinearlayout.DragLinearLayout
import java.io.*

class TaskBuilder(private val fileName: String, private val context: Context, private val taskContainer: DragLinearLayout, private val viewModel: TaskViewModel) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        @SuppressLint("StaticFieldLeak")
        lateinit var taskContainer: DragLinearLayout
        @SuppressLint("StaticFieldLeak")
        lateinit var viewModel: TaskViewModel
    }

    /** 全ての親タスク **/
    lateinit var masterTask: MasterTask

    fun build(){

        TaskBuilder.context = context
        TaskBuilder.taskContainer = taskContainer
        TaskBuilder.viewModel = viewModel

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
                masterTask = MasterTask(isDone, content, fold, this)
            }else{
                masterTask.addSubtask(isDone, content, indentCount, fold, masterTask, this)
            }

            line = br.readLine()
        }
        masterTask.initUI(taskContainer)
    }

    fun saveFile(context: Context, fileName: String){

        val file = File(context.filesDir, fileName)

        val text = masterTask.export()

        Log.d("export", text)

        try {
            file.writeText(text)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFile(context: Context, fileName: String): BufferedReader {
        val file = File(context.filesDir, fileName)

        try {
            if(file.createNewFile()){
                file.writeText("+[ ]$fileName")
            }else{
                Log.d("alreadyExist", "そのファイルは既に存在しています:$fileName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return BufferedReader(FileReader(file))
    }
}

//fun debugBuilder(text: String, context: Context, taskContainer: DragLinearLayout){
//    val lines = text.split("\n")
//
//    for(line in lines){
//
//        /** 階層の深さ **/
//        val indentCount = line.lastIndexOf('\t') + 1
//        Log.d("builder", indentCount.toString())
//
//        var fold = false
//        if(line.replace("\t", "").startsWith("+")){
//            fold = false
//        }
//        if(line.replace("\t", "").startsWith("-")){
//            fold = true
//        }
//
//        /** タスクの状態(完了or未完了) **/
//        val isDone = line.substring(1).startsWith("[x]", indentCount)
//        Log.d("builder", isDone.toString())
//
//        /** タスクの内容 **/
//        val content = line.replace("\t", "").substring(4, line.length-indentCount)
//        Log.d("builder", content)
//
//        if(indentCount == 0){
//            MasterTask.init(isDone, content, fold, context, taskContainer)
//        }else{
//            MasterTask.addSubtask(isDone, content, indentCount, fold)
//        }
//    }
//    MasterTask.initUI(taskContainer)
//}
