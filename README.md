# SparkTaskMetrics
Task Metrics Explorer - a tool that explode task internal metrics

* Build: run `sbt package`


Sometimes it is useful to be able to query the job internal metrics as a table. Previously, we can check on Spark UI and then find out for each task, when did it launch, what is the duration, and what is the input size/ records for the task, etc. Now, you can use this package to extract this information to a DataFrame and query! For example:

```scala
import com.databricks.TaskMetricsExplorer
 
val t = new TaskMetricsExplorer(spark)
sql("""CREATE OR REPLACE TEMPORARY VIEW nested_data AS
       SELECT id AS key,
       ARRAY(CAST(RAND(1) * 100 AS INT), CAST(RAND(2) * 100 AS INT), CAST(RAND(3) * 100 AS INT), CAST(RAND(4) * 100 AS INT), CAST(RAND(5) * 100 AS INT)) AS values,
       ARRAY(ARRAY(CAST(RAND(1) * 100 AS INT), CAST(RAND(2) * 100 AS INT)), ARRAY(CAST(RAND(3) * 100 AS INT), CAST(RAND(4) * 100 AS INT), CAST(RAND(5) * 100 AS INT))) AS nested_values
       FROM range(5)""")
val query = sql("""SELECT * FROM nested_data""").show(false)
val res = t.runAndMeasure(query)
```

The `runAndMeasure` will proceed to run the command and get tasks internal metrics using a Spark listener. It will then run the query eagerly and return the result:

```
+---+-------------------+-----------------------+
|key|values             |nested_values          |
+---+-------------------+-----------------------+
|0  |[26, 11, 66, 8, 47]|[[26, 11], [66, 8, 47]]|
|1  |[66, 8, 47, 91, 6] |[[66, 8], [47, 91, 6]] |
|2  |[8, 47, 91, 6, 70] |[[8, 47], [91, 6, 70]] |
|3  |[91, 6, 70, 41, 19]|[[91, 6], [70, 41, 19]]|
|4  |[6, 70, 41, 19, 12]|[[6, 70], [41, 19, 12]]|
+---+-------------------+-----------------------+
 
Time taken: 0 ms
```

The task metric information will be saved in a DataFrame, in our case, you can check it by just displaying it:

```scala
res.select($"stageId", $"taskType", $"taskLocality", $"executorRunTime", $"duration", $"executorId", $"host", $"jvmGCTime").show(false)
```

```
+-------+----------+-------------+---------------+--------+----------+---------+---------+
|stageId|taskType  |taskLocality |executorRunTime|duration|executorId|host     |jvmGCTime|
+-------+----------+-------------+---------------+--------+----------+---------+---------+
|3      |ResultTask|PROCESS_LOCAL|2              |9       |driver    |localhost|0        |
|4      |ResultTask|PROCESS_LOCAL|3              |11      |driver    |localhost|0        |
|4      |ResultTask|PROCESS_LOCAL|3              |16      |driver    |localhost|0        |
|4      |ResultTask|PROCESS_LOCAL|2              |20      |driver    |localhost|0        |
|4      |ResultTask|PROCESS_LOCAL|4              |22      |driver    |localhost|0        |
|5      |ResultTask|PROCESS_LOCAL|2              |12      |driver    |localhost|0        |
|5      |ResultTask|PROCESS_LOCAL|3              |17      |driver    |localhost|0        |
|5      |ResultTask|PROCESS_LOCAL|7              |21      |driver    |localhost|0        |
+-------+----------+-------------+---------------+--------+----------+---------+---------+
```

You may wonder what are the metrics can I get? Here is the schema of the res DataFrame:

```
root
 |-- stageId: integer (nullable = false)
 |-- stageAttemptId: integer (nullable = false)
 |-- taskType: string (nullable = true)
 |-- index: long (nullable = false)
 |-- taskId: long (nullable = false)
 |-- attemptNumber: integer (nullable = false)
 |-- launchTime: long (nullable = false)
 |-- finishTime: long (nullable = false)
 |-- duration: long (nullable = false)
 |-- schedulerDelay: long (nullable = false)
 |-- executorId: string (nullable = true)
 |-- host: string (nullable = true)
 |-- taskLocality: string (nullable = true)
 |-- speculative: boolean (nullable = false)
 |-- gettingResultTime: long (nullable = false)
 |-- successful: boolean (nullable = false)
 |-- executorRunTime: long (nullable = false)
 |-- executorCpuTime: long (nullable = false)
 |-- executorDeserializeTime: long (nullable = false)
 |-- executorDeserializeCpuTime: long (nullable = false)
 |-- resultSerializationTime: long (nullable = false)
 |-- jvmGCTime: long (nullable = false)
 |-- resultSize: long (nullable = false)
 |-- numUpdatedBlockStatuses: integer (nullable = false)
 |-- diskBytesSpilled: long (nullable = false)
 |-- memoryBytesSpilled: long (nullable = false)
 |-- peakExecutionMemory: long (nullable = false)
 |-- recordsRead: long (nullable = false)
 |-- bytesRead: long (nullable = false)
 |-- recordsWritten: long (nullable = false)
 |-- bytesWritten: long (nullable = false)
 |-- shuffleFetchWaitTime: long (nullable = false)
 |-- shuffleTotalBytesRead: long (nullable = false)
 |-- shuffleTotalBlocksFetched: long (nullable = false)
 |-- shuffleLocalBlocksFetched: long (nullable = false)
 |-- shuffleRemoteBlocksFetched: long (nullable = false)
 |-- shuffleWriteTime: long (nullable = false)
 |-- shuffleBytesWritten: long (nullable = false)
 |-- shuffleRecordsWritten: long (nullable = false)
 |-- errorMessage: string (nullable = true)
```
