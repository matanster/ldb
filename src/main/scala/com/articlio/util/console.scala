package com.articlio.util

object Console {

  import scala.Console._     // Available colors and styling at: http://www.scala-lang.org/api/2.10.2/index.html#scala.Console$
  val GRAY    = "\033[90m"   // And some more color
  val ITALICS = "\033[3m"    // And some more style

  def log(message:String, msgType: String) {

    msgType match {
      case "performance" => println(MAGENTA + message + RESET)
      case "startup"     => println(GREEN + message + RESET)
      case "timers"      => println(message)
      case _ => // swallow the message
    }
  }
}