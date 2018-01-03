package ch.awae.esgcal

import scala.concurrent.ExecutionContext

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar
import scala.collection.convert.WrapAsScala

class CalendarAgent(credentials: Credential)(implicit context: ExecutionContext) {

  private val JSON_FACTORY = JacksonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport
  private lazy val API = new Calendar.Builder(httpTransport, JSON_FACTORY, credentials).setApplicationName("ESG Calendar Tool").build

  private val throttle = new Throttler(5)

  def getCalendarList =  throttle {
    WrapAsScala.asScalaBuffer(API.calendarList().list().execute().getItems).toList
  }

}