package com.articlio.util

object Text {

  // simply turns first letter to lowercase, assumes machine has English locale.
  // uses http://www.scala-lang.org/api/current/index.html#scala.Char.toLower
  // with some refactor, toLowerCase can be used instead if need to override machine locale
  def deSentenceCase = (text: String) => text.head.toLower + text.tail
  def splitToWords = (word: String) => word.split(" ")
  
  def wordFollowing(text: String, following: String) : Option[String] = {
    if (text.containsSlice(following)) Some(text.drop(text.indexOfSlice(following) + following.length).takeWhile(char => !(Seq(' ', '.') contains char)))
    else None
  }

  def wordFollowingAny(text: String, following: Seq[String]) : Option[Seq[String]] = {
    val found = following map { f => wordFollowing(text, f) } filter(_.isDefined) map {_.get}
    if (found.isEmpty) None 
    else Some(found)
  }
}
