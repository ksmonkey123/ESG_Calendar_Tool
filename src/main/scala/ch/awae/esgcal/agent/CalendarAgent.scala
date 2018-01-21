package ch.awae.esgcal.agent

import scala.collection.convert.WrapAsScala
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.implicitConversions

import java.util.Date

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.{ Calendar => CalendarService }
import com.google.api.services.calendar.model.{ CalendarListEntry => Calendar }
import com.google.api.services.calendar.model.Event

import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.Throttler

/**
 * Google Calendar API Service Agent
 *
 * Handles calls to the Google Calendar API
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @version 1.3 (2018-01-04)
 */
class CalendarAgent(credentials: Credential)(implicit protected val context: ExecutionContext) {

  type Pair[T] = (T, T)

  implicit def JList2SList[T](list: java.util.List[T]) = WrapAsScala.asScalaBuffer(list).toList

  private val JSON_FACTORY = JacksonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport
  private val API = new CalendarService.Builder(httpTransport, JSON_FACTORY, credentials).setApplicationName("ESG Calendar Tool").build
  protected val throttle = new Throttler(5)

  def getCalendarList: Future[List[Calendar]] = throttle {
    API.calendarList.list.execute.getItems
  }

  def getEventsOfCalendar(calendar: Calendar)(range: Pair[Date]) = throttle {
    val (from, to) = range
    require(from before to)

    JList2SList(
      API.events.list(calendar.getId)
        .setTimeMin(new DateTime(from))
        .setTimeMax(new DateTime(to))
        .execute
        .getItems)
  }

  def moveEvent(event: Event)(movement: Pair[Calendar]) = throttle {
    doMoveEvent(event)(movement)
  }

  protected def doMoveEvent(event: Event)(movement: Pair[Calendar]) = movement match {
    case (from, to) => API.events.move(from.getId, event.getId, to.getId).execute
  }

}