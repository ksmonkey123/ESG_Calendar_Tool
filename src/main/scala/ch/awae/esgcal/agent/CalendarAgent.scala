package ch.awae.esgcal.agent

import java.util.Date

import scala.collection.convert.WrapAsScala
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.implicitConversions

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.CalendarListEntry
import com.google.api.services.calendar.model.Event

import ch.awae.esgcal.Throttler

/**
 * Google Calendar API Service Agent
 *
 * Handles calls to the Google Calendar API
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @version 1.2 (2018-01-03)
 */
class CalendarAgent(credentials: Credential)(implicit context: ExecutionContext) {

  implicit def JList2SList[T](list: java.util.List[T]) = WrapAsScala.asScalaBuffer(list).toList

  private val JSON_FACTORY = JacksonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport
  private lazy val API = new Calendar.Builder(httpTransport, JSON_FACTORY, credentials).setApplicationName("ESG Calendar Tool").build

  private val throttle = new Throttler(5)

  def getCalendarList: Future[List[CalendarListEntry]] = throttle {
    API.calendarList.list.execute.getItems
  }

  def getEventsOfCalendar(calendarID: String)(range: (Date, Date)): Future[List[Event]] = throttle {
    val (from, to) = range
    require(from before to)
    API.events.list(calendarID).setTimeMin(new DateTime(from)).setTimeMax(new DateTime(to)).execute.getItems
  }

  def moveEvent(eventID: String)(movement: (String, String)) = throttle {
    val (from, to) = movement
    API.events.move(from, eventID, to).execute
  }

}