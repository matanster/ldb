import com.articlio.input.JATS
import com.articlio.config
import com.articlio.HttpService
import com.articlio.ldb
import com.articlio.selfMonitor.{Monitor}
import com.articlio.util.{Console}
import com.articlio.storage
import java.io.File

object Analytic {
  val inDb = storage.InDB.Query("ubuntu-2014-11-21T12:06:51.286Z")
  storage.InDB.close
}

object Boot extends App {

  Monitor

  storage.OutDB.dropCreate
  
  ldb.ldb.init
  
  val httpService = HttpService
     
  def bulk { 
	  try {
	
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
	    
	    //processAll(config.pdf, Some("pdf-converted"))
	    //processAll(config.eLife)
	    
	    //ldb.ldb.go(new JATS("../data/ready-for-semantic/from-pdf/management 14", "pdf-converted"))
	    //ldb.ldb.go(new JATS("../data/ready-for-semantic/eLife-JATS/elife03399.xml", "pdf-converted"))
	    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/test", "pdf-converted"))
	    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/Rayner (1998)", "pdf-converted"))
	    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/toJATS/imagenet", "pdf-converted"))
	    //ldb.ldb.go(new JATS("/home/matan/ingi/repos/fileIterator/data/prep/elife03399.xml"))
	
	    
	    } finally {
	        // closing stuff - to be moved to own function
	        Monitor.shutdown
	        storage.OutDB.close
	      }
	}
}