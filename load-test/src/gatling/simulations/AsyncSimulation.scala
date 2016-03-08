import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class AsyncSimulation extends Simulation {

  val host = System.getProperty("undertest.host", "localhost")
  val port = System.getProperty("undertest.port", "8000")

  val httpConf = http.baseURL(s"http://$host:$port")

  val scn = scenario("SyncSimulation")
    .forever {exec(
      http("OnlyRequest").get("/async").check(status.is(200))
    )}

  setUp(
    scn
      .inject(atOnceUsers(4))
      .throttle(
        reachRps(16) in (5 seconds),
        holdFor(60 seconds)
      )
  ).protocols(httpConf)

}
