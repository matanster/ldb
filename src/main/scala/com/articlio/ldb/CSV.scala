package com.articlio.ldb
import com.articlio.util._
import com.articlio.util.Text._
//import com.articlio.util.{wordFollowing}

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
import com.github.verbalexpressions.VerbalExpression._

// rule parameter classes
//class Include (nickname: Symbol, ave: Set[String]) extends parameter (nickname)
//class BeUnder (nickname: Symbol, section: Set[String]) extends parameter (nickname)

case class RawInput (pattern: String, indication: String, parameters: Seq[String]) 

case class Parameter (nickname: Symbol, parameterType: Symbol, necessity: Symbol)
case class Rules (pattern: String, indication: String, parameters: Parameter) 
//case class Rule (pattern: String, indication: String, parameters: Seq[String]) extends RawInput (pattern, indication, parameters)

object csv {

  //
  // Extract raw values from database CSV (skipping the first headers done really not scala-idiomatically for now..)
  //
  def getCSV : Seq[RawInput] = {

    Timelog.timer("reading CSV")
    val reader = CSVReader.open("ldb/July 24 2014 database - Markers - filtered.csv")
    val iterator = reader.iterator
    iterator.next // skip first row assumed to be headers

    var rawInput = Seq[RawInput]() 

    while (iterator.hasNext)
    { 
      val asArray: Array[String] = iterator.next.toArray // convert to Array for easy column access
      val pattern = asArray(2)
      val indication = asArray(3)
      val parameters : Seq[String] = Seq(asArray(5), asArray(6)) // additional parameters expressed in the database CSV
      
      rawInput = rawInput :+ new RawInput(pattern, indication, parameters)
    }

    rawInput map { rule =>   
      rule.parameters filter (_.nonEmpty) foreach (parameter => {

        val selfRef    : Boolean             = parameter.containsSlice("self ref")
        val deicticRef : Boolean             = parameter.containsSlice("deictic")
        val section    : Option[Seq[String]] = wordFollowingAny(parameter, Seq("in ", "or in "))
        val modality   : Symbol              = if (parameter.containsSlice("no ") | parameter.containsSlice("not ")) 'mustNot 
                                               else 'must
        /*
        if (section.isDefined) {
          println(section.get)
          println(parameter)
        }*/

        /*
        if (selfRef) {
          println(s"Self Ref: $selfRef")
          println(parameter)
        }*/


        /*
        if (deicticRef) {
          println(s"deicticRef Ref: $deicticRef")
         nln(parameter)
        }*/    

        if (modality == 'must) {
          println(s"modality: $modality")
          println(parameter)
        }

      })
    }

    Timelog.timer("reading CSV")

    reader.close
    return rawInput
  }
}