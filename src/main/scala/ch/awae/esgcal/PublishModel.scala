package ch.awae.esgcal

import java.util.Date

import com.google.api.services.calendar.model.{ CalendarListEntry => Calendar }
import com.google.api.services.calendar.model.Event

import agent._

object PublishModel {

  case class SelectDates(
    agent: CalendarAgent with CompoundCalendarJobs,
    inverted: Boolean)

  case class SelectCalendars(
    agent: CalendarAgent with CompoundCalendarJobs,
    inverted: Boolean,
    dateRange: (Date, Date),
    calendars: List[((Calendar, Calendar), List[Event])])

  case class SelectEvents(
    agent: CalendarAgent with CompoundCalendarJobs,
    inverted: Boolean,
    calendars: List[((Calendar, Calendar), List[Event])])

}