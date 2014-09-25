package com.articlio.storage

import scala.slick.driver.MySQLDriver.simple._
//import scala.slick.lifted._ // more clumsy to debug/use than "direct/simple"
import scala.slick.direct._   // http://slick.typesafe.com/doc/2.1.0/direct-embedding.html
import language.experimental.macros

object DB {
  val host     = "localhost"
  val port     = "3306"
  val database = "articlio"
  val user     = "articlio"
  println("starting DB connection...")
  val db = Database.forURL(s"jdbc:mysql://$host:$port/$database", user, driver = "com.mysql.jdbc.Driver")
  implicit val session: Session = db.createSession

  class Matches(tag: Tag) extends Table[(String, String, String)](tag, "Matches") {
    def sentence        = column[String]("sentence")
    def matchPattern    = column[String]("matchPattern")
    def matchIndication = column[String]("matchIndication")
    def * = (sentence, matchPattern, matchIndication)
  }

  val matches = TableQuery[Matches]

  def dropCreate {
    println("DB Session started")

    try {
      matches.ddl.drop 
      println("existing table dropped")
    } catch { case e: Exception => } // exception type not documented
    println("creating table")
    matches.ddl.create
  }

  def write(message:String, msgType:String) = {
    matches += ("something", "matches something", "indicates something")   
    matches ++= Seq(("something new", "matches something new", "indicates something"),
                    ("something new", "matches something new", "indicates something"))
  }

  def close = session.close
}
