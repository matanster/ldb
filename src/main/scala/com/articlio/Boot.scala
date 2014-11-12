import com.articlio.input.JATS
import com.articlio.ldb
import com.articlio.selfMonitor.{Monitor}
import com.articlio.util.{Console}
import com.articlio.storage
import java.io.File

object Boot extends App {

  Monitor

  try {

    ldb.ldb.init
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
    
    processAll("../data/ready-for-semantic/converted-to-JATS", Some("pdf-converted"))
    processAll("../data/ready-for-semantic/eLife-JATS")
    
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/prep/elife02576.xml//", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/test", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/Rayner (1998)", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/imagenet", "pdf-converted"))
    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/prep/elife03399.xml"))

    val data = Seq(("something new", "matches something new", "indicates something"),
                   ("something new", "matches something new", "indicates something"))

    storage.OutDB.dropCreate
    storage.OutDB ++= (data)
    
    } finally {
        // closing stuff - to be moved to own function
        Monitor.shutdown
        storage.OutDB.close
      }
}