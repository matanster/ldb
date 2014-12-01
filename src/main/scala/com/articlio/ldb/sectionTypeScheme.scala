package com.articlio.ldb

//
// This is rather very ugly, a function that checks a map and separately handles the cases not covered in the map,
// may make better sense. This is entirely convoluted as is.
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

