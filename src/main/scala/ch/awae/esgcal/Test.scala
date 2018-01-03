package ch.awae.esgcal

import scala.util.Success
import scala.util.Failure

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.google.api.services.calendar.Calendar
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import scala.collection.convert.WrapAsScala
import ch.awae.esgcal.agent.CalendarAgent
import ch.awae.esgcal.agent.LoginAgent

object Test extends App {

  val token = new LoginAgent().authenticate(8080)

  val tok = Await.result(token, Duration.Inf)

  val calendar = new CalendarAgent(tok)

  for (_ <- 1 to 10) {
    println(Await.result(calendar.getCalendarList, Duration.Inf))
  }

}