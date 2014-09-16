package com.articlio.reproduce
import org.ahocorasick.trie._

import scala.collection.JavaConverters._    // convert Java colllections to Scala ones

object wierd {

  val trie = new Trie

  def trieInit(patterns: List[String]) {
    trie.onlyWholeWords();
    for (pattern <- patterns)
      trie.addKeyword(pattern)
  }

  def patternTest(sentence : String) : List[String] = 
  {
    val emitsJ = trie.parseText(sentence)
    val emits = emitsJ.asScala map (i => i.getKeyword) toList

    println(s"converted from ${emitsJ.getClass} to ${emits.getClass}")

    return(emits)
    //return (List.empty[String])
  }

  trieInit(List("hello"))
  println(patternTest("hello"))
}

