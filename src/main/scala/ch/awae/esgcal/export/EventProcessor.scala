package ch.awae.esgcal.export

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.google.api.services.calendar.model.{ CalendarListEntry => GCalendar }
import com.google.api.services.calendar.model.Event

import ch.awae.esgcal.Implicit._
import java.time.LocalDate
import scala.annotation.tailrec
import java.time.ZoneOffset
import ch.awae.esgcal.agent.CalendarAgent

class EventProcessor[T <: EventParser](val parser: T) {

  def execute(agent: CalendarAgent, from: LocalDate, to: LocalDate)(implicit context: ExecutionContext) = {
    for {
      calendars <- agent.getCalendarList
      result <- run(calendars, agent.getEventsOfCalendar(_)(from.toDate -> to.toDate))
    } yield {
      parser.getEntries(from -> to, result)
    }
  }

  def run(calendars: List[GCalendar], fetcher: GCalendar => Future[List[Event]])(implicit context: ExecutionContext) = {
    val events = Future.sequence(for {
      calendar <- calendars
      title = calendar.getSummary
      searched <- parser.calendars
      reference <- searched.sources
      if reference == title
    } yield {
      fetcher(calendar) map { x => searched -> x }
    })

    val parsed = events map { evts =>
      val l = for {
        (calendar, events) <- evts
        event <- events
        (local, in) <- fragment(event, parser.extractor(calendar, event))
      } yield {
        local -> parser.parser(in)
      }
      l.groupBy(_._1).map { case (x, ys) => x -> ys.map(_._2) }
    }

    val merged = parsed map { prsd =>
      prsd.map {
        case (local, outs) => local -> outs.foldLeft(List.empty[parser.Out]) {
          case (Nil, x) => x :: Nil
          case (list, x) =>
            list
              .find { h => parser.merger.isDefinedAt((h, x)) }
              .map { h => parser.merger((h, x)) :: list.filterNot(h==) }
              .getOrElse(x :: list)
        }
      }
    }

    val directed = merged map { mrgd =>
      mrgd.map {
        case (local, outs) => local -> (outs flatMap parser.director)
      }.toList.sortBy(_._1.toEpochDay())
    }

    directed

  }

  def fragment(raw: Event, event: parser.In): List[(LocalDate, parser.In)] = {
    @tailrec
    def frag(start: LocalDate, end: LocalDate, l: List[LocalDate]): List[LocalDate] =
      if (start != end)
        frag(start, end minusDays 1, end :: l)
      else
        end :: l

    val start = raw.getStart.toLocal
    val end = raw.getEnd.toLocal

    val ONE_DAY = 24 * 60 * 60

    val startSec = start.toEpochSecond(ZoneOffset.UTC)
    val endSec = end.toEpochSecond(ZoneOffset.UTC)

    if ((endSec - startSec) % ONE_DAY == 0) {
      frag(start.toLocalDate, end.minusDays(1).toLocalDate, Nil) map { _ -> event }
    } else {
      frag(start.toLocalDate, end.toLocalDate, Nil) map { _ -> event }
    }
  }

}