package ch.awae.esgcal.agent

import scala.concurrent.Future
import scala.language.postfixOps

import com.google.api.services.calendar.model.{ CalendarListEntry => Calendar }
import com.google.api.services.calendar.model.Event

import ch.awae.esgcal.{ ActivityReporter => Rep }
import ch.awae.esgcal.AsyncReporting
import ch.awae.esgcal.Implicit._

trait CompoundCalendarJobs {
  self: CalendarAgent =>

  def getCalendarPairs(suffix: String)(implicit report: Rep) = {
    report busy "lese Kalender..."
    self.getCalendarList map { list =>
      val length = list.size
      report busy "suche Kalender-Paare..."
      (for {
        cal_0 <- list
        cal_1 <- list
        if cal_0 != cal_1
        if (cal_0.getSummary + suffix) == cal_1.getSummary
      } yield cal_1 -> cal_0) tweak { _ => report.idle }
    }
  }

  def moveEvents(list: List[(List[Event], Pair[Calendar])])(implicit report: Rep) = {
    report busy "Verschieben vorbereiten..."
    // flatten movement jobs
    val jobs = for {
      (events, movement) <- list
      event <- events
    } yield (event, movement)
    val size = jobs.size
    val indexedJobs = (0 until size) map { i => i -> jobs(i) }
    val logger = new AsyncReporting(report, size)
    Future.sequence(for ((i, (event, (from, to))) <- indexedJobs) yield {
      throttle {
        logger progress s"verschiebe '${event.getSummary}' nach '${to.getSummary}'..."
        self.doMoveEvent(event)(from -> to)
      }
    }) map { _.toList } tweak { _ onComplete { _ => report.idle } }
  }

}