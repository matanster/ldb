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

object go {

  //
  // gets mock data
  //
  println("loading sentences")
  //val SentencesInputFile = "/home/matan/ingi/repos/back-end-js/docData/w4aXHuIDR8KGrQ688XEi/sentences*ubuntu-2014-08-25T12:30:16.035Z.out"
  val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.out"
  val sentences = Source.fromFile(SentencesInputFile).getLines
    
  //
  // initalizes an aho-corasick tree for searching all pattern fragments implied in the linguistic database
  //
  val trie = new Trie
  def trieInit(patterns: List[(String, List[String], String)]) {
    trie.onlyWholeWords();
    for (pattern <- patterns)
      pattern._2 map trie.addKeyword
    util.Logger.write(patterns flatMap (pattern => pattern._2) mkString("\n"), "fragments")
  }


  //
  // breaks down wildcard-containing patterns into fragments,
  // creating a record mapping each fragment to its fragments
  //
  def fragmentizePatterns(basicPatterns: List[Map[String, String]]): List[(String, List[String], String)] =
  {
    val wildcards = List("..", "…")      // wildcard symbols allowed to the human who codes the CSV database
    val wildchars = List('.', '…', ' ')  // characters indicating whether we are inside a wildcard sequence.. hence - "wildchars"

    def breakDown(pattern: String): List[String] = {

      val indexes = wildcards map pattern.indexOf filter { i: Int => i > -1 } // discard non-founds
      if (indexes.isEmpty) {
        util.Logger.write(pattern, "patterns")
        return(List(pattern))
      }
      else {
        util.Logger.write(s"composite pattern for: $pattern:", "patterns")
        val pos = indexes.min
        val (leftSide, rest) = pattern.splitAt(pos)
        val rightSide = rest.dropWhile((char) => wildchars.exists((wildchar) => char == wildchar))
        util.Logger.write(leftSide, "patterns")
        return List(leftSide) ::: breakDown(rightSide)
      }
    }
    
    val patterns = scala.collection.mutable.ListBuffer.empty[(String, List[String], String)]
    basicPatterns.foreach(basicPattern => {
      val pattern = (basicPattern("pattern"), breakDown(basicPattern("pattern")), basicPattern("indication"))
      patterns += pattern
      //println(s"${fragments.length} $pattern")
    })

    return patterns.toList
  }
  
  //
  // invoke aho-corasick to find all fragments in given sentence
  //
  def fragmentMatch(sentence : String, patterns: List[(String, List[String], String)]) : List[Map[String, Any]] = 
  {
    val emitsJ = trie.parseText(sentence)
    
    if (emitsJ.size > 0) {
      val emits = (emitsJ.asScala map (i => Map("start" -> i.getStart, "end" -> i.getEnd, "match" -> i.getKeyword))).toList
      //println(sentence)
      //println(emitsJ.size)
      //println(emits.mkString("\n"))
      util.Logger.write(sentence, "matches")
      util.Logger.write(emits.mkString("\n") + "\n", "matches")
      return(emits)
    }
    else return (List.empty[Map[String, Any]])
  }
  
  val rules = csv.getCSV
  val fragmentizedPatterns = fragmentizePatterns(rules)
  trieInit(fragmentizedPatterns)
  for (sentence <- sentences) {
    val foundFragments = fragmentMatch(sentence, fragmentizedPatterns)
  }
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

