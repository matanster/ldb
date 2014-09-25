package com.articlio.selfMonitor

import org.vertx.scala.core._
import org.vertx.scala.platform.Verticle

//
// Monitor own-JVM memory usage
//
object Monitor extends Verticle {

  def percentThreshold = 10
  def interval = 1000
  val timer = new java.util.Timer("selfMonitor") // give the timer thread a name for ops-friendliness

  // get the hook to memory consumption
  import runtime.{ totalMemory, freeMemory, maxMemory } // http://stackoverflow.com/questions/3571203/what-is-the-exact-meaning-of-runtime-getruntime-totalmemory-and-freememory
  private val runtime = Runtime.getRuntime()

  var former = getMem
  var current = former

  def getMem: Map[String, Float] = {
    val heapUsed  = totalMemory - freeMemory
    val heapTotal = maxMemory
    Map("heapUsed" -> heapUsed,
        "heapTotal" -> heapTotal,
        "heapPercent" -> heapUsed.toFloat / heapTotal * 100)
  }
  
  def logUsage (verb: String) {
    System.out.println(f"JVM Heap usage ${verb} ${current("heapPercent")}%.1f" + "%" + f" of JVM heap (${current("heapUsed")/1024/1024}%.0fMB of ${current("heapTotal")/1024/1024}%.0fMB)")
  }

  def logUsageIfChanged {

    current = getMem

    if (math.abs(current("heapPercent") - former("heapPercent")) / former("heapPercent") > (percentThreshold.toFloat/100)) {    
      if (current("heapPercent") > former("heapPercent")) {
        logUsage("increased to")
      }
      else {
        logUsage("decreased to")        
      }
      former = current
    }

  }

  def shutdown {
    timer.cancel
  }

  override def start { /* keep function name for vert.x compatibility */
    println("starting self-monitoring")
    logUsage("is")
    logUsageIfChanged
    // under vertx, simply: val timer = vertx.setPeriodic(interval, { timerID: Long => logUsageIfChanged })
    val recur = new java.util.TimerTask { override def run = logUsageIfChanged }
    timer.schedule(recur, 0.toLong, interval.toLong)
  }

  start

}