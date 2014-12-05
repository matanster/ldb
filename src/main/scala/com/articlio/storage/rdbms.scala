package com.articlio.storage

//import language.experimental.macros
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta._
//import scala.slick.lifted._ // more clumsy to debug/use than "direct/simple"
//import scala.slick.direct._ // http://slick.typesafe.com/doc/2.1.0/direct-embedding.html

// direct SQL access imports
//import scala.slick.driver.JdbcDriver.backend.Database
//import Database.dynamicSession
//import scala.slick.jdbc.{GetResult, StaticQuery}

object OutDB {

  // connection parameters
  val host     = "localhost"
  val port     = "3306"
  val database = "articlio"
  val user     = "articlio"
  
  // acquire single database connection used as implicit throughout this object
  println("starting output DB connection...")
  val db = Database.forURL(s"jdbc:mysql://$host:$port/$database", user, driver = "com.mysql.jdbc.Driver")
  implicit val session: Session = db.createSession

  type Match = (String, String, String, String, String, String, Boolean, String)

  // slick class binding definition
  class Matches(tag: Tag) extends Table[Match](tag, "Matches") {
    def docName = column[String]("docName")
    def runID = column[String]("runID")
    def sentence = column[String]("sentence", O.Length(20000,varying=true))
    def matchPattern = column[String]("matchPattern")
    def locationActual = column[String]("locationActual")
    def locationTest = column[String]("locationTest")    
    def isFinalMatch = column[Boolean]("fullMatch")
    def matchIndication = column[String]("matchIndication")
    def * = (runID, docName, sentence, matchPattern, locationTest, locationActual, isFinalMatch, matchIndication)
  }

  // the table handle
  val matches = TableQuery[Matches]

  // Table write functions
  def ++= (data: Seq[Match]) = matches ++= data
  def ++= (data: Seq[String]) = println("stringgggggggggggggg")

  def += (data: Match) = matches += data

  def createIfNeeded {
    if (MTable.getTables("Matches").list.isEmpty) 
      matches.ddl.create
  }
  
  def dropCreate {
    try {
      matches.ddl.drop 
      println("existing table dropped")
    } catch { case e: Exception => } // exception type not documented
    println("creating table")
    matches.ddl.create
  }

  def close = session.close
  
  //matches += ("something", "matches something", "indicates something")   
  //matches ++= Seq(("something new", "matches something new", "indicates something"),
  //                ("something new", "matches something new", "indicates something"))

}
