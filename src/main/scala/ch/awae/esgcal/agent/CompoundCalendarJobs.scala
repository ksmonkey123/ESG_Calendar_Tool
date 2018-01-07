package ch.awae.esgcal.agent

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.postfixOps

import com.google.api.services.calendar.model.{ CalendarListEntry => Calendar }
import com.google.api.services.calendar.model.Event

import ch.awae.esgcal.ActivityReporter
import ch.awae.esgcal.Implicit.Object2Future

trait CompoundCalendarJobs {
  self: CalendarAgent =>

  def getCalendarPairs(suffix: String)(implicit report: ActivityReporter): Future[List[(Calendar, Calendar)]] = {
    report busy "lese Kalender..."
    self.getCalendarList map { list =>
      val length = list.size
      report busy "suche Kalender-Paare..."
      val result = for {
        cal_0 <- list
        cal_1 <- list
        if cal_0 != cal_1
        if (cal_0.getSummary + suffix) == cal_1.getSummary
      } yield {
        cal_1 -> cal_0
      }
      report.idle
      result
    }
  }

  def moveEvents(list: List[(List[Event], (Calendar, Calendar))])(implicit report: ActivityReporter) = Future {
    report busy "Verschieben vorbereiten..."
    val jobs = list.flatMap { case (events, move) => events map { (_, move) } }
    val size = jobs.size
    val indexedJobs = (0 until size) map { i => i -> jobs(i) }

    val result = (for ((i, (event, (from, to))) <- indexedJobs) yield {
      (report working (100 * i / size, s"verschiebe ${event.getSummary} nach ${to.getSummary}...")).f
      Await.ready(self.moveEvent(event)(from -> to), Duration.Inf)
    }) map { _.value.get } toList

    report.idle
    result
  }

}