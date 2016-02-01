package org.indyscala.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Group {
  val browse = exec(http("Indy Scala Meetup Page")
      .get("/IndyScala/")
      .check(status.is(200)))
    .pause(2)
    .exec(http("Gatling Meeting")
      .get("/IndyScala/events/228261031/")
      .check(status.is(200))
      .check(regex("E-gineering LLC").exists)
      .check(regex("short talk").exists)
      .check(regex("boring").notExists))
    .pause(2)
}

object Brad {
  val xhr_headers = Map(
    "Accept" -> "application/json, text/javascript, */*; q=0.01",
    "X-Requested-With" -> "XMLHttpRequest")

  val xhr_js_headers = Map(
    "Accept" -> "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01",
    "X-Requested-With" -> "XMLHttpRequest")

  val browse = exec(http("Brad - Hover")
      .get("/IndyScala/api/?method=getChapterMemberInfo&arg_member=9156876&arg_authMember=0")
      .headers(xhr_headers)
      .resources(
        http("pageStat.js").get("/api/?callback=jQuery17108712321960167058_1454291849822&method=pageStatJs&arg_pageName=chapterMemberInfo.total%2CchapterMemberInfo.false&_=1454291852661")
        .headers(xhr_js_headers)))
    .pause(2)
    .exec(http("Brad - Detail")
      .get("/IndyScala/members/9156876")
      // .check(css("""a[title=^'Twitter:']""", "href").is("http://twitter.com/bfritz"))
      .check(regex("""Twitter: @bfritz""").exists)
      .check(regex("""https?://twitter.com/bfritz""").exists))
    .pause(2)
}

class RefinedMeetupSimulation extends Simulation {

  val httpProtocol = http
    .baseURL("http://www.meetup.com")
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())

  val scn = scenario("RefinedMeetupSimulation")
    .exec(Group.browse, Brad.browse)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
