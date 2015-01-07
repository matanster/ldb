import com.articlio.config

import com.articlio.HttpService
import com.articlio.ldb
import com.articlio.selfMonitor.{Monitor}
import com.articlio.util.{Console}
import com.articlio.storage
import akka.actor.ActorSystem
import com.articlio.AppActorSystem
import com.articlio.analyze._

object Boot extends App {

  Monitor

  val i = Indels
  
  AppActorSystem.outDB ! "createIfNeeded"
  
  //ldb.ldb.init

  //com.articlio.storage.createCSV.go()
  com.articlio.storage.createAnalyticSummary.go()
  
  val httpService = HttpService
                                              
  // Ultimately something like http://stackoverflow.com/questions/24731242/spray-can-webservice-graceful-shutdown
  // should be used for the following lines of code, as much as skipping them has no ecosystemic side effects:
  // Monitor.shutdown
  // storage.OutDB.close
}

object Analytic {
  val inDb = storage.InDB.Query("ubuntu-2014-11-21T12:06:51.286Z")
  storage.InDB.close
}