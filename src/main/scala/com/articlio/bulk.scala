package com.articlio
import java.io.File
import com.articlio.util.runID
import com.articlio.input.JATS

class Bulk(runID: String) { 
  
  //ldb.ldb.init
  def processAll(runID: String, sourceDirName: String, treatAs: Option[String] = None) {
    val files = new File(sourceDirName).listFiles.filter(file => (file.isFile)) // && file.getName.endsWith(".xml")))
    files.par.foreach(file => {
      val fileName = file.getName  
      println("about to process file " + fileName)
      treatAs match {
        case Some(s) => ldb.ldb.go(runID, new JATS(s"$sourceDirName/$fileName", s))
        case None => ldb.ldb.go(runID, new JATS(s"$sourceDirName/$fileName"))
      }
    }) 
  }

  def allPDF = processAll(runID, config.pdf, Some("pdf-converted"))
  def alleLife = processAll(runID, config.eLife)
  
  def all {
    allPDF
    alleLife
  } 
    
    //ldb.ldb.go(new JATS("../data/ready-for-semantic/from-pdf/management 14", "pdf-converted"))
    //ldb.ldb.go(new JATS("../data/ready-for-semantic/eLife-JATS/elife03399.xml", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/test", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/Rayner (1998)", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/imagenet", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/prep/elife03399.xml"))
 
}
