package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginLeft

// できるだけ画面描画は描画するだけ、内部処理の結果を出すだけにする -> redraw()
// todo Taskを継承してmasterTaskを作成、これは一つしか存在しないsingleton
// todo dragLinearLayoutで選択中のタスクを同じ階層内でドラッグできるように

open class Task(var done: Boolean, var contents: String, val subTasks: MutableList<Task>, val depth: Int){

    /** このタスクの選択状態 **/
    var selected = false
    /** 1つ上の階層のタスクの選択状態 **/
    var parentSelected = false
    /** このタスクを親とする階層全体の線形レイアウトView **/
    lateinit var subtaskLinearLayout: LinearLayout
    /** 各タスクのチェックボックスView **/
    lateinit var chk: CheckBox

    fun checkAllSubtask(): Boolean{
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

    fun deleteSubtaskAt(index: Int){
        subTasks.removeAt(index)
    }

    fun deleteSubtaskSelected(context: Context, parentView: LinearLayout){
        for(i in subTasks){
            if(i.selected || i.parentSelected){
                // タスクを削除
                subTasks.remove(i)
                // Viewを削除
                this.subtaskLinearLayout.removeView(i.subtaskLinearLayout)
            }
        }
    }

    fun makeAllSubtaskSelected(){
        for(i in subTasks){
            i.parentSelected = true
            i.makeAllSubtaskSelected()
            i.subtaskLinearLayout.setBackgroundColor(Color.parseColor("#DDDDDD"))
        }
    }
    fun makeAllSubtaskUnselected(){
        for(i in subTasks){
            i.parentSelected = false
            i.makeAllSubtaskUnselected()
            i.subtaskLinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
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

    /**
     * このタスクの情報をテキストに書き出す
     */
    open fun export(): String{
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

    /**
     * このタスクの情報からAndroidの画面上のレイアウトを生成
     */
    open fun initUI(context: Context, parentView: LinearLayout){
        subtaskLinearLayout = LinearLayout(context)
        subtaskLinearLayout.orientation = LinearLayout.VERTICAL
        var layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(40, 10, 0, 0)
        subtaskLinearLayout.layoutParams = layoutParams

        chk = CheckBox(context)
        chk.layoutParams = layoutParams
        chk.setPadding(0, 0, 15, 0)
        chk.isChecked = done
        chk.text = contents
        chk.setOnLongClickListener(){
            if(!parentSelected){
                if(selected){
                    selected = false
                    subtaskLinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    makeAllSubtaskUnselected()
                }else{
                    selected = true
                    subtaskLinearLayout.setBackgroundColor(Color.parseColor("#DDDDDD"))
                    makeAllSubtaskSelected()
                }
            }
            true
        }

        subtaskLinearLayout.addView(chk)
        parentView.addView(subtaskLinearLayout)

        for(task in subTasks){
            task.initUI(context, subtaskLinearLayout)
        }
    }
}