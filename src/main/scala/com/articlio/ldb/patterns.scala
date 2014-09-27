package com.articlio.ldb

import com.articlio.util._
import com.articlio.selfMonitor.{Monitor}
import scala.io.Source
//import java.net.URLEncoder
//import spray.json._
//import DefaultJsonProtocol._
import org.ahocorasick.trie._
//import scala.collection.JavaConversions._ // work with Java collections as if they were Scala
import scala.collection.JavaConverters._    // convert Java colllections to Scala ones
import com.github.tototoshi.csv._           // only good for "small" csv files; https://github.com/tototoshi/scala-csv/issues/11

object go {

  //
  // Builds and exposes the data structures necessary for working the rules database
  //
  class patternsRepresentation(inputRules: List[Map[String, String]]) {

    // patterns to indications map - 
    // each pattern correlates to only one indictaion 
    val patterns2indications = scala.collection.mutable.HashMap.empty[String, String]

    // fragments to patterns map - 
    // patterns collections are scala Sets 
    // because order and appearing-more-than once are not necessary
    val fragments2patterns   = new collection.mutable.HashMap[String, collection.mutable.Set[String]] 
                               with collection.mutable.MultiMap[String, String]
    
    // patterns to fragments map - 
    // fragments collections are scala Lists as order and appearing-more-than-once matter
    val patterns2fragments   = new collection.mutable.HashMap[String, List[String]] 

    // build the data structures
    private val wildcards = List("..", "…")      // wildcard symbols allowed to the human who codes the CSV database
    private val wildchars = List('.', '…', ' ')  // characters indicating whether we are inside a wildcard sequence.. hence - "wildchars"

    // breaks down a wildcard-containing pattern into a list of its fragments 
    def breakDown(pattern: String): List[String] = {
      val indexes = wildcards map pattern.indexOf filter { i: Int => i > -1 } // discard non-founds
      if (indexes.isEmpty) {
        return(List(pattern))
      }
      else {
        val pos = indexes.min
        val (leftSidePlus1, rest) = pattern.splitAt(pos); val leftSide = leftSidePlus1.dropRight(1) // split and drop space
        val rightSide = rest.dropWhile((char) => wildchars.exists((wildchar) => char == wildchar))
        return List(leftSide) ::: breakDown(rightSide)
      }
    }

    case class Rule (pattern: String, fragments: List[String], indication: String) 
    val rules: List[Rule] = inputRules map (inputRule => new Rule(inputRule("pattern"), breakDown(inputRule("pattern")), inputRule("indication")))

    rules.foreach(rule => {
 
      Logger.write(s"${rule.fragments}","db-rule-fragments")

      rule.fragments.foreach(fragment => {
        fragments2patterns addBinding (fragment, rule.pattern)
        patterns2fragments += ((rule.pattern, rule.fragments))
      })

      patterns2indications += ((rule.pattern, rule.indication))
    })

    // bag of all fragments - 
    // uses a Set to avoid duplicate strings
    val allFragmentsDistinct : Set[String] = rules.map(rule => rule.fragments).flatten.toSet

    //println(fragments2patterns)
    //println(patterns2indications)
    Monitor.logUsage("after patterns representation building is")
    Logger.write(allFragmentsDistinct.mkString("\n"), "db-distinct-fragments")
  }

  //
  // initalizes an aho-corasick tree for searching all pattern fragments implied in the linguistic database
  // TODO: switch to use fragments map!
  object AhoCorasick {

    val trie = new Trie

    def init (fragments: Set[String]) {
      trie.onlyWholeWords()
      fragments foreach trie.addKeyword
    }

    //
    // invoke aho-corasick to find all fragments in given sentence
    //
    def go(sentence : String) : List[Map[String, String]] = 
    {
      val emitsJ = trie.parseText(sentence)
      
      if (emitsJ.size > 0) {
        val emits = (emitsJ.asScala map (i => Map("start" -> i.getStart.toString, "end" -> i.getEnd.toString, "match" -> i.getKeyword.toString))).toList
        Logger.write(sentence, "sentence-fragment-matches")
        Logger.write(emits.mkString("\n") + "\n", "sentence-fragment-matches")
        return(emits)
      }
      else return (List.empty[Map[String, String]])
    }
  }

  //
  // get mock data
  //
  println("loading sentences")
  val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.out"
  val sentences = Source.fromFile(SentencesInputFile).getLines
  
  // run rules per sentence    
  val inputRules = csv.getCSV
  val rep = new patternsRepresentation(inputRules)
  AhoCorasick.init(rep.allFragmentsDistinct)

  val sentenceMatchCount = scala.collection.mutable.ArrayBuffer.empty[Integer] 

  for (sentence <- sentences) {
    val matchedFragments = AhoCorasick.go(sentence)
    sentenceMatchCount += matchedFragments.length

    // checks if all fragments making up a pattern, are contained in target string *in order*
    def isInOrder(fragments: List[String], loc: Integer) : Boolean = {
      if (fragments.isEmpty) return true  // getting to the end of the list without returning false --> true
      else {
        val head = fragments.head
        
        val matches = matchedFragments.filter(_("match") == head) 
        if (matches.isEmpty) return false                         // has this pattern been matched for this sentence?
        if (matches.exists(_("start").toInt > loc))               // has it been matched in order?
          isInOrder(fragments.tail, (matches.map(_("end").toInt).min))
        else 
          false
      }
    }

    // for each matched fragment, trace back to the patterns to which it belongs,
    // then check if that pattern is matched in its entirety - i.e. if all its fragments match in order.
    matchedFragments.foreach(matched => { 
      val fragmentPatterns = rep.fragments2patterns.get(matched("match").toString).get
      fragmentPatterns.foreach(pattern => { 
        if (isInOrder (rep.patterns2fragments.get(pattern).get, -1)) {
          val indication = rep.patterns2indications.get(pattern).get
          Logger.write(s"sentence '$sentence' matches pattern '$pattern' -> which indicates '$indication'","sentence-matches")
        }
      })
    })
  }
  
  new Descriptive(sentenceMatchCount, "Fragments match count per sentence").all
}



      // re-integrate to caller function, after return value type issue has solved
      //val found = emits map (m => Map("match" -> m("match"), 
      //                                "indication" -> patterns.find(pattern => pattern == m("match")).getOrElse(("", List.empty, "none"))._3))

      //val matched = emits map (m => Map("match" -> m("match"), 
      //                                  "indication" -> patterns.find(pattern => pattern._2.exists(fragment => fragment == m("match"))).getOrElse("no information category assigned")._3))

      //println(matched.mkString("\n"))
      //println(found.mkString("\n"))
      //println()

/*
type matchSequence = (String, scala.collection.mutable.ListBuffer[String])

    object all {
      val fragments = scala.collection.mutable.ListBuffer.empty[String]
      val matchSequences = scala.collection.mutable.ListBuffer.empty[matchSequence]
    }
*/

