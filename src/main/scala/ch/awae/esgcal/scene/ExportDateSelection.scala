package ch.awae.esgcal.scene

import java.awt.Color

import javax.swing.SwingConstants

import com.google.api.client.auth.oauth2.Credential

import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.agent.CalendarAgent
import ch.awae.esgcal.export.parser.GanztagListe
import ch.awae.esgcal.export.parser._Type
import ch.awae.esgcal.export.parser._Types._
import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.ui.DateSelection
import ch.awae.esgcal.ui.Navigation
import ch.awae.esgcal.ui.Navigation._
import ch.awae.esgcal.ui.Scene
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.xssf.Workbook

case class ExportDateSelection(credential: Credential, export: _Type) extends Scene {

  val error_label = label(" ", Color.RED)
  val label_start = label("von:")
  val label_end = label("bis:") tweak { l =>
    l setMaximumSize label_start.getMaximumSize
    l setHorizontalAlignment SwingConstants.TRAILING
  }
  val select_start = new DateSelection(0, 0, 3, checkDate)
  val select_end = new DateSelection(30, 11, 3, checkDate)
  val navigation = Navigation("Abbrechen", "Exportieren") {
    case (LEFT, b)  => pop
    case (RIGHT, b) => process(b)
  }

  def start = select_start.localDate
  def end = select_end.localDate

  val panel =
    vertical(
      vlock(horizontal(label(s"Export: ${export.title}"))), glue,
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

  def process(b: Button) = export match {
    case Ganztag => {
      b.disable
      val agent = new CalendarAgent(credential)
      val book = Workbook.empty
      getFileSaveLocation(suffix = ".xlsx") match {
        case Some(file) =>
          println(file)
          GanztagListe.processAll(agent, start, end, book) onComplete { x =>
            book.write(file)
            popTo(1)
          }
        case None => b.enable
      }
    }
    case _ => ???
  }

}