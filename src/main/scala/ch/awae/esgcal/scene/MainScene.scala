package ch.awae.esgcal.scene

import com.google.api.client.auth.oauth2.Credential

import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.PublishModel
import ch.awae.esgcal.ui.Scene
import ch.awae.esgcal.agent.CalendarAgent
import ch.awae.esgcal.agent.CompoundCalendarJobs
import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.export.EventProcessor
import com.google.api.client.json.jackson2.JacksonFactory
import ch.awae.esgcal.export.parser.Jahresplan
import java.util.Date
import java.util.Calendar
import ch.awae.esgcal.xssf.Workbook
import java.time.LocalDate
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator

case class MainScene(credential: Credential) extends Scene {

  val panel =
    vcenter(
      center(button("Exportieren", doExport)), gap(30),
      center(button("Publizieren", doPublish(false))), gap(30),
      center(button("Publikation widerrufen", doPublish(true))))

  def doExport(b: Button) = push(ExportScene(credential))

  def doPublish(inverted: Boolean)(b: Button) = push(PublishDateSelection(PublishModel.SelectDates(new CalendarAgent(credential) with CompoundCalendarJobs, inverted)))

}