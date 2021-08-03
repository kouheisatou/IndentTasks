package com.KHsoftware.IndentTasks

interface TaskInterface {
    fun selectAt(id: Int, select: Boolean)
    fun makeAllSubtaskSelected(select: Boolean)
    fun deleteSubtaskById(id: Int)
    fun foldSubtasks(fold: Boolean, applyToSubtasks: Boolean)
    fun findTaskById(id: Int): Task?
    fun export(): String
    fun setSubtaskDraggable(draggable: Boolean, selectedTask: Task)
    fun addSubtask(contents: String, masterTask: MasterTask?, taskBuilder: TaskBuilder)
}