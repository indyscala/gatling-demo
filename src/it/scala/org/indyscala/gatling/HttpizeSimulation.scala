package org.indyscala.gatling

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class HttpizeSimulation extends Simulation {

  val httpProtocol = http
    .baseURL("http://127.0.0.2:8080")
    .inferHtmlResources()

  val headers_1 = Map("Accept" -> "image/png,image/*;q=0.8,*/*;q=0.5")

    val uri1 = "https://s3.amazonaws.com/github/ribbons/forkme_right_darkblue_121621.png"
    val uri2 = "http://127.0.0.2:8080"

  val scn = scenario("HttpizeSimulation")
    .exec(http("request_0")
      .get("/")
      .resources(http("request_1")
      .get(uri2 + "/favicon.ico")
      .headers(headers_1)
      .check(status.is(404)),
            http("request_2")
      .get(uri2 + "/favicon.ico")
      .check(status.is(404))))
    .pause(18)
    // Ip
    .exec(http("request_3")
      .get("/ip"))
    .pause(15)
    // Headers
    .exec(http("request_4")
      .get("/headers"))
    .pause(46)
    // Cookie Set
    .exec(http("request_5")
      .get("/cookies/set?foo=bar"))
    .pause(11)
    // Cookie Get
    .exec(http("request_6")
      .get("/cookies"))
    .pause(18)
    // Home
    .exec(http("request_7")
      .get("/"))
    .pause(27)
    // Stream
    .exec(http("request_8")
      .get("/stream/20"))
    .pause(16)
    // Delay
    .exec(http("request_9")
      .get("/delay/3"))
    .pause(15)
    // HTML Page
    .exec(http("request_10")
      .get("/moby"))
    .pause(20)
    // Form Get
    .exec(http("request_11")
      .get("/form"))
    .pause(46)
    // Form POST
    .exec(http("request_12")
      .post("/post")
      .formParam("custname", "a")
      .formParam("custtel", "b")
      .formParam("custemail", "c@d.e")
      .formParam("size", "medium")
      .formParam("topping", "bacon")
      .formParam("topping", "mushroom")
      .formParam("delivery", "1:00")
      .formParam("comments", "Deliver it!"))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
