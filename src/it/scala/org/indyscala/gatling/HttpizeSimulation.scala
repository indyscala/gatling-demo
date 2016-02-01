package org.indyscala.gatling

import io.gatling.core.structure.PopulatedScenarioBuilder
import io.gatling.core.structure.ScenarioBuilder

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.concurrent.ThreadLocalRandom

class HttpizeSimulation extends Simulation {

  val Ipv4Localhost = "127.0.0.1"
  val Ipv6Localhost = "0:0:0:0:0:0:0:1"
  val Localhost = Ipv4Localhost

  val httpProtocol = http
    .baseURL("http://127.0.0.2:8080")
    .inferHtmlResources()

  val ip = scenario("Httpize - IP")
    .exec(http("ip")
      .get("/ip")
      .check(jsonPath("$.remote").is(Localhost)))

  val random_delay = scenario("Httpize - Delay"  )
    .exec(http("delay")
      .get(session => "/delay/" + ThreadLocalRandom.current.nextInt(10)))

  val random_stream = scenario("Httpize - Stream"  )
      .exec(http("stream")
          .get(session => "/stream/" + ThreadLocalRandom.current.nextInt(100)))

  val form_post = scenario("Httpize - Form")
    .exec(http("GET")
      .get("/form"))
    .pause(3)
    .exec(http("POST")
      .post("/post")
      .formParam("custname", "a")
      .formParam("custtel", "b")
      .formParam("custemail", "c@d.e")
      .formParam("size", "medium")
      .formParam("topping", "bacon")
      .formParam("topping", "mushroom")
      .formParam("delivery", "1:00")
      .formParam("comments", "Deliver it!"))

  setUp(List(ip, random_delay, random_stream, form_post).map(populateScenario))

  def populateScenario(sb: ScenarioBuilder): PopulatedScenarioBuilder = {
    sb.inject(
      nothingFor(1 seconds),
      atOnceUsers(10),
      rampUsers(10) over(5 seconds)
    ).protocols(httpProtocol)
  }
}
