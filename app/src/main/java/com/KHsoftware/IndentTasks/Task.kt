package com.KHsoftware.IndentTasks

import android.content.Context
import android.util.Log
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginLeft

class Task(var done: Boolean, var contents: String, val subTasks: MutableList<Task>, val depth: Int){
    // todo 通し番号で管理

    fun checkAllSubtaskDone(): Boolean{
        var done = true
        for(i in subTasks){
            if(!i.done) done = false
        }
        return done
    }

    fun setAllSubtaskStatus(status: Boolean){
        for(i in subTasks){
            i.done == status
        }
    }

    fun deleteSubtask(index: Int){
        subTasks.removeAt(index)
    }

    /**
     * @param done タスクの状態
     * @param contents タスクの内容
     * @param depth 追加するタスクの階層
     */
    fun addSubtask(done: Boolean, contents: String, depth: Int){
        // 追加されるタスクの階層が(このタスクの階層+1)ならば、このタスクの直下にタスクを追加
        // それより深い階層ならば、このタスク配列の直下のタスクのaddSubTaskメソッドを呼び出し
        if(depth == this.depth + 1){
            subTasks.add(Task(done, contents, mutableListOf<Task>(), this.depth + 1))
        }else{
            subTasks.last().addSubtask(done, contents, depth)
        }
    }

    fun export(): String{

        var tab = ""
        for(i in 0 until depth){
            tab += "\t"
        }

        var text = ""
        text += "$tab$depth[${this.done}] ${this.contents}\n"
        for(i in subTasks){
            text += tab
            text += "\t"
            text += i.export()
        }
        return text
    }

    fun initUI(taskContainer: LinearLayout, context: Context){
        val chk = CheckBox(context)
        val textView = TextView(context)
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.addView(chk)
        linearLayout.addView(textView)

        for(task in subTasks){
            task.initUI(taskContainer, context)
        }
    }
}