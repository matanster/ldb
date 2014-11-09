package com.articlio.util

//
// Semantically routes messages to destinations, currently only to local file destinations
//
class Logger (name: String) {

  //
  // take care of a ready empty target directory - move out to util package
  //
  def createDir(targetDirName: String) {
    import java.io.{File}
    import java.nio.file.{Path, Paths, Files}
    import org.apache.commons.io.FileUtils.{deleteDirectory}
    
    def based(dir: String) = "data" + "/" + dir
    
    val targetDirObj = Paths.get(based(targetDirName))
    if (Files.exists(targetDirObj)) deleteDirectory(new File(based(targetDirName)))
    Files.createDirectory(targetDirObj)
  }
  
  createDir(name)
  println(s"opening logger: $name")
  
  import java.nio.file.{Paths, Files, StandardOpenOption}
  import java.nio.charset.StandardCharsets

  val openFiles = scala.collection.mutable.Map.empty[String, java.nio.file.Path]

  // 
  // initiates a file destination for given type
  //
  private def initializeType(msgType:String): java.nio.file.Path =
  {
    val fileName = "data/" + name + "/"+ msgType + ".out"
    val file = Paths.get(fileName)
    Files.deleteIfExists(file)
    file
  }

  //
  // writes message 
  //
  def write(message:String, msgType:String) = {
    if (!openFiles.contains(msgType)) openFiles(msgType) = initializeType(msgType)
    val bytes = message + "\n"
    Files.write(openFiles(msgType), bytes.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND) // buffered writing may be more performant than this... see java.nio.file...
  }
}
