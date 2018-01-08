package ch.awae.esgcal.agent

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.postfixOps

import com.google.api.services.calendar.model.{ CalendarListEntry => Calendar }
import com.google.api.services.calendar.model.Event

import ch.awae.esgcal.ActivityReporter
import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.AsyncReporting

trait CompoundCalendarJobs {
  self: CalendarAgent =>

  def getCalendarPairs(suffix: String)(implicit report: ActivityReporter): Future[List[(Calendar, Calendar)]] = {
    report busy "lese Kalender..."
    self.getCalendarList map { list =>
      val length = list.size
      report busy "suche Kalender-Paare..."
      (for {
        cal_0 <- list
        cal_1 <- list
        if cal_0 != cal_1
        if (cal_0.getSummary + suffix) == cal_1.getSummary
      } yield cal_1 -> cal_0) Λ { _ => report.idle }
    }
  }

  def moveEvents(list: List[(List[Event], (Calendar, Calendar))])(implicit report: ActivityReporter) = {
    report busy "Verschieben vorbereiten..."
    val jobs = list flatMap { case (events, move) => events map { (_, move) } }
    val size = jobs.size
    val indexedJobs = (0 until size) map { i => i -> jobs(i) }
    val logger = new AsyncReporting(report, size)
    Future.sequence(for ((i, (event, (from, to))) <- indexedJobs) yield {
      throttle {
        logger progress s"verschiebe '${event.getSummary}' nach '${to.getSummary}'..."
        self.doMoveEvent(event)(from -> to)
      }
    }) map { _.toList } Λ { _ onComplete { _ => report.idle } }
  }

}