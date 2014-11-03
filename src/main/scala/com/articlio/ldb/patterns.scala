package com.articlio.ldb

import com.articlio.input._
import com.articlio.util._
import com.articlio.util.text._
import com.articlio.LanguageModel._
import com.articlio.selfMonitor.{Monitor}
//import java.net.URLEncoder
//import spray.json._
//import DefaultJsonProtocol._
import org.ahocorasick.trie._
//import scala.collection.JavaConversions._ // work with Java collections as if they were Scala
import scala.collection.JavaConverters._    // convert Java colllections to Scala ones

abstract class PlugType
case object    RefAppendable  extends PlugType // self reference is potentially *appendable* to target phrase
case object    RefPrependable extends PlugType // self reference is potentially *prependable* to target phrase

abstract class PotentiallyPluggable (val plugType: PlugType)
//case class AA extends PotentiallyPluggable(RefPrependable)
case object    VerbFragment extends PotentiallyPluggable(RefPrependable)
case object    NounFragment extends PotentiallyPluggable(RefPrependable)
case object    InByOfFragment   extends PotentiallyPluggable(RefAppendable)

//
// class hierarchy for describing rules as derived by the LDB object
//

abstract class Rule
case class SimpleRule (pattern: String, fragments: List[String], indication: String, locationProperty: Option[Seq[Property]]) 
case class ExpandedRule (rule: SimpleRule) extends Rule {
  def getFragmentType : PotentiallyPluggable = {
                          if (rule.pattern.containsSlice("{asr-V}")) return VerbFragment 
                          if (rule.pattern.containsSlice("{asr-N}")) return NounFragment
                          return InByOfFragment
  }

  val fragmentType = getFragmentType
  override def toString = s"$rule, $fragmentType"
}


object go {

  //
  // Builds and exposes the data structures necessary for working the rules database
  // Refactor opportunity: use newBuilder and .result rather than hold mutable and immutable collections - for correct coding style without loss of performance!
  //
  class LDB(inputRules: Seq[RuleInput]) {

    //
    // expand base rules into more rules - quite not triggered from the database data right now
    //
    def expand(rules: Seq[SimpleRule]) : Seq[ExpandedRule] = {

      Timelog.timer("exapanding patterns containing article-self-references into all their combinations")  

      val ASRRules : Seq[ExpandedRule] = rules.filter(rule => rule.pattern.containsSlice("{asr")) map ExpandedRule
      println(ASRRules)

      for (rule <- ASRRules) { 
        rule.fragmentType match {
            case VerbFragment => for (ref <- ArticleSelfReference.refs if (ref.annotatedL1.isnt(Personal))) 
                                   println()
                                   //println(ref.annotatedL1.sequence + rule.rule.pattern)
            case NounFragment => for (ref <- ArticleSelfReference.refs if (ref.annotatedL1.is(PossesivePronoun)))  // must have props PossesivePronoun or possibly nounphrase + 's
                                   println()
                                   //println(ref.annotatedL1.sequence + rule.rule.pattern)
            case InByOfFragment => for (ref <- ArticleSelfReference.refs if (ref.annotatedL1.isAnyOf(Set(Personal, Possesive))))
                                   println(ref.annotatedL1.sequence + rule.rule.pattern)
                                 
          }
        }

      //

      //val expansion : Seq[String] = ASRRules.flatMap (rule => ArticleSelfReference.refsText.map
      //                                      (refText => rule.pattern.patch(rule.pattern.indexOfSlice("{asr}"), refText, "{asr}".length)))
      
      //println(ASRRules.mkString("\n"))
      //println(expansion.mkString("\n"))
      println(rules.length)
      println(ASRRules.length)
      //println(expansion.length)

      Timelog.timer("exapanding patterns containing article-self-references into all their combinations")  
      return ASRRules
    }

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

    Timelog.timer("patterns representation building")

    val rules: Seq[SimpleRule] = inputRules map (inputRule => new SimpleRule(inputRule.pattern, 
                                                                   breakDown(deSentenceCase(inputRule.pattern)), 
                                                                   inputRule.indication, 
                                                                   inputRule.properties.collect { case locationProp : LocationProperty => locationProp }))
                                                          
    // patterns to indications map - 
    // each pattern correlates to only one indictaion 
    val patterns2indications : Map[String, String] = rules.map(rule => (rule.pattern -> rule.indication)).toMap

