package com.databricks

import scala.collection.mutable

import org.apache.spark.executor.TaskMetrics
import org.apache.spark.scheduler._
import org.apache.spark.TaskEndReason

class TaskInfoRecorderListener extends SparkListener {
  val taskInfoMetrics = mutable.Buffer[(Int, Int, String, TaskInfo, TaskMetrics, TaskEndReason)]()

  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = synchronized {
    if (taskEnd.taskInfo != null && taskEnd.taskMetrics != null) {
      taskInfoMetrics += ((taskEnd.stageId,
        taskEnd.stageAttemptId,
        taskEnd.taskType,
        taskEnd.taskInfo,
        taskEnd.taskMetrics,
        taskEnd.reason))
    }
  }
}
