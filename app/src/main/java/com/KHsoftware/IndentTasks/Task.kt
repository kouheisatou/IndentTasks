package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import com.jmedeisis.draglinearlayout.DragLinearLayout
import kotlinx.android.synthetic.main.activity_main.view.*

// できるだけ画面描画は描画するだけ、内部処理の結果を出すだけにする -> redraw()
// todo Taskを継承してmasterTaskを作成、これは一つしか存在しないsingleton
// todo dragLinearLayoutで選択中のタスクを同じ階層内でドラッグできるように

open class Task(var done: Boolean, var contents: String, val subTasks: MutableList<Task> = mutableListOf(), val depth: Int){

    /** このタスクの選択状態 **/
    var selected = false
    /** タスクのID **/
    var id = generateId()

    /** このタスクを親とする階層全体の線形レイアウトView **/
    lateinit var subtaskLinearLayout: DragLinearLayout
    /** 折り畳み/展開ボタン **/
    lateinit var foldButton: ImageView
    /** タスクの内容TextView **/
    lateinit var contentsText: TextView
    /** 完了チェックボックス **/
    lateinit var chk: CheckBox
    lateinit var context: Context

    open fun generateId() = ++MasterTask.taskNum

    /**
     * 指定したIDのタスクとそのサブタスクを選択する
     * @param id 選択するタスクのID
     * @param select true:選択, false:選択解除
     */
    fun selectAt(id: Int, select: Boolean){

        // このIDのタスクが選択された時
        if(id == this.id){
            selected = select

            // 選択状態をリセット
            MasterTask.unselectAll()

            // 選択された場合はマスタータスクに選択されたタスクを保持、選択が解除された場合はnullを代入
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

            // 選択されたタスクのサブタスクのドラッグを有効にする
            if(select){
                setSubtaskDraggable(true, context)
            }

        }else{
            // このタスクのIDが指定したIDと一致しない場合、サブタスクのIDも検索する
            for(i in subTasks){
                i.selectAt(id, select)
            }
        }
    }

    fun unselectAll(){

        // 選択色を解除
        val color = Color.parseColor("#FFFFFF")
        MasterTask.selectedTask?.subtaskLinearLayout?.setBackgroundColor(color)

        // 全てのサブタスクで実行
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

    /**
     * サブタスクが0のときに折り畳みボタンを非表示にする
     * タスクが追加された時に必ずMasterTaskから全てのタスクに対して実行
     */
    fun setFoldButton(){
        foldButton.alpha = if(subTasks.size == 0) { 0f } else { 1f }
        for(task in subTasks){
            task.setFoldButton()
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

    // 引数のIDのタスクを削除する
    fun deleteSubtaskById(id: Int, context: Context){
        for(task in subTasks){
            if(id == task.id){
                // 選択されているタスクを削除した時
                if(id == MasterTask.selectedTask?.id){
                    MasterTask.selectedTask = null
                }
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
        // 選択されている場合は追加するサブタスクにも選択色を付ける
        if(selected){
            newTask.subtaskLinearLayout.setBackgroundColor(Color.parseColor("#DDDDDD"))
            setSubtaskDraggable(true, context)
        }
    }

    // 引数のIDのタスクを返す
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
     * サブタスクのドラッグの有効化/無効化を切り替える
     * 必ずサブタスクのinitUI()の後に実行
     * @param draggable true:ドラッグを有効化, false:ドラッグ無効化
     */
    fun setSubtaskDraggable(draggable: Boolean, context: Context){
        if(draggable){
            for(task in subTasks){
                val dragView = task.subtaskLinearLayout
                val handle = task.subtaskLinearLayout
                subtaskLinearLayout.setViewDraggable(dragView, handle)
                // ドラッグによるviewの入れ替えに伴い、サブタスク配列の順序も入れ替える
                subtaskLinearLayout.setOnViewSwapListener { firstView, firstPosition, secondView, secondPosition ->
                    Log.d("swap", "$firstPosition <-> $secondPosition")
                    val temp = subTasks[firstPosition - 1]
                    subTasks[firstPosition - 1] = subTasks[secondPosition - 1]
                    subTasks[secondPosition - 1] = temp
                }
            }
        }else{
            subtaskLinearLayout.removeAllViews()
            initUI(context, subtaskLinearLayout)
            for(task in subTasks){
                task.setSubtaskDraggable(false, context)
            }
        }
    }

    /**
     * このタスクの情報からAndroidの画面上のレイアウトを生成
     * タスクのインスタンスを生成したら実行しUIを初期化する
     * @param parentView 追加先のLinearLayout
     */
    @SuppressLint("SetTextI18n")
    open fun initUI(context: Context, parentView: DragLinearLayout){
        this.context = context

        // タスク全体のLinearLayout
        subtaskLinearLayout = DragLinearLayout(context)
        subtaskLinearLayout.orientation = LinearLayout.VERTICAL
        var layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        subtaskLinearLayout.layoutParams = layoutParams
        subtaskLinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))

        // 各タスクの横向き行LinearLayout
        val rowLinearLayout = LinearLayout(context)
        rowLinearLayout.orientation = LinearLayout.HORIZONTAL
        rowLinearLayout.layoutParams = layoutParams

        // 余白用View
        for(i in 0 until depth){
            val margin = TextView(context)
            margin.text = "  │"
            margin.gravity = LinearLayout.TEXT_ALIGNMENT_CENTER
            margin.width = 50
            rowLinearLayout.addView(margin)
        }

        // 完了チェックボックス
        chk = CheckBox(context)
        chk.isChecked = done
        // 長押しでタスク選択
        chk.setOnLongClickListener(){
            selectAt(this.id, !selected)
            true
        }

        // タスクの内容TextView
        contentsText = TextView(context)
        contentsText.text = "($id)$contents"
        // 長押しでタスク選択
        contentsText.setOnLongClickListener(){
            selectAt(this.id, !selected)
            true
        }

        // 確定ボタン
        val confirmBtn = TextView(context)
        confirmBtn.text = "↩︎"
        confirmBtn.textSize = 30f
        confirmBtn.isVisible = false

        // タスク編集時のEditText
        val editText = EditText(context)
        editText.setText(contentsText.text)
        editText.isVisible = false

        //折り畳みボタン
        foldButton = ImageView(context)
        foldButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        foldButton.layoutParams = buttonParams
        foldButton.setOnClickListener(){
            foldButton.rotation += 180F
            for(task in subTasks){
                task.subtaskLinearLayout.isVisible = !task.subtaskLinearLayout.isVisible
            }
        }
        // サブタスクが0の時以外折り畳みボタン表示
        foldButton.alpha = if(subTasks.size == 0) { 0f } else { 1f }

        // タスク内容テキスト編集時の挙動
        contentsText.setOnClickListener(){
            contentsText.isVisible = false
            editText.isVisible = true
            confirmBtn.isVisible = true
        }
        confirmBtn.setOnClickListener(){
            contentsText.text = editText.text
            contentsText.isVisible = true
            editText.isVisible = false
            confirmBtn.isVisible = false
        }

        // 各Viewをアタッチ
        rowLinearLayout.addView(foldButton)
        rowLinearLayout.addView(chk)
        rowLinearLayout.addView(contentsText)
        rowLinearLayout.addView(editText)
        rowLinearLayout.addView(confirmBtn)
        subtaskLinearLayout.addView(rowLinearLayout)
        parentView.addView(subtaskLinearLayout)

        // サブタスクのinitUIを実行
        for(task in subTasks){
            task.initUI(context, subtaskLinearLayout)
        }
    }
}