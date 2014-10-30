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

  case class LocatedSentence(sentence : String, section: String) // not in use for now
  case class JATSsection (sectionJATStype: String, sectionTitle: String, paragraphs: Seq[String])

  def getXML : Seq[JATSsection] = {
    println("loading JATS input...")
    //val JATSinput = scala.xml.XML.loadFile("mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.xml")
    val JATSinput = scala.xml.XML.loadFile("elife-articles(XML)/elife00425styled.xml")
    //println(JATSinput)

    // to follow this XML api, see the tests at https://github.com/scala/scala-xml/blob/master/src/test/scala/scala/xml/XMLTest.scala
    val sections = for (node <- JATSinput \ "body" \ "sec") 
                     yield JATSsection(node.attributes("sec-type").toString, 
                                      (node \ "title").head.child.head.toString, 
                                      (node \ "p") map (node => node.toString)) // forgoing the nuance and noise of annotation
                                                                                           // within each paragraph (e.g. ref annotations),
                                                                                           // for now

    //println(sections.head.paragraphs.mkString("\n"))
    return sections
  }

}
