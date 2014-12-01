package com.articlio.ldb

//
// This is a bit ugly, a function that checks a map and separately handles the cases not covered in the map,
// might make better sense.
//

abstract class sectionTypeScheme {
  val translation: Map[String, String]
}

object eLifeSectionTypeScheme extends sectionTypeScheme {
  val translation = Map("introduction" -> "intro",
                        "discussion" -> "discussion",
                        "conclusion" -> "discussion",
                        "abstract" -> "abstract")
}

object pdfConvertedSectionTypeScheme extends sectionTypeScheme {
  val translation = Map("introduction" -> "introduction",
                        "conclusion" -> "conclusion",
                        "abstract" -> "abstract")
}

