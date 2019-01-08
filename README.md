# SparkTaskMetrics
Task Metrics Explorer

Build:

run `sbt package`

Usage:

```scala
import com.databricks.TaskMetricsExplorer

val t = new TaskMetricsExplorer(spark)
val query = sql("select 1 + 1").show()
val res = t.runAndMeasure(query)
```

You can check the result:

```scala
res.select($"stageId", $"taskType", $"taskLocality", $"executorRunTime").show(false)
```

Result:

```
+-------+----------+-------------+---------------+
|stageId|taskType  |taskLocality |executorRunTime|
+-------+----------+-------------+---------------+
|0      |ResultTask|PROCESS_LOCAL|167            |
+-------+----------+-------------+---------------+
```
