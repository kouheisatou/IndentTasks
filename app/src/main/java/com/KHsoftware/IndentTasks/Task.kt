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

open class Task(var done: Boolean, var contents: String, val subTasks: MutableList<Task> = mutableListOf(), val depth: Int){

    /** このタスクの選択状態 **/
    var selected = false
//    /** 1つ上の階層のタスクの選択状態 **/
//    var parentSelected = false
    /** このタスクを親とする階層全体の線形レイアウトView **/
    lateinit var subtaskLinearLayout: LinearLayout
    /** このタスクのチェックボックスView **/
    lateinit var chk: CheckBox
    /** タスクのID **/
    var id = generateId()

    open fun generateId() = ++MasterTask.taskNum

    /**
     * 指定したIDのタスクとそのサブタスクを選択する
     * @param id 選択するタスクのID
     * @param select true:選択, false:選択解除
     */
    fun selectAt(id: Int, select: Boolean){
        if(id == this.id){

            // 選択状態をリセット
            MasterTask.unselectAll()

            // マスタータスクに選択されたタスクを保持、選択が解除された場合はnullを代入
            MasterTask.selectedTask = if(select){this}else{null}
            this.selected = select
            Log.d("selected", MasterTask.selectedTask?.id.toString())

            // 選択されたタスクに色を付ける
            val color = if(select){ "#DDDDDD" }else{ "#FFFFFF" }
            this.subtaskLinearLayout.setBackgroundColor(Color.parseColor(color))

            // 選択されたタスクのサブタスクを全て選択する
            for(task in subTasks){
                task.makeAllSubtaskSelected(select)
                // サブタスクが選択された時に色を付ける
                task.subtaskLinearLayout.setBackgroundColor(Color.parseColor(color))
            }

        }else{
            // このタスクのIDが指定したIDと一致しない場合、サブタスクのIDも検索する
            for(i in subTasks){
                i.selectAt(id, select)
            }
        }
    }

    fun unselectAll(){
        val color = Color.parseColor("#FFFFFF")
        MasterTask.selectedTask?.subtaskLinearLayout?.setBackgroundColor(color)

        for(task in subTasks){
            task.subtaskLinearLayout.setBackgroundColor(color)
            task.selected = false
            task.unselectAll()
        }

        this.selected = false
        MasterTask.selectedTask = null

        Log.d("selected", MasterTask.selectedTask?.id.toString())

    }

    /**
     * このタスクが持つサブタスク全てを選択または選択解除する
     * @param select true:選択, false:選択解除
     */
    fun makeAllSubtaskSelected(select: Boolean){

        // このタスクが持つサブタスクを全て選択済みにする
        for(task in subTasks){
            task.subtaskLinearLayout.setBackgroundColor(Color.parseColor(if(select){ "#DDDDDD" }else{ "#FFFFFF" }))
            // サブタスクの持つサブサブタスクを全て選択状態/選択解除状態にする
            task.makeAllSubtaskSelected(select)
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

    fun deleteSubtaskById(id: Int, context: Context){
        for(task in subTasks){
            if(id == task.id){
                subTasks.remove(task)
                this.subtaskLinearLayout.removeView(task.subtaskLinearLayout)
                return
            }else{
                // idが一致しない場合、さらに下の階層もidで検索
                task.deleteSubtaskById(id, context)
            }
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

    // このタスクの一番最後のサブタスクとして新規タスクを追加
    fun addSubtask(context: Context, contents: String){
        val newTask = Task(done = false, contents = contents, depth = this.depth+1)
        this.subTasks += newTask
        newTask.initUI(context, this.subtaskLinearLayout)
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
     * タスクのインスタンスを生成したら実行し初期化する
     * @param parentView 追加先のLinearLayout
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
            selectAt(this.id, !selected)
            true
        }

        subtaskLinearLayout.addView(chk)
        parentView.addView(subtaskLinearLayout)

        for(task in subTasks){
            task.initUI(context, subtaskLinearLayout)
        }
    }
}