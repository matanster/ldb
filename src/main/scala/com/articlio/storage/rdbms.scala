package com.articlio.storage

import scala.slick.driver.MySQLDriver.simple._
//import scala.slick.lifted._ // more clumsy to debug/use than "direct/simple"
import scala.slick.direct._   // http://slick.typesafe.com/doc/2.1.0/direct-embedding.html

object DB {
  val host = "localhost"
  val port = "3306"
  val database = "articlio"
  val user = "articlio"
  println("starting DB connection...")
  val db = Database.forURL(s"jdbc:mysql://$host:$port/$database", user, driver = "com.mysql.jdbc.Driver") withSession {
  implicit session =>
    println("DB Session started")

    class Something(tag: Tag) extends Table[(String, String)](tag, "Something") {
      def sentence = column[String]("SENTENCE")
      def matches  = column[String]("MATCHES")
      def * = (sentence, matches)
    }

    val something = TableQuery[Something]

    something.ddl.create
  }
}

/*
connection =
  host:     'localhost'
  user:     'articlio'
  database: 'articlio'
  charset:  'utf8'
*/

