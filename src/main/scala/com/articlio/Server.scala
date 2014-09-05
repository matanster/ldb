package com.articlio.ldb

import org.vertx.scala.core._
import org.vertx.scala.core.http.HttpServerRequest
import org.vertx.scala.core.http.HttpClientResponse
import org.vertx.scala.platform.Verticle
import org.vertx.scala.core.buffer.Buffer
import scala.io.Source
//import java.net.URLEncoder
//import spray.json._
//import DefaultJsonProtocol._
import org.ahocorasick.trie._
import scala.collection.JavaConversions._

class deployer extends Verticle {
  override def start() {
    container.logger.info("starting deployer verticle")
    container.deployVerticle("scala:com.articlio.ldb.ldb")
    container.deployVerticle("scala:com.articlio.selfMonitor")
  }
}

class ldb extends Verticle {
  override def start() {

    val trie = new Trie
    trie.onlyWholeWords();
    trie.addKeyword("sugar")
    val emits = trie.parseText("sugar cane sugarcane sugar canesugar")
    emits.foreach(println)


    //container.logger.info("starting linguistic database verticle")
    System.out.println("starting linguistic database verticle")
    val databaseInputFile = "/home/matan/Downloads/database-machine-ready July 24 2014 non-abstract database - Markers.csv"
    for (row <- Source.fromFile(databaseInputFile).getLines()) {
      val array = row.split(",")
    }
  }
}