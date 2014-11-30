package com.articlio
import java.io.File
import com.articlio.input.JATS

object Bulk { 
 
    //ldb.ldb.init
    def processAll(sourceDirName: String, treatAs: Option[String] = None) {
      val files = new File(sourceDirName).listFiles.filter(file => (file.isFile)) // && file.getName.endsWith(".xml")))
      files.foreach(file => {
        val fileName = file.getName  
        println("about to process file " + fileName)
        treatAs match {
          case Some(s) => ldb.ldb.go(new JATS(s"$sourceDirName/$fileName", s))
          case None => ldb.ldb.go(new JATS(s"$sourceDirName/$fileName"))
        }
      }) 
    }

    def allPDF = processAll(config.pdf, Some("pdf-converted"))
    def alleLife = processAll(config.eLife)
    
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
