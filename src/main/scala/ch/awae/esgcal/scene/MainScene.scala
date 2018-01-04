package ch.awae.esgcal.scene

import com.google.api.client.auth.oauth2.Credential

import ch.awae.esgcal.Button
import ch.awae.esgcal.Scene

case class MainScene(credential: Credential) extends Scene {

  val panel =
    vcenter(
      center(button("Exportieren", _ => ???)),
      gap(30),
      center(button("Publizieren", doPublish(false))),
      gap(30),
      center(button("Publikation widerrufen", doPublish(true))))

  def doPublish(inverted: Boolean)(b: Button) =
    push(PublishDateSelection(credential, inverted))

}