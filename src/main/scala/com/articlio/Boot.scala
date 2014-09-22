import com.articlio.ldb
import com.articlio.reproduce
import com.articlio.selfMonitor
import com.articlio.storage

object Boot extends App {

  println("main starting...")

  val s = selfMonitor.selfMonitor
  val l = ldb.go
  val d = storage.DB

  s.shutdown
}