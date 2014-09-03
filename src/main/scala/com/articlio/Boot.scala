package com.articlio

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import util.Properties

object Boot extends App {
  
  // we need an ActorSystem to host our application in
 
  //IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = sys.env.get("PORT").map(_.toInt).getOrElse(8091))
}
