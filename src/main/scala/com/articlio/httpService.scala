package com.articlio
import com.articlio.input.JATS

//
// Spray imports
//
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import scala.concurrent.duration._

class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val myRoute =
    get {
	  path("") { 
		complete {
          <html>
            <body>
              <h1>service is up</h1>
            </body>
          </html>
        }
      } ~
      path("semantic") {
        parameter('inputFile) { inputFile =>
      	  complete(ldb.ldb.go(new JATS(s"${config.pdf}/$inputFile", "pdf-converted")))
        } ~
        parameter('eLifeInputFile) { eLifeInputFile =>
      	  complete(ldb.ldb.go(new JATS(s"${config.eLife}/$eLifeInputFile")))
        } ~       
        parameter('all) { all =>
          Bulk.all
          complete("Done processing all files... but you probably timed out by now")
        } ~
        parameter('allp) { all =>
          Bulk.allPDF
          complete("Done processing all pdf files... but you probably timed out by now")
        } ~
        parameter('alle) { all =>
          Bulk.alleLife
          complete("Done processing all eLife files... but you probably timed out by now")
        }
      }
    }
}

object HttpService {

  // ActorSystem for spray
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "demo-service")

  implicit val timeout = Timeout(6000.seconds)
  
  // start http listener
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 3001)
 
}