package com.articlio
import scala.io.Source

object Input {
  def get : Seq[String] = {
    println("loading sentences...")
    val SentencesInputFile = "mock-data/sentences*ubuntu-2014-08-25T12:30:16.035Z.out"
    val sentences = Source.fromFile(SentencesInputFile).getLines.toSeq
    return sentences
  }
}
