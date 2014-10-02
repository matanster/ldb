package com.articlio.ldb
import com.articlio.util._
import com.articlio.util.text._
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

//
// structure for input CSV representation
//
case class RawCSVInput (pattern: String, indication: String, parameters: Seq[String]) 

//
// class hierarchy for describing rule properties
//
abstract class Property {
  val subType: Symbol
  val necessityModality: Symbol
}

case class ReferenceProperty (subType: Symbol, 
                              necessityModality: Symbol) 
                              extends Property 

case class LocationProperty (subType: Symbol, 
                             parameter: Seq[String], 
                             necessityModality: Symbol)
                             extends Property


//
// final rule representation
//
case class Rule (pattern: String, indication: String, properties: Option[Seq[Property]]) 


object CSV {

  //
  // Extract raw values from database CSV (skipping the first headers done really not scala-idiomatically for now..)
  //
  def getCSV : Seq[RawCSVInput] = {

    Timelog.timer("reading CSV")

    val reader = CSVReader.open("ldb/July 24 2014 database - Markers - filtered.csv")
    val iterator = reader.iterator
    iterator.next // skip first row assumed to be headers

    var rawInput = Seq[RawCSVInput]() 

    while (iterator.hasNext)
    { 
      val asArray: Array[String] = iterator.next.toArray // convert to Array for easy column access
      val pattern = asArray(2)
      val indication = asArray(3)
      val parameters : Seq[String] = Seq(asArray(5), asArray(6)) // additional parameters expressed in the database CSV
      
      rawInput = rawInput :+ new RawCSVInput(pattern, indication, parameters)
    }

    Timelog.timer("reading CSV")
    reader.close

    rawInput
  }

  //
  // build rules from the raw CSV input rows
  //
  def deriveFromCSV : Seq[Rule] = {

    val rules = scala.collection.mutable.Seq.newBuilder[Rule]
    val rawInput = getCSV

    rawInput map { rawInputRule =>   

      val ruleProperties = scala.collection.mutable.Seq.newBuilder[Property]

      rawInputRule.parameters filter (_.nonEmpty) foreach (parameter => {

        val selfRef    : Boolean             = parameter.containsSlice("self ref")
        val deicticRef : Boolean             = parameter.containsSlice("deictic")
        val youRef     : Boolean             = parameter.containsSlice("\"you\"")
        val sections   : Option[Seq[String]] = wordFollowingAny(parameter, Seq("in ", "or in "))
        val modality   : Symbol              = if (parameter.containsSlice("no ") | parameter.containsSlice("not ")) 'mustNot 
                                               else 'must

        if (selfRef) ruleProperties += ReferenceProperty('self, modality)
        if (deicticRef) ruleProperties += ReferenceProperty('deictic, modality)
        if (youRef) ruleProperties += ReferenceProperty('you, modality)
        if (sections.isDefined) ruleProperties += LocationProperty('inside, sections.get, modality)

      })

      rules += new Rule(rawInputRule.pattern, rawInputRule.indication, if (ruleProperties.result.nonEmpty) Some(ruleProperties.result) else None)

    }

    Logger.write(rules.result.mkString("\n"), "db-rules")
    return rules.result
  }
}
