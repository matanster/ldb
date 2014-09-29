package com.articlio.ldb
import com.articlio.util

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

// rule parameter classes
abstract class parameter (nickname: String)
class mustInclude (nickname: String, mustHave: Set[String]) extends parameter (nickname)
class mustBeUnder (nickname: String, section: Set[String]) extends parameter (nickname)

case class RawInput (pattern: String, indication: String, parameters: Seq[String]) 

object csv {

  //
  // Extract raw values from database CSV (skipping the first headers done really not scala-idiomatically for now..)
  //
  def getCSV : Seq[RawInput] = {

    util.Timelog.timer("reading CSV")
    val reader = CSVReader.open("ldb/July 24 2014 database - Markers - filtered.csv")
    val iterator = reader.iterator
    iterator.next // skip first row assumed to be headers

    var rawInput = Seq[RawInput]() 

    while (iterator.hasNext)
    { 
      val asArray: Array[String] = iterator.next.toArray // convert to Array for easy column access
      val pattern = asArray(2)
      val indication = asArray(3)
      val parameters : Seq[String] = Seq(asArray(6), asArray(7)) // additional parameters expressed in the database CSV
      
      rawInput = rawInput :+ new RawInput(pattern, indication, parameters)
    }

    rawInput map { rule =>   
      // detect expressed rules
      rule.parameters.foreach(parameter => {
        val s = Seq(Map("self ref" -> "self-ref"), Map("diectic" -> "diectic-ref"))
        //if (parameter.containsSlice("self ref")) => new MustInclude("self-ref", Set("self ref")
        //if (parameter.containsSlice("diectic"))  => new MustInclude("diectic-ref", Set("diectic")
      })
    }

    util.Timelog.timer("reading CSV")

    reader.close
    return rawInput
  }
}