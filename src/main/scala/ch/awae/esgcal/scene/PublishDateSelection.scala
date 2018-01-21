package ch.awae.esgcal.scene

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

import java.awt.Color
import java.util.Date

import javax.swing.SwingConstants

import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.ui.DateSelection
import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.ui.Navigation
import ch.awae.esgcal.ui.Navigation._
import ch.awae.esgcal.PublishModel
import ch.awae.esgcal.ui.Scene

case class PublishDateSelection(data: PublishModel.SelectDates) extends Scene {

  val error_label = label(" ", Color.RED)
  val label_start = label("von:")
  val label_end = label("bis:") tweak { l =>
    l setMaximumSize label_start.getMaximumSize
    l setHorizontalAlignment SwingConstants.TRAILING
  }
  val select_start = new DateSelection(0, 0, 3, checkDate)
  val select_end = new DateSelection(30, 11, 3, checkDate)
  val navigation = Navigation("Abbrechen", "Weiter") {
    case (LEFT, b)  => pop
    case (RIGHT, b) => fetchCalendars(b)
  }

  def start = select_start.date
  def end = select_end.date

  val panel =
    vertical(
      vlock(horizontal(label(s"Publikation ${if (data.inverted) "widerrufen" else "erfassen"}"))), glue,
      vlock(hcenter(label(" "))),
      vertical(
        horizontal(label_start, gap(10), select_start.build), gap(20),
        horizontal(label_end, gap(10), select_end.build)),
      vlock(hcenter(error_label)), glue, navigation.panel)

  def checkDate(): Unit =
    if (select_start.date after select_end.date) {
      error_label.setText("'bis'-Datum muss hinter dem 'von'-Datum liegen!")
      navigation.right.setEnabled(false)
    } else {
      error_label.setText(" ")
      navigation.right.setEnabled(true)
    }

  def fetchCalendars(b: Button) = {
    b.disable
    val result = data.agent.getCalendarPairs(" - Planung") map { list =>
      report busy "lade Ereignisse..."
      for {
        _item <- list.sortBy(_._1.getSummary)
        (from, to) = if (data.inverted) _item.swap else _item
        range = (start, new Date(end.getTime + 86400000L))
      } yield {
        data.agent.getEventsOfCalendar(from)(range) map { ((from, to), _) }
      }
    } flatMap { Future.sequence(_) }

    result onComplete { _ =>
      report.idle
      b.enable
    }
    result onComplete {
      case Success(list)  => push(PublishCalendarSelection(PublishModel.SelectCalendars(data.agent, data.inverted, start -> end, list)))
      case Failure(error) => error_label.setText(s"Es ist ein Fehler aufgetreten (${error.getClass.getSimpleName})")
    }
  }

}