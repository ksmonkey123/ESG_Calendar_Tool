package ch.awae.esgcal

import ch.awae.esgcal.agent.CalendarAgent
import ch.awae.esgcal.agent.CompoundCalendarJobs
import java.util.Date
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.{ CalendarListEntry => Calendar }

object PublishModel {

  case class SelectDates(
    agent: CalendarAgent with CompoundCalendarJobs,
    inverted: Boolean)

  case class SelectCalendars(
    agent: CalendarAgent with CompoundCalendarJobs,
    inverted: Boolean,
    dateRange: (Date, Date),
    calendars: List[((Calendar, Calendar), List[Event])])

}