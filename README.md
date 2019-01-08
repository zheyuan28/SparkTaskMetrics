# SparkTaskMetrics
Task Metrics Explorer - a tool that explode task internal metrics

* Build: run `sbt package`

* Usage:

```scala
import com.databricks.TaskMetricsExplorer

val t = new TaskMetricsExplorer(spark)
val query = sql("select 1 + 1").show()
val res = t.runAndMeasure(query)
```

* Result:

```scala
res.select($"stageId", $"taskType", $"taskLocality", $"executorRunTime").show(false)
```

```
+-------+----------+-------------+---------------+
|stageId|taskType  |taskLocality |executorRunTime|
+-------+----------+-------------+---------------+
|0      |ResultTask|PROCESS_LOCAL|167            |
+-------+----------+-------------+---------------+
```
