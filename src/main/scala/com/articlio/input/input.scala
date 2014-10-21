package com.articlio
import scala.io.Source
import scala.xml.Utility.{trim}

object Input {

  def get : Iterator[String] = {
    println("loading sentences...")
    val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.xml"
    val sentences = Source.fromFile(SentencesInputFile).getLines
    return sentences
  }

  case class LocatedSentence(sentence : String, section: String)

  def getXML : Iterator[String] = {
    println("loading JATS input...")
    //val JATSinput = scala.xml.XML.loadFile("mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.xml")
    val JATSinput = scala.xml.XML.loadFile("elife-articles(XML)/elife00425styled.xml")
    //println(JATSinput)

    case class JATSsection (JATStype: String, title: String, paragraphs: scala.xml.NodeSeq  )
    // to follow this XML api, see the tests as https://github.com/scala/scala-xml/blob/master/src/test/scala/scala/xml/XMLTest.scala
    val sections = for (node <- JATSinput \ "body" \ "sec") 
                     yield JATSsection(node.attributes("sec-type").toString, 
                                      (node \ "title").head.child.head.toString, 
                                       node \ "p")

    println(sections.head)
    println
  
    val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.xml"
    val sentences = Source.fromFile(SentencesInputFile).getLines
    return sentences
  }

}
