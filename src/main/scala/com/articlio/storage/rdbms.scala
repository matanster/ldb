package com.articlio.storage

import scala.slick.driver.MySQLDriver.simple._
//import scala.slick.lifted._ // more clumsy to debug/use than "direct/simple"
import scala.slick.direct._   // http://slick.typesafe.com/doc/2.1.0/direct-embedding.html

object DB {
  val host = "localhost"
  val port = "3306"
  println("starting DB connection...")
  val db = Database.forURL(s"jdbc:mysql://$host:$port", driver = "com.mysql.jdbc.Driver") withSession {
  implicit session =>
    println("DB Session started")
  }
}

/*
connection =
  host:     'localhost'
  user:     'articlio'
  database: 'articlio'
  charset:  'utf8'
*/

