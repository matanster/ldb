package com.articlio.ldb

abstract class sectionTypeScheme {
  val translation: Map[String, String]
}

object eLifeSectionTypeScheme extends sectionTypeScheme {
  val translation = Map("introduction" -> "intro",
                                         "discussion" -> "discussion",
                                         "conclusion" -> "discussion")
}

object pdfConvertedSectionTypeScheme extends sectionTypeScheme {
  val translation = Map("introduction" -> "introduction",
                                          "conclusion" -> "conclusion")
}

