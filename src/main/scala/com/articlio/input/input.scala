package com.articlio.input
import scala.io.Source
import scala.xml.Utility.{trim}

object Input {

  def get : Iterator[String] = {
    println("loading sentences...")
    val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.xml"
    val sentences = Source.fromFile(SentencesInputFile).getLines
    return sentences
  }
}

class AnnotatedText extends scala.xml.NodeSeq

case class JATSsection (sectionJATStype: String, sectionTitle: String, paragraphs: AnnotatedText)

object JATSsections {
  // to follow this XML api, see the tests at https://github.com/scala/scala-xml/blob/master/src/test/scala/scala/xml/XMLTest.scala
  def load(filePath: String) : Seq[JATSsection] = {
    println("loading JATS input...")
    val JATSinput = scala.xml.XML.loadFile(filePath)

    val sections = for (node <- JATSinput \ "body" \ "sec") 
                     yield JATSsection(node.attributes("sec-type").toString, 
                                      (node \ "title").head.child.head.toString, 
                                      (node \ "p").asInstanceOf[AnnotatedText])
    return sections
  }
}


//println(sections.head.paragraphs.mkString("\n"))

//case class LocatedSentence(sentence : String, section: String) // not in use for now