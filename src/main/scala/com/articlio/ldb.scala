package com.articlio.ldb

import scala.io.Source
//import java.net.URLEncoder
//import spray.json._
//import DefaultJsonProtocol._
import org.ahocorasick.trie._
//import scala.collection.JavaConversions._ // work with Java collections as if they were Scala
import scala.collection.JavaConverters._    // convert Java colllections to Scala ones
import com.github.tototoshi.csv._           // only good for "small" csv files; https://github.com/tototoshi/scala-csv/issues/11
//import scala.collection.mutable.MutableList
import org.apache.commons.math3

object go {
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

  println("main starting...")

  //
  // Extract raw values from database CSV (skipping the first headers done really not scala-idiomatically for now..)
  //
  def getCSV : List[Map[String, String]] = {

    val t0 = System.nanoTime()
    val reader = CSVReader.open("/home/matan/Downloads/July 24 2014 database - Markers.csv")
    val iterator = reader.iterator
    iterator.next // skip first row assumed to be headers

    var rawInputs = List[Map[String, String]]() // switch to val scala.collection.mutable.MutableList and += for more correct Scala
                                               // http://stackoverflow.com/questions/6557169/how-to-declare-empty-list-and-then-add-string-in-scala

    while (iterator.hasNext)
    { 
      val asArray: Array[String] = iterator.next.toArray // convert to Array for easy column access
      val pattern = asArray(2)
      val indication = asArray(3)
      rawInputs ::= Map("pattern" -> pattern, "indication" -> indication)
    }

    val t1 = System.nanoTime()
    println(s"initializing from csv took ${(t1-t0)/1000/1000} milliseconds")

    reader.close
    return rawInputs
  }


  def contriveSearchStrings(rawInputs: List[Map[String, String]]) =
  {
    val wildcards = List("..", "…") // wildcard symbols allowed to the human who codes the CSV database
    val wildchars = List('.', '…', ' ')  // characters indication whether we are inside a wildcard sequence.. hence - "wildchars"

    type matchSequence = (String, scala.collection.mutable.ListBuffer[String])

    object all {
      val fragments = scala.collection.mutable.ListBuffer.empty[String]
      val matchSequences = scala.collection.mutable.ListBuffer.empty[matchSequence]
    }

    def breakDown(rawPattern: String): List[String] = {

      val indexes = wildcards map rawPattern.indexOf filter { i: Int => i > -1 } // discard non-founds
      if (indexes.isEmpty) {
        return(List(rawPattern))
      }
      else {
        val pos = indexes.min
        val (leftSide, rest) = rawPattern.splitAt(pos)
        val rightSide = rest.dropWhile((char) => wildchars.exists((wildchar) => char == wildchar))
        return List(leftSide) ::: breakDown(rightSide)
      }
    }

    var i = 1
    rawInputs.foreach(rawInput => {
      val raw = rawInput("pattern")
      val rowFragments = breakDown(raw)
      if (rowFragments.length > 1) 
      {
        i += 1
        println(rowFragments)
      }
    })

    println(i)

  }
  
  val rawInput = getCSV
  contriveSearchStrings(rawInput)
}