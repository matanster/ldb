package com.articlio.util

object Text {

  // simply turns first letter to lowercase, assumes machine has English locale.
  // uses http://www.scala-lang.org/api/current/index.html#scala.Char.toLower
  // with some refactor, toLowerCase can be used instead if need to override machine locale
  def deSentenceCase = (text: String) => text.head.toLower + text.tail

}
