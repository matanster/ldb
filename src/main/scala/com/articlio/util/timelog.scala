package com.articlio.util

//
// Simple facility for timing code - timings get data logged, and written to the console
//
object Timelog {

  val timers = scala.collection.mutable.Map.empty[String, Long]

  //
  // Usage: call once to start the timer, and once to stop it, using the same timer name parameter
  //
  def timer(timerName:String) = {
    if (timers contains timerName) {
      val output = s"$timerName took ${(System.nanoTime() - timers(timerName)) / 1000 / 1000} milliseconds"
      timers -= timerName
      Logger.write(output, "timers")
      Console.log(output, "timers")
    }
    else timers(timerName) = System.nanoTime()
  }

  //
  // Usage: wraps around a function (or code block)
  //
  def time[T](func: => T): T = {
    val start = System.nanoTime()
    val result = func // invoke the wrapped function
    val output = s"function took ${(System.nanoTime() - start) / 1000 / 1000} milliseconds"
    Logger.write(output, "timers")
    Console.log(output, "timers")
    return(result)
  }
}