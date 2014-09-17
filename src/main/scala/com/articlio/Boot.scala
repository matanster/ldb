import com.articlio.ldb
import com.articlio.reproduce
import com.articlio.selfMonitor

object Boot extends App {
  println("main starting...")
  val s = selfMonitor.selfMonitor
  val l = ldb.go
  //val w = reproduce.wierd
}