    // patterns to fragments map - 
    // fragments collections are scala Lists as order and appearing-more-than-once matter
    val patterns2fragments : Map[String, List[String]] = rules.map(rule => (rule.pattern, rule.fragments)).toMap

    // fragments to patterns map 
    // (constructed through a mutable builder - to avoid the memory hogging of the alternative pure functional implementation)
    // the pattern collections here are ultimately scala Sets because order and appearing-more-than once are not necessary
    private val builder = new collection.mutable.HashMap[String, collection.mutable.Set[String]] 
                          with collection.mutable.MultiMap[String, String]                                  // this is a multimap builder, the way scala needs it
    rules.foreach(rule => rule.fragments.foreach(fragment => builder addBinding (fragment, rule.pattern)))  // build it
    val fragments2patterns : Map[String, Set[String]] = builder.map(kv => kv._1 -> kv._2.toSet).toMap       // extract to immutable

    //
    // map back from patterns to rules (needed as aho-corasick returns strings not rules that included them)
    //
    val patterns2rules : Map[String, SimpleRule] = rules.map(rule => rule.pattern -> rule).toMap

    // bag of all fragments - 
    // uses a Set to avoid duplicate strings
    val allFragmentsDistinct : Set[String] = rules.map(rule => rule.fragments).flatten.toSet

    Timelog.timer("patterns representation building")
    Monitor.logUsage("after patterns representation building is")
    Logger.write(allFragmentsDistinct.mkString("\n"), "db-distinct-fragments")
    Logger.write(patterns2fragments.mkString("\n"), "db-rule-fragments")

    expand(rules) // should do nothing for now

  }

  //
  // initalizes an aho-corasick tree for searching all pattern fragments implied in the linguistic database
  //
  object AhoCorasick {

    val trie = new Trie

    def init (fragments: Set[String]) {
      Timelog.timer("aho-corasick initialization (lazy operations not necessarily included)")
      trie.onlyWholeWords()
      fragments foreach trie.addKeyword
      Timelog.timer("aho-corasick initialization (lazy operations not necessarily included)")
    }

