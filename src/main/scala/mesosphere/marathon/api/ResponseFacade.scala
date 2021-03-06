package mesosphere.marathon
package api

import java.net.URI
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.{NewCookie, Response}

import mesosphere.marathon.plugin.http.HttpResponse

/**
  * Implementation of plugin-interface HttpResponse; a response builder with some extra helper methods.
  */
class ResponseFacade extends HttpResponse {
  private[this] var builder = Response.status(Status.UNAUTHORIZED)
  override def header(name: String, value: String): Unit = builder.header(name, value)
  override def status(code: Int): Unit = builder = builder.status(code)
  override def sendRedirect(location: String): Unit = {
    builder.status(Status.TEMPORARY_REDIRECT).location(new URI(location))
  }
  override def cookie(name: String, value: String, maxAge: Int, secure: Boolean): Unit = {
    builder.cookie(new NewCookie(name, value, null, null, null, maxAge, secure))
  }
  override def body(mediaType: String, bytes: Array[Byte]): Unit = {
    builder.`type`(mediaType)
    builder.entity(bytes)
  }
  def response: Response = builder.build()
}

object ResponseFacade {

  /**
    * Factory method for building a response using [[ResponseFacade]]
    */
  def apply(fn: HttpResponse => Unit): Response = {
    val responseFacade = new ResponseFacade
    fn(responseFacade)
    responseFacade.response
  }
}
