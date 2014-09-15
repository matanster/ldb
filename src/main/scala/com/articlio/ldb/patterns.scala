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

object go {

  //
  // get mock data
  //
  println("loading sentences")
  val SentencesInputFile = "/home/matan/ingi/repos/back-end-js/docData/w4aXHuIDR8KGrQ688XEi/sentences*ubuntu-2014-08-25T12:30:16.035Z.out"
  val sentences = Source.fromFile(SentencesInputFile).getLines
  println(sentences.getClass)
    
  //
  // Search all strings within one sentence
  //

  val trie = new Trie

  def trieInit(patterns: List[(String, List[String])]) {
    trie.onlyWholeWords();
    for (pattern <- patterns)
      trie.addKeyword(pattern._1)
  }

  def patternTest(sentence:String) {
    val emitsJ = trie.parseText(sentence)
    //println(s"java type: ${emitsJ.getClass}")
    
    if (emitsJ.size > 0){
      val emits = emitsJ.asScala map (i => Map("start" -> i.getStart, "end" -> i.getEnd, "match" -> i.getKeyword))
      //println(emitsJ.getClass)
      //println(emits.getClass)
      println(emitsJ.size)
      println(emits)
      println()
    }
    //println(s"scala converted type: ${emits.getClass}")
    //println(s"scala converted value: $emits")
  }

  def contriveSearchStrings(rawInputs: List[Map[String, String]]): List[(String, List[String])] =
  {
    val wildcards = List("..", "…") // wildcard symbols allowed to the human who codes the CSV database
    val wildchars = List('.', '…', ' ')  // characters indication whether we are inside a wildcard sequence.. hence - "wildchars"

    
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
    
    val patterns = scala.collection.mutable.ListBuffer.empty[(String, List[String])]
    rawInputs.foreach(rawInput => {
      val raw = rawInput("pattern")
      val fragments = breakDown(raw)
      val pattern = (raw, fragments)

      patterns += pattern

      //println(s"${fragments.length} $pattern")

    })

    return patterns.toList

  }
  
  val rawInput = csv.getCSV
  val patterns = contriveSearchStrings(rawInput)
  trieInit(patterns)
  for (sentence <- sentences) {
    //println(sentence)
    patternTest(sentence)
  }
}


/*
type matchSequence = (String, scala.collection.mutable.ListBuffer[String])

    object all {
      val fragments = scala.collection.mutable.ListBuffer.empty[String]
      val matchSequences = scala.collection.mutable.ListBuffer.empty[matchSequence]
    }
*/