    //
    // invoke aho-corasick to find all fragments in given sentence
    //
    def go(sentence : String) : List[Map[String, String]] = 
    {
      val emitsJ = trie.parseText(deSentenceCase(sentence))

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
  // match rules per sentence    
  //
  val inputRules = CSV.deriveFromCSV
  val db = new LDB(inputRules)
  AhoCorasick.init(db.allFragmentsDistinct)

  //
  // get data
  //
  val sections : Seq[JATSsection] = new JATS("elife-articles(XML)/elife00425styled.xml").sections

  //
  // separate into util file
  //
  val SPACE = " "
  
  val specialCaseWords = Seq("vs.", "al.", "cf.", "st." ,"Fig.", "FIG.")

  implicit class MyStringOps(val s: String) {
    def endsWithAny(any: Seq[String]) : Boolean = {
      for (word <- any)
        if (s.endsWith(SPACE + word)) return true
      return false
    }
  }

  def sentenceSplit (text: String) : Seq[String] = {

    if (text.isEmpty) 
      return Seq.empty[String]

    for (i <- 2 to text.length) {

      val tentative = text.take(i) 

      if (tentative.endsWith(". "))      
        if (tentative.dropRight(1).endsWithAny(specialCaseWords) && text.isDefinedAt(i) && (text.charAt(i).isUpper))
          return Seq(tentative) ++ sentenceSplit(text.drop(i))

      if (tentative.endsWith(". "))      
          return Seq(tentative.dropRight(1)) ++ sentenceSplit(text.drop(i))

      if (tentative.endsWith("? ") || tentative.endsWith("! "))      
          return Seq(tentative.dropRight(1)) ++ sentenceSplit(text.drop(i))
    }

    return Seq(text) // getting to the end of the text without sentence delimitation having been detected, 
                     // this code flushes the entire text as one sentence.
                     // future intricacies may call to trigger a notice here.
  }

  case class AnnotatedSentence(text : AnnotatedText, section: String)
  
  case class LocatedText(text: String, section: String)

  def sentenceSplitter (toSplit: LocatedText) : Seq[LocatedText] = {

    Logger.write(toSplit.text, "JATS-paragraphs")
    val sentences = sentenceSplit(toSplit.text)
    Logger.write(sentences.mkString("\n"), "JATS-sentences")
    return sentences map (sentence => LocatedText(sentence, toSplit.section))
  }

  /*val sentences : Seq[AnnotatedSentence] = sections.flatMap(section => 
                                           section.paragraphs.flatMap(paragraph => sentenceTokenize(paragraph)
                                           .map(sentence => AnnotatedSentence(sentence, section.sectionTitle))))
*/

  // flat map all section -> paragraph -> sentences into one big pile of sentences. 
  val sentences: Seq[LocatedText] = sections.flatMap(section =>  section.paragraphs.flatMap(p =>
     sentenceSplitter(LocatedText(p.sentences.map(s => s.text).mkString(""),  section.sectionType)))) 
 
  //
  // process sentence by sentence
  //
  Timelog.timer("matching")
  processSentences(sentences)
  Timelog.timer("matching")

   
  
  //
  // matches rules per sentence    
  //
  def processSentences (sentences : Seq[LocatedText]) = {

    val sentenceMatchCount = scala.collection.mutable.ArrayBuffer.empty[Integer] 

    for (sentence <- sentences) {
      val matchedFragments = AhoCorasick.go(sentence.text)
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

      val possiblePatternMatches = Set.newBuilder[String] // a Set to avoid duplicates

      matchedFragments.foreach(matched => { 
        val fragmentPatterns = db.fragments2patterns.get(matched("match").toString).get
        possiblePatternMatches ++= fragmentPatterns
      })

      val possibleMatches = for (pattern <- possiblePatternMatches.result 
                              if (isInOrder (db.patterns2fragments.get(pattern).get, -1))) 
                                yield (pattern, sentence, db.patterns2indications.get(pattern).get, db.patterns2rules(pattern))

      possibleMatches.foreach(p =>
        Logger.write(Seq(s"sentence '${p._2.text}'",
                         s"in section ${p._2.section}",
                         s"matches pattern '${p._1}'",
                         s"which indicates '${p._3}'").mkString("\n") + "\n","sentence-pattern-matches"))

      //
      // trace back from pattern to rule
      //
      //
      // filter out matches occuring outside their designated location requirement
      //
      val matches = possibleMatches.filter(p => p._4 == p._4)

      /* superfluous to for-yield block now 
      possiblePatternMatches.result.foreach(pattern => { 
        if (isInOrder (db.patterns2fragments.get(pattern).get, -1)) {
          val indication = db.patterns2indications.get(pattern).get
          /*Logger.write(Seq(s"sentence '$sentence'",
                           s"matches pattern '$pattern'",
                           s"which indicates '$indication'").mkString("\n") + "\n","sentence-pattern-matches")*/
        }
      })*/


      //val LocationFiltered = possiblePatternMatches.result.filter(patternMatched => patternMatched.locationProperty.isDefined)

    }
    new Descriptive(sentenceMatchCount, "Fragments match count per sentence").all
  }
}

   
   
   
    /*case class AnnotatedSentence(sentence : String, section: String)
  def getAnnotatedSentences : String = {
    val sentences = com.articlio.Input.get
    //val annotatedSentences Seq[AnnotatedSentence] = 
    case class SectionDeterminer(sectionName: String) {
      var inside = false
      def isOpener (text: String, iterator: Iterator[String]) = if (text == s"<$sectionName>") {
                                                                  inside = true
                                                                  iterator.next // swallow this opener                                             
                                                                } 
      def isCloser (text: String, iterator: Iterator[String]) = if (text == s"</$sectionName>") {
                                                                  inside = false
                                                                  iterator.next // swallow this closer
                                                                } 
      def check    (text: String, iterator: Iterator[String]) = if (inside) isCloser(text, iterator)
                                                                else isOpener(text, iterator) 
    }
    
    val SectionDeterminers : Set[SectionDeterminer] = Set("introduction", "conclusion", "discussion") map SectionDeterminer

    Timelog.timer("marking input doc sections")

    while (sentences.hasNext)
    { 
      val sentence = sentences.next

      SectionDeterminers map (s => s.check(sentence, sentences))
      SectionDeterminers.foreach(s => s.inside match {
        case true  => // println(s.sectionName + "\n" + sentence)
        case false =>        
      })
    } 
    Timelog.timer("marking input doc sections")
    return sentences.
  }*/   


