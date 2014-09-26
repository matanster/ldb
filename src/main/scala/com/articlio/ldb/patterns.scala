package com.articlio.ldb

import com.articlio.util
import com.articlio.selfMonitor
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
  object patternsRepresentation {

    val rules                = scala.collection.mutable.ListBuffer.empty[(String, List[String], String)]
    val patterns2indications = scala.collection.mutable.HashMap.empty[String, String]
    val fragments2patterns   = new collection.mutable.HashMap[String, collection.mutable.Set[String]] with collection.mutable.MultiMap[String, String]

    // build the data structures
    def build(inputRules: List[Map[String, String]])
    {
      val wildcards = List("..", "…")      // wildcard symbols allowed to the human who codes the CSV database
      val wildchars = List('.', '…', ' ')  // characters indicating whether we are inside a wildcard sequence.. hence - "wildchars"


      // breaks down a wildcard-containing pattern into a list of its fragments 
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

      inputRules.foreach(inputRule => {
   
        val fragments = breakDown(inputRule("pattern"))

        val rule = (inputRule("pattern"), fragments, inputRule("indication"))
        rules += rule

        fragments.foreach(fragment => fragments2patterns addBinding (fragment, inputRule("pattern")))
        patterns2indications += ((inputRule("pattern"), inputRule("indication")))
      })

      //println(fragments2patterns)
      println(patterns2indications)
    }

  }

  //
  // initalizes an aho-corasick tree for searching all pattern fragments implied in the linguistic database
  // TODO: switch to use fragments map!
  object AhoCorasick {

    val trie = new Trie

    def init {
      trie.onlyWholeWords();
      for (rule <- patternsRepresentation.rules)
        rule._2 map trie.addKeyword
      util.Logger.write(patternsRepresentation.rules flatMap (rule => rule._2) mkString("\n"), "fragments")
    }

    //
    // invoke aho-corasick to find all fragments in given sentence
    //
    def go(sentence : String) : List[Map[String, Any]] = 
    {
      val emitsJ = trie.parseText(sentence)
      
      if (emitsJ.size > 0) {
        val emits = (emitsJ.asScala map (i => Map("start" -> i.getStart, "end" -> i.getEnd, "match" -> i.getKeyword))).toList
        //println(sentence)
        //println(emitsJ.size)
        //println(emits.mkString("\n"))
        util.Logger.write(sentence, "raw-matches")
        util.Logger.write(emits.mkString("\n") + "\n", "raw-matches")
        return(emits)
      }
      else return (List.empty[Map[String, Any]])
    }
  }

  //
  // gets mock data
  //
  println("loading sentences")
  val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.out"
  val sentences = Source.fromFile(SentencesInputFile).getLines
  
  // run rules per sentence    
  val inputRules = csv.getCSV
  patternsRepresentation.build(inputRules)
  AhoCorasick.init

  //
  // Output basic descriptive statistics for a sequence of numbers
  // TODO: move to util/visualization
  class Descriptive(vals: Seq[java.lang.Number], title:String) {
    import org.apache.commons.math3.stat.descriptive.{DescriptiveStatistics} // for using descriptive statistics over collections    
    var descriptive = new DescriptiveStatistics                              // NOT thread safe (http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/stat/descriptive/DescriptiveStatistics.html#addValue(double))
    vals.foreach(value => descriptive.addValue(value.doubleValue))
    
    // Some descriptive statistics from http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/stat/descriptive/DescriptiveStatistics.html
    val average  = descriptive.getMean
    val std      = descriptive.getStandardDeviation
    val variance = descriptive.getVariance
    val zerosP   = (vals.count(_ == 0).doubleValue / vals.length.doubleValue)

    def allApacheCommons: String = descriptive.toString 

    def basic = {
      val printables = Seq(("average", average),
                           ("variance", variance),
                           ("std", std),
                           ("% zero", zerosP))
      val maxLen = (printables map (p => p._1.length)).max
      printables.foreach(p => println(p._1 + ':' + (" " * (1 + maxLen - p._1.length)) + p._2.toString)) // padded printout of each statistic
    }

    def input = {
      println(vals.mkString(" "))
    }

    def all = { 
      println()
      println(title)
      input 
      basic
      println()
    }

  }

  val sentenceMatchCount = scala.collection.mutable.ArrayBuffer.empty[Integer] 

  for (sentence <- sentences) {
    val matchedFragments = AhoCorasick.go(sentence)
    sentenceMatchCount += matchedFragments.length

    // for each matched fragment, trace back to the patterns to which it belongs,
    // then check if that pattern is matched in its entirety
    matchedFragments.foreach(matched => { 
      val fragmentPatterns = patternsRepresentation.fragments2patterns.get(matched("match").toString).get
      //fragments2patterns.foreach(pattern => {
      
      //})
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

