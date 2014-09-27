package com.articlio.util
import scala.Console._


object Console {
  def log(message:String, msgType: String) {
  // Available colors and styling at: http://www.scala-lang.org/api/2.10.2/index.html#scala.Console$
    msgType match {
      case "performance" => println(MAGENTA + message + RESET)
      case "startup"     => println(BOLD + GREEN + message + RESET)
    }
  }
}