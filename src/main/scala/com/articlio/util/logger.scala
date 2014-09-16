package com.articlio.util

import java.nio.file.{Paths, Files, StandardOpenOption}
import java.nio.charset.StandardCharsets

object Logger {
  val fileName = "log.log"
  val outFile = Paths.get(fileName)
  Files.deleteIfExists(outFile)

  def write(message:String) = {
    val bytes = message + "\n"
    Files.write(outFile, bytes.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
  }
}
