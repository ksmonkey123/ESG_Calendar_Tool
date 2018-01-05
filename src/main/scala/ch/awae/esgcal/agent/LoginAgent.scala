package ch.awae.esgcal.agent

import java.awt.Desktop
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.util.Collections

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.language.postfixOps

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.CalendarScopes

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import ch.awae.esgcal.ActivityReporter
import ch.awae.esgcal.Implicit.Object2Future
import scala.concurrent.duration.Duration
import scala.concurrent.Await

/**
 * Service Agent for Google OAuth2 Authentication
 *
 * Redirects the system browser to the google authentication service and captures
 * the authentication code to generate a valid OAuth2 token.
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @version 1.1 (2018-01-02)
 */
class LoginAgent(implicit context: ExecutionContext) {
  type Flow = GoogleAuthorizationCodeFlow
  type FlowBuilder = GoogleAuthorizationCodeFlow.Builder

  val JSON_FACTORY = JacksonFactory.getDefaultInstance

  /**
   * performs user authentication
   *
   * @param port - the local port the capturing http server should use (default: 8080)
   * @return a future object eventually holding the OAuth2 credentials
   */
  def authenticate(port: Int = 8080, maxWait: Duration)(implicit report: ActivityReporter) = for {
    _ <- (report busy "warte auf Benutzer...").f
    flow <- directToAuth(port)
    code <- getCode(port, maxWait)
    _ <- (report busy "login...").f
    tokn <- getToken(flow, code, port)
    _ <- (report idle).f
  } yield {
    tokn
  }

  /**
   * directs the system browser to the google authentication page
   */
  private def directToAuth(port: Int) = Future {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
      new InputStreamReader(this.getClass.getResourceAsStream("/client_secrets.json")));
    // set up authorization code flow
    val flow = new FlowBuilder(
      httpTransport, JSON_FACTORY, clientSecrets,
      Collections.singleton(CalendarScopes.CALENDAR))
      .build();
    val authURL = flow.newAuthorizationUrl().setRedirectUri("http://127.0.0.1:" + port)
    Desktop.getDesktop.browse(authURL.toURI())
    flow
  }

  /**
   * uses the captured authentication code to generate an OAuth2 token
   */
  private def getToken(flow: Flow, code: String, port: Int) = Future {
    val resp = flow.newTokenRequest(code).setRedirectUri("http://127.0.0.1:" + port).execute()
    flow.createAndStoreCredential(resp, null)
  }

  /**
   * launches a local http server and captures the google authentication code.
   * The google authentication form is redirected to http://127.0.0.1:port/ where
   * this local server can receive the authentication code. In any case the local
   * server will be shut down after receiving a GET request.
   */
  private def getCode(port: Int, maxWait: Duration): Future[String] =
    try {
      val promise = Promise[String]
      val server = HttpServer.create(new InetSocketAddress(port), 0)
      server.createContext("/", new MyHandler(promise, server))
      server.setExecutor(null)
      server.start()
      Future {
        try {
          Await.ready(promise.future, maxWait)
        } catch {
          case e: Exception =>
            server stop 0
            promise.tryFailure(e)
        }
      }
      promise.future
    } catch {
      case e: Exception => Future failed e
    }

  /**
   * The http server handler for the "/" route extracting the authentication code from
   * the GET request redirected to the local server. Terminates the local http server
   * as soon as the first request is handled.
   */
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