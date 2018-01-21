package ch.awae.esgcal.scene

import scala.language.postfixOps

import java.awt.Color
import java.time.LocalDate
import java.util.Calendar

import javax.swing.JComboBox

import com.google.api.client.auth.oauth2.Credential

import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.agent.CalendarAgent
import ch.awae.esgcal.export.parser.Jahresplan
import ch.awae.esgcal.export.parser._Type
import ch.awae.esgcal.export.parser._Types
import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.ui.Navigation
import ch.awae.esgcal.ui.Navigation._
import ch.awae.esgcal.ui.Scene
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.xssf.Workbook
import ch.awae.esgcal.export.parser._Types.JahresplanBE
import ch.awae.esgcal.export.parser.JahresplanBK
import ch.awae.esgcal.export.parser.JahresplanZH

case class ExportYearSelection(credential: Credential, export: _Type) extends Scene {

  val error_label = label(" ", Color.RED)
  val cb_years = new JComboBox(-2 to 10 map { _ + (Calendar.getInstance get Calendar.YEAR) + "" } toArray)

  cb_years.setSelectedIndex(3)

  val navigation = Navigation("Abbrechen", "Exportieren") {
    case (LEFT, b)  => pop
    case (RIGHT, b) => process(b)
  }

  val panel =
    vertical(
      horizontal(label(s"Export: ${export.title}")),
      glue,
      vertical(horizontal(glue, cb_years, glue)),
      glue,
      navigation.panel)

  def process(b: Button) = export match {
    case _Types.Jahresplan => {
      b.disable
      val agent = new CalendarAgent(credential)
      val year = cb_years.getSelectedItem.toString.toInt
      getFileSaveLocation(s"Jahresplanung ESG $year.xlsm", ".xlsm") match {
        case Some(target) =>
          val book = Workbook fromResource "JP.xlsm"
          Jahresplan.processAll(agent, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31), book) onComplete { x =>
            book.write(target)
            popTo(1)
          }
        case None => b.enable
      }
    }

    case _Types.JahresplanBE => {
      b.disable
      val agent = new CalendarAgent(credential)
      val year = cb_years.getSelectedItem.toString.toInt
      getFileSaveLocation(s"Jahresplanung Bern $year.xlsx", ".xlsx") match {
        case Some(target) =>
          val book = Workbook.empty
          JahresplanBK.processAll(agent, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31), book) onComplete { x =>
            book.write(target)
            popTo(1)
          }
        case None => b.enable
      }
    }
    
    case _Types.JahresplanZH => {
      b.disable
      val agent = new CalendarAgent(credential)
      val year = cb_years.getSelectedItem.toString.toInt
      getFileSaveLocation(s"Jahresplanung Zürich $year.xlsx", ".xlsx") match {
        case Some(target) =>
          val book = Workbook.empty
          JahresplanZH.processAll(agent, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31), book) onComplete { x =>
            book.write(target)
            popTo(1)
          }
        case None => b.enable
      }
    }

    case _ => ???
  }
}