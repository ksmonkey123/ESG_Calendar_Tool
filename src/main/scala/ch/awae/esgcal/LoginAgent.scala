package ch.awae.esgcal

import java.awt.Desktop
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.util.Collections
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Promise
import com.google.api.client.googleapis.auth.oauth2.{ GoogleAuthorizationCodeFlow => Flow }
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.CalendarScopes
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import com.google.api.client.googleapis.auth.oauth2.{ GoogleAuthorizationCodeFlow => Flow }

class LoginAgent(implicit context: ExecutionContext) {
  val JSON_FACTORY = JacksonFactory.getDefaultInstance

  def authenticate(port: Int) = for {
    flow <- directToAuth(port)
    code <- getCode(port)
    tokn <- getToken(flow, code, port)
  } yield {
    tokn
  }

  private def directToAuth(port: Int) = Future {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
      new InputStreamReader(this.getClass.getResourceAsStream("/client_secrets.json")));
    // set up authorization code flow
    val flow = new Flow.Builder(
      httpTransport, JSON_FACTORY, clientSecrets,
      Collections.singleton(CalendarScopes.CALENDAR))
      .build();
    val authURL = flow.newAuthorizationUrl().setRedirectUri("http://127.0.0.1:" + port)
    Desktop.getDesktop.browse(authURL.toURI())
    flow
  }

  private def getToken(flow: Flow, code: String, port: Int) = Future {
    val resp = flow.newTokenRequest(code).setRedirectUri("http://127.0.0.1:" + port).execute()
    flow.createAndStoreCredential(resp, null)
  }

  private def getCode(port: Int): Future[String] =
    try {
      val promise = Promise[String]
      val server = HttpServer.create(new InetSocketAddress(port), 0)
      server.createContext("/", new MyHandler(promise, server))
      server.setExecutor(null)
      server.start()
      promise.future
    } catch {
      case e: Exception => Future failed e
    }

  class MyHandler(promise: Promise[String], server: HttpServer) extends HttpHandler {
    def handle(t: HttpExchange) = {
      val uri = t.getRequestURI.toString

      try {
        promise success uri.split("\\s")(0).split("code=")(1)
      } catch {
        case e: Exception => promise failure e
      }

      val response = "OK";
      t.sendResponseHeaders(200, response.length)
      val os = t.getResponseBody
      os write response.getBytes
      os.close
      server stop 0
    }
  }

}