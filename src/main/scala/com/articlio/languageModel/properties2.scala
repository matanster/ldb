package com.articlio.ldb
import com.articlio.util.text._

abstract class PhraseProperty
abstract class EmbeddingProjection

abstract class PluralityType extends PhraseProperty
case object    Singular      extends PluralityType
case object    Plural        extends PluralityType

abstract class EntityType        extends PhraseProperty
case object    WorkEntityType     extends EntityType
abstract class PersonaEntityType extends EntityType
case object    PersonaEntityType extends PersonaEntityType
case object    PersonalPronoun   extends PersonaEntityType with PersonalPronounForm

trait       PersonalPronounForm
case object Subject extends PersonalPronounForm
case object Object  extends PersonalPronounForm

case class ProjectedForm(form: String) extends EmbeddingProjection

class  Modulator (val modulator: String => String = identity[String]) extends EmbeddingProjection // defaults to identity function
object Modulator { def apply(modulator: String => String) = new Modulator(modulator) }            // class constructor

case object DefaultPresentTenseModulator extends Modulator
case object DefaultPossesiveModulator extends Modulator

case class NounPhrase (
  baseForm                  : String,
  pluralityType             : PluralityType, 
  entityType                : EntityType, 
  possesiveForm             : Modulator = DefaultPossesiveModulator,
  presentTenseVerbModulator : Modulator = DefaultPresentTenseModulator
)

object ArticleSelfReferences {

  //
  // article-self-reference triggers (noun phrases all)
  //
  object ArticleDirectSelfReference {

    // determiners and their determiner type (as per ontology at http://en.wikipedia.org/wiki/Determiner_phrase#The_competing_analyses)
    private val reference = Set( 
      "the present", 
      "this",        
      "our",       
      "my",        
      "the current"
    )

    // nouns
    private val referencedAs = Set(
      "study",
      "article",
      "section",
      "chapter",
      "unit",
      "paper",
      "research",
      "work",
      "essay",
      "note",
      "report",
      "experiment",
      "series of experiments",
      "project",
      "subsection",
      "review",
      "draft",
      "project"
    )

    //val references : Set[String] = for (firstHalf <- reference; secondHalf <- referencedAs) yield (firstHalf.text + SPACE + secondHalf.text)
    val references : Set[NounPhrase] = for (firstHalf <- reference; secondHalf <- referencedAs) 
                                            yield NounPhrase(firstHalf + SPACE + secondHalf, Singular, WorkEntityType)
  }

  //
  // author-self-reference triggers
  //
  object ArticleAuthorSelfReference {

    // - to be categorized/tagged by their different properties such as inclusion/disclusion of a possesive(genetive) property.
    val references = Set(
      NounPhrase("I", Singular, PersonaEntityType, possesiveForm = Modulator(_ => "My")),
      NounPhrase("we", Plural, PersonaEntityType, possesiveForm = Modulator(_ => "Our")),
      NounPhrase("the author",  Singular, PersonaEntityType),
      NounPhrase("the authors", Plural, PersonaEntityType)
    )
  }

 
}