import sbt._
import io.gatling.sbt.GatlingPlugin
import io.gatling.sbt.GatlingKeys.GatlingIt

object ItBuild extends Build {
  lazy val root =
    Project(id = "gatling-demo"
      , base = file("."))
      .enablePlugins(GatlingPlugin)
      .configs(GatlingIt)
      .settings(Defaults.itSettings)
} 
