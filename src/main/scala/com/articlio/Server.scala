package com.articlio.ldb

import org.vertx.scala.core._
import org.vertx.scala.core.http.HttpServerRequest
import org.vertx.scala.core.http.HttpClientResponse
import org.vertx.scala.platform.Verticle
import org.vertx.scala.core.buffer.Buffer
import java.net.URLEncoder

import spray.json._
import DefaultJsonProtocol._

class deployer extends Verticle {
  override def start() {
    container.logger.info("starting deployer verticle")
    container.deployVerticle("scala:com.articlio.ldb.ldb")
    container.deployVerticle("scala:com.articlio.selfMonitor")
  }
}

class ldb extends Verticle {
  override def start() {

    container.logger.info("starting linguistic database verticle")
    
}