package com.articlio.input
import scala.io.Source
import scala.xml.Utility.{ trim }
import scala.xml.{ Node, NodeSeq }

object Input {

  def get: Iterator[String] = {
    println("loading sentences...")
    val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.xml"
    val sentences = Source.fromFile(SentencesInputFile).getLines
    return sentences
  }
}

//class AnnotatedText extends scala.xml.NodeSeq


case class JATSsection(sectionJATStype: String, sectionTitle: String, paragraph: NodeSeq)

object JATSloader{
  // to follow this XML api, see the tests at https://github.com/scala/scala-xml/blob/master/src/test/scala/scala/xml/XMLTest.scala
  def load(filePath: String): Seq[JATSsection] = {
    println("loading JATS input...")
    val JATSinput = scala.xml.XML.loadFile(filePath)

    val sections = for (node <- JATSinput \ "body" \ "sec")
      yield JATSsection(node.attributes("sec-type").toString,
      (node \ "title").head.child.head.toString,
      node \ "p")
    return sections
  }
}

case class Annotation (annotation: String)
case class AnnotatedText (text: String, annotations: Seq[Annotation])

//
// IN:        top XML node of an XML node hierarchy, 
// OUT:    a tree mirror of it, made of plain self-defined object nodes
//
class JATS (filePath: String) {
  private val JATSsections= JATSloader.load(filePath) // "elife-articles(XML)/elife00425styled.xml"
  private val sentences = Seq.newBuilder[AnnotatedText]
  private val annotation = Annotation("stripped-text")
  
  // a recursive cloner. can add filtering logic here later.
  def build(xmlNode: Node) {
    println(xmlNode.label)
    sentences += AnnotatedText(xmlNode.text, Seq(annotation))
    xmlNode.child foreach build
  }

  build(JATSsections.head.paragraph.head)
  val sentenceObjects = sentences.result
  println(sentenceObjects)
}

//println(sections.head.paragraphs.mkString("\n"))

//case class LocatedSentence(sentence : String, section: String) // not in use for now