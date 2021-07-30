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
    /** このタスクのチェックボックスView **/
    lateinit var chk: CheckBox
    /** タスクのID **/
    var id = generateId()

    open fun generateId() = ++MasterTask.taskNum

    fun selectAt(id: Int){
        if(id == this.id){
            selectThis()
        }
        for(i in subTasks){
            i.selectAt(id)
        }
    }

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

    fun deleteSubtaskAt(index: Int){
        subTasks.removeAt(index)
    }

    fun deleteSubtaskSelected(){
        for(i in subTasks.withIndex()){
            if(i.value.selected){
                subTasks.removeAt(i.index)
            }
        }
    }

    fun selectThis(){
        selected = true
        subtaskLinearLayout.setBackgroundColor(Color.parseColor("#DDDDDD"))
        MasterTask.selectedTask = this
        for(i in subTasks){
            i.parentSelected = true
            i.selectThis()
            i.subtaskLinearLayout.setBackgroundColor(Color.parseColor("#DDDDDD"))
        }
    }
    fun unselectThis(){
        selected = false
        subtaskLinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
        MasterTask.selectedTask = null
        for(i in subTasks){
            i.parentSelected = false
            i.unselectThis()
            i.subtaskLinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
    }

    /**
     * 自動的に適切な階層の一番最後へタスクを追加する
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

    fun addSubtask(done: Boolean, contents: String){

    }

    open fun findTaskById(id: Int): Task?{
        var result: Task? = null
        for(task in subTasks){
            if(id == task.id){
                result = task
            }else{
                result = task.findTaskById(id)
                if(result != null) return result
            }
        }
        return result
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
        chk.text = "($id)$contents"
        chk.setOnLongClickListener(){
            if(!parentSelected){
                if(selected){
                    unselectThis()
                }else{
                    selectThis()
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