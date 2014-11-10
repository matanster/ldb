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
    val sourceDirName = "/home/matan/ingi/repos/fileIterator/data/toJATS"
    val files = new File(sourceDirName).listFiles.filter(file => (file.isFile)) // && file.getName.endsWith(".xml")))
    files.foreach(file => {
      val fileName = file.getName  
      ldb.ldb.go(new JATS(s"$sourceDirName/$fileName", "pdf-converted"))
    })
    
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