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

case class JATSsectionRaw(sectionJATStype: String, sectionTitle: String, paragraphs: NodeSeq)

case class Annotation (annotation: String)
case class AnnotatedText (text: String, annotations: Seq[Annotation])
case class Paragraph(sentences: Seq[AnnotatedText])
case class JATSsection(sectionType: String, paragraphs: Seq[Paragraph])

object JATSloader{
  // to follow this XML api, see:
  //    the tests at https://github.com/scala/scala-xml/blob/master/src/test/scala/scala/xml/XMLTest.scala
  //    http://alvinalexander.com/scala/how-to-extract-data-from-xml-nodes-in-scala
  //    the scaladoc
  def load(filePath: String): Seq[JATSsectionRaw] = {
    println("loading JATS input...")
    val JATSinput = scala.xml.XML.loadFile(filePath)

    val sections = for (node <- JATSinput \ "body" \ "sec")
      yield JATSsectionRaw(node.attributes("sec-type").toString,
      (node \ "title").head.child.head.toString,
      node \ "p")
    return sections
  }
}

//
// constructor argument: top XML node of an XML node hierarchy, 
// derived member:            a tree mirror of it, made of plain self-defined object nodes
//
class JATS (filePath: String) {
  private val JATSsectionsRaw = JATSloader.load(filePath) // "elife-articles(XML)/elife00425styled.xml"
  private val annotation = Annotation("stripped-text")

  def builder (section: JATSsectionRaw) : JATSsection = {
    
    val paragraphs= Seq.newBuilder[Paragraph]

    def buildParagraph(xmlNode: Node) : Paragraph = {
      
      val sentences = Seq.newBuilder[AnnotatedText]
      
      // a recursive cloner. can add filtering logic here later.
      def build(xmlNode: Node) {
        if (xmlNode.child.isEmpty) {
          sentences += AnnotatedText(xmlNode.text, Seq(annotation))
          println(xmlNode.text)
        }
        xmlNode.child foreach build
      }
      
      build(xmlNode)
      return Paragraph(sentences.result)
    }
    
    if (!section.paragraphs.isEmpty) paragraphs ++= section.paragraphs map buildParagraph
    
    return JATSsection(section.sectionJATStype, paragraphs.result)
  }
  
  val sections = JATSsectionsRaw map builder
}
