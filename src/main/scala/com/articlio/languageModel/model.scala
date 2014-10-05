package com.articlio.LanguageModel

//
// Model for linguistic meanings included in words.
// As there are many ontologies for linguistic properties entailed in words, 
// this model avoids a very hierarchical or otherwise restrictive approach.
//
// Wikipedia is a good source for the meanings of the properties
//

abstract class LinguisticProperty
case object Noun            extends LinguisticProperty
case object Determiner      extends LinguisticProperty
case object Demonstrative   extends LinguisticProperty
case object Possesive       extends LinguisticProperty
case object PersonalPronoun extends LinguisticProperty
case object SubjectForm     extends LinguisticProperty
case object ObjectForm      extends LinguisticProperty

abstract class MappingMeaningType
case object is extends MappingMeaningType

case class PropertyMapping (from: LinguisticProperty, mappingType: MappingMeaningType, to: LinguisticProperty)

//
// Class for curating all linguistic properties embedded in a word.
// This class is prescriptive - it prescribes the meanings meant in the word (rather than enumerating all possible meanings).
//
case class AnnotatedWord (word: String, meanings: Set[LinguisticProperty] = Set(Noun)) // defaults to Article property

/*
// scala's quircky structure for defining an enumeration..
object LinguisticProperty extends Enumeration { 
  type LinguisticProperty = Value

  // the enumeration values
  val Noun = Value
  val Determiner = Value
  val Demonstrative = Value
  val Possesive = Value
  val PersonalPronoun = Value
  val SubjectForm = Value
  val ObjectForm = Value

}; import LinguisticProperty._
*/