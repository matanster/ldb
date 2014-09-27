import com.articlio.ldb
import com.articlio.reproduce
import com.articlio.selfMonitor.{Monitor}
import com.articlio.util.{Console}
import com.articlio.storage

object Boot extends App {

  Console.log("starting...", "startup")

  Monitor
  val l = ldb.go

  val data = Seq(("something new", "matches something new", "indicates something"),
                 ("something new", "matches something new", "indicates something"))

  storage.DB.dropCreate
  storage.DB ++= (data)

  // closing stuff - to be moved to own function
  //s.shutdown
  Monitor.shutdown
  storage.DB.close
}