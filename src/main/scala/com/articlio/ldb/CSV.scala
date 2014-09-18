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
//import org.apache.commons.math3           // for using descriptive statistics over collections

object csv {
  //
  // Extract raw values from database CSV (skipping the first headers done really not scala-idiomatically for now..)
  //
  def getCSV : List[Map[String, String]] = {

    val t0 = System.nanoTime()
    val reader = CSVReader.open("ldb/July 24 2014 database - Markers - filtered.csv")
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
}