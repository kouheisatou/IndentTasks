package com.KHsoftware.IndentTasks

import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel

// 表示中のタスク
@SuppressLint("StaticFieldLeak")
lateinit var taskBuilder: TaskBuilder

// 選択されているタスクリストの名前
var selectedTaskList = ""

// スピナーのアダプタ
lateinit var adapter: ArrayAdapter<String>

// デバイス内に保存されたテキストファイル名リスト
var files = mutableListOf<String>()

class TaskViewModel : ViewModel() {

}