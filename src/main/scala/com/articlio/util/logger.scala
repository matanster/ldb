package com.articlio.util

//
// semantically routes messages to destinations, currently only to local file destinations
//
object Logger {

import java.nio.file.{Paths, Files, StandardOpenOption}
import java.nio.charset.StandardCharsets

  val openFiles:scala.collection.mutable.Map[String, java.nio.file.Path] = scala.collection.mutable.Map.empty[String, java.nio.file.Path]

  private def initializeType(msgType:String): java.nio.file.Path =
  {
    val fileName = "data/" + msgType + ".out"
    val file = Paths.get(fileName)
    Files.deleteIfExists(file)
    file
  }

  def write(message:String, msgType:String) = {
    if (!openFiles.contains(msgType)) {
      openFiles(msgType) = initializeType(msgType)
    }
    val bytes = message + "\n"
    Files.write(openFiles(msgType), bytes.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
  }
}
