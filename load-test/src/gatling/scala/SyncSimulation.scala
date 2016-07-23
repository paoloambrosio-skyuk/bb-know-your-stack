import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class SyncSimulation extends Simulation {

  val host = System.getProperty("undertest.host", "localhost")
  val port = System.getProperty("undertest.port", "8000")

  val httpConf = http.baseURL(s"http://$host:$port")

  val scn = scenario("SyncSimulation")
    .forever {
      exec(
        http("OnlyRequest").get("/sync")
          .check(status.is(200))
      ).pace(1 second)
    }

  setUp(
    scn.inject(rampUsers(8) over (5 seconds))
  )
    .maxDuration(2 minutes)
    .protocols(httpConf)

}
