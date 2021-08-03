package com.KHsoftware.IndentTasks

interface TaskInterface {
    // ユーザの操作
    fun selectAt(id: Int, select: Boolean)
    fun makeAllSubtaskSelected(select: Boolean)
    fun deleteSubtaskById(id: Int)  // undo, redo
    fun foldSubtasks(fold: Boolean, applyToSubtasks: Boolean)
    fun findTaskById(id: Int): Task?
    fun export(): String
    fun setSubtaskDraggable(draggable: Boolean, selectedTask: Task)
    fun addSubtask(contents: String, masterTask: MasterTask?, taskBuilder: TaskBuilder)

    // undo,redoで使用
    fun add(id: Int, contents: String)
    fun check(isDone: Boolean, id: Int, applyToSubtasks: Boolean)
    fun fold(fold: Boolean, id: Int, applyToSubtasks: Boolean)
    fun swap(parent: Task?, indexA: Int, indexB: Int)
    fun delete(id: Int)
    fun edit(id: Int, contents: String)
}