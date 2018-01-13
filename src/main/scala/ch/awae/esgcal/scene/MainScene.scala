package ch.awae.esgcal.scene

import com.google.api.client.auth.oauth2.Credential

import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.PublishModel
import ch.awae.esgcal.ui.Scene
import ch.awae.esgcal.agent.CalendarAgent
import ch.awae.esgcal.agent.CompoundCalendarJobs
import ch.awae.esgcal.Implicit._

case class MainScene(credential: Credential) extends Scene {

  val panel =
    vcenter(
      center(button("Exportieren", doExport) Λ { _ setEnabled false }), gap(30),
      center(button("Publizieren", doPublish(false))), gap(30),
      center(button("Publikation widerrufen", doPublish(true))))

  def doExport(b: Button) = ???

  def doPublish(inverted: Boolean)(b: Button) = push(PublishDateSelection(PublishModel.SelectDates(new CalendarAgent(credential) with CompoundCalendarJobs, inverted)))

}