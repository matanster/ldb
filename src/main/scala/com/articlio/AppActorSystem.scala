package com.articlio
import akka.actor.ActorSystem
import akka.actor.Props
import com.articlio.util.Timelog

object AppActorSystem {
  val system = ActorSystem("app-actor-system")
  val timelog = AppActorSystem.system.actorOf(Props[Timelog], 
                                              name = "timer-logging-service")
}