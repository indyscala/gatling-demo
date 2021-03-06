package org.purang.net.httpize

import org.http4s.headers.`Content-Type`
import org.http4s.{DateTime, MediaType, Response, Request}
import org.http4s.dsl._

import scala.collection.mutable

import scalaz.concurrent.Task
import scalaz.stream.Process
import scalaz.stream.io.chunkR



class ResourceCache {

  private val startDate = DateTime.now

  private val cacheMap = new mutable.HashMap[String, Array[Byte]]()

  private val sanitizeRegex = "\\.\\.".r

  // This is almost certainly not good enough for sanitization...
  private def sanitize(path: String): String = sanitizeRegex.replaceAllIn(path, ".")

  private def checkResource(path: String): Option[Array[Byte]] = {
    // See if we have a minified version of js available first
    val rs = if (path.endsWith(".js")) {
      val min = path.substring(0, path.length - 3) + ".min.js"
      Option(getClass.getResourceAsStream(sanitize(min))) orElse
      Option(getClass.getResourceAsStream(sanitize(path)))
    } else Option(getClass.getResourceAsStream(sanitize(path)))
    rs.map { p =>
      val bytes  = Process.constant(8*1024)
        .toSource
        .through(chunkR(p))
        .runLog
        .run
        .map(_.toArray)
        .toArray
        .flatten

      cacheMap(path) = bytes
      bytes
    }
  }

  private def assemblePath(path: String, name: String): String = {
    val realdir = if (path != "") path else ""
    val realname = if (name.startsWith("/")) name.substring(1) else name
    s"$realdir/$realname"
  }

  private def _getResource(dir: String, name: String): Task[Response] = {
    val path = assemblePath(dir, name)
    cacheMap.synchronized {
      cacheMap.get(path) orElse checkResource(path)
    }.fold(NotFound(s"404 Not Found: '$path'")){ bytes =>

      val mime = {
        val parts = path.split('.')
        if (parts.length > 0) MediaType.forExtension(parts.last)
          .getOrElse(MediaType.`application/octet-stream`)
        else MediaType.`application/octet-stream`
      }

      Ok(bytes).putHeaders(`Content-Type`(mime))
    }
  }

  def getResource(dir: String, name: String, req: Request): Task[Response] =
      _getResource(dir, name)

}

