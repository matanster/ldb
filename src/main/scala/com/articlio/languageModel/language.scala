package com.articlio.LanguageModel

//
// Model for linguistic meanings included in words.
// As there are many ontologies for linguistic properties entailed in words, 
// this model avoids a very hierarchical or otherwise restrictive approach.
//
// Wikipedia is a good source for the meanings of the properties
//

// Linguistic properties. 
abstract class LinguisticProperty
abstract trait phrase
case object Noun            extends LinguisticProperty
case object Personal        extends LinguisticProperty 
case object Possesive       extends LinguisticProperty
case object SubjectForm     extends LinguisticProperty
case object ObjectForm      extends LinguisticProperty
case object NounPhrase      extends LinguisticProperty with phrase

case object Determiner      extends LinguisticProperty
case object Demonstrative   extends LinguisticProperty
case object PersonalPronoun extends LinguisticProperty

abstract class MappingMeaningType
case object isNecessarily extends MappingMeaningType

//
// Class for defining mappings between properties and properties they imply
//
case class PropertyMapping (from: LinguisticProperty, mappingType: MappingMeaningType, to: LinguisticProperty)

object Mappings {
  val mappings : PropertyMapping = PropertyMapping(Noun, isNecessarily, NounPhrase)
}

//
// Classes for curating all linguistic properties embedded in a word, sequence of words, or any text.
// These are meant to be prescriptive - to prescribe the meanings meant in the word (not to enumerate all possible meanings).
//
abstract class Annotated (val text: String, properties: Set[LinguisticProperty] = Set(Noun)) // defaults to Noun property
case class AnnotatedL1   (sequence: String, properties: Set[LinguisticProperty] = Set(Noun)) extends Annotated (sequence, properties)
case class AnnotatedL2   (annotatedL1: AnnotatedL1, property: DomainProperty) 





















/* Old enumeration code, prior to using case objects.
=====================================================
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