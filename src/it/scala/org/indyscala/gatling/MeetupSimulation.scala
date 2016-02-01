package org.indyscala.gatling

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MeetupSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://www.meetup.com")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())

	val headers_2 = Map(
		"Accept" -> "application/json, text/javascript, */*; q=0.01",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_3 = Map(
		"Accept" -> "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01",
		"X-Requested-With" -> "XMLHttpRequest")

    val uri1 = "http://www.meetup.com"

	val scn = scenario("MeetupSimulation")
		.exec(http("request_0")
			.get("/IndyScala/"))
		.pause(2)
		.exec(http("request_1")
			.get("/IndyScala/events/228261031/"))
		.pause(2)
		.exec(http("request_2")
			.get("/IndyScala/api/?method=getChapterMemberInfo&arg_member=9156876&arg_authMember=0")
			.headers(headers_2)
			.resources(http("request_3")
			.get(uri1 + "/api/?callback=jQuery17108712321960167058_1454291849822&method=pageStatJs&arg_pageName=chapterMemberInfo.total%2CchapterMemberInfo.false&_=1454291852661")
			.headers(headers_3),
            http("request_4")
			.get(uri1 + "/IndyScala/members/9156876")))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}