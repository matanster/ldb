import com.articlio.ldb
import scala.io.Source
//import java.net.URLEncoder
//import spray.json._
//import DefaultJsonProtocol._
import org.ahocorasick.trie._
//import scala.collection.JavaConversions._ // work with Java collections as if they were Scala
import scala.collection.JavaConverters._    // convert Java colllections to Scala ones
import com.github.tototoshi.csv._           // only good for "small" csv files; https://github.com/tototoshi/scala-csv/issues/11

object Boot extends App {
  
  println("main starting...")

  val wildCards = List("…", "...")

  def getDb() {

    val reader = CSVReader.open("/home/matan/Downloads/July 24 2014 database - Markers.csv")
    val t0 = System.nanoTime()
    reader.foreach(fields => 
      { 
        //println(fields); println(fields.getClass) 
        val asArray: Array[String] = fields.toArray // convert to Array for easy column access
        val pattern = asArray(2)
        val indication = asArray(3)
      }
    )
    val t1 = System.nanoTime()
    println(s"initializing from csv took ${(t1-t0)/1000/1000} milliseconds")

    reader.close
    //println(reader.all)
  }

  //
  // Search all strings within one sentence
  //
  def search() {

    val trie = new Trie
    trie.onlyWholeWords();
    trie.addKeyword("sugar")
    val emitsJ = trie.parseText("sugar cane sugarcane sugar canesugar")
    println(s"java type: ${emitsJ.getClass}")
    
    val emits = emitsJ.asScala map (i => Map("start" -> i.getStart, "end" -> i.getEnd, "match" -> i.getKeyword))
    println(s"scala converted type: ${emits.getClass}")
    println(s"scala converted value: $emits")
  
    //container.logger.info("starting linguistic database verticle")
    /*
    System.out.println("starting linguistic database verticle")
    val databaseInputFile = "/home/matan/Downloads/July 24 2014 database - Markers.csv"
    for (row <- Source.fromFile(databaseInputFile).getLines()) {
      val array = row.split(",")
    }
    */
  }

  getDb

}