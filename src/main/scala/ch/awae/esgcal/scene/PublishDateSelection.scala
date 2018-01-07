package ch.awae.esgcal.scene

import java.awt.Color

import com.google.api.client.auth.oauth2.Credential

import ch.awae.esgcal.DateSelection
import ch.awae.esgcal.Scene
import javax.swing.SwingConstants
import ch.awae.esgcal.Navigation
import ch.awae.esgcal.Navigation._
import ch.awae.esgcal.Button
import ch.awae.esgcal.PublishModel
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import ch.awae.esgcal.Util.List2IndexedList

case class PublishDateSelection(data: PublishModel.SelectDates) extends Scene {

  val error_label = label(" ", Color.RED)
  val label_start = label("von:")
  val label_end = label("bis:")
  val select_start = new DateSelection(0, 0, 3, checkDate)
  val select_end = new DateSelection(30, 11, 3, checkDate)
  val navigation = Navigation("Abbrechen", "Weiter") {
    case (LEFT, b) => pop
    case (RIGHT, b) => fetchCalendars(b)
  }

  def start = select_start.date
  def end = select_end.date

  label_end setMaximumSize label_start.getMaximumSize
  label_end setHorizontalAlignment SwingConstants.TRAILING

  val panel =
    vertical(
      vlock(
        horizontal(
          label(s"Publikation ${if (data.inverted) "widerrufen" else "erfassen"}"))),
      glue,
      vlock(
        hcenter(
          label(" "))),
      vertical(
        horizontal(
          label_start,
          gap(10),
          select_start.build),
        gap(20),
        horizontal(
          label_end,
          gap(10),
          select_end.build)),
      vlock(
        hcenter(
          error_label)),
      glue,
      navigation.panel)

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
    val result =
      data.agent.getCalendarPairs(" - Planung") map { list =>
        report busy "lade Ereignisse..."
        val size = list.size
        list map {
          case (from, to) => if (data.inverted) to -> from else from -> to
        } map {
          case (from, to) =>
            data.agent.getEventsOfCalendar(from)(start -> end) map {
              case list => from -> to -> list
            }
        }
      } flatMap {
        Future.sequence(_)
      }

    result onComplete {
      case Success(list) =>
        report.idle
        b.enable
        push(PublishCalendarSelection(PublishModel.SelectCalendars(
          data.agent,
          data.inverted,
          start -> end,
          list.sortBy(_._1._1.getSummary))))
      case Failure(x) =>
        report.idle
        b.enable
        println(x)
        error_label.setText(s"Es ist ein Fehler aufgetreten (${x.getClass.getSimpleName})")
    }
  }

}