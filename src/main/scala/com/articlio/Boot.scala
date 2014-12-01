import com.articlio.config
import com.articlio.HttpService
import com.articlio.ldb
import com.articlio.selfMonitor.{Monitor}
import com.articlio.util.{Console}
import com.articlio.storage

object Analytic {
  val inDb = storage.InDB.Query("ubuntu-2014-11-21T12:06:51.286Z")
  storage.InDB.close
}

object Boot extends App {

  Monitor

  storage.OutDB.dropCreate
  
  ldb.ldb.init
  
  try {
    val httpService = HttpService
  } finally {
        // closing stuff - to be moved to own function
        Monitor.shutdown
        storage.OutDB.close
      }
}