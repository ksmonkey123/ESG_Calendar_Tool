package ch.awae.esgcal.scene

import ch.awae.esgcal.Scene
import javax.swing.JLabel
import com.google.api.client.auth.oauth2.Credential
import ch.awae.esgcal.Button

case class PublishDateSelection(credential: Credential, invert: Boolean) extends Scene {

  val panel =
    vertical(
      vlock(
        horizontal(
          label(s"Publikation ${if (invert) "widerrufen" else "erfassen"}"))),
      glue,
      center(
        label("Datumsauswahl")),
      glue,
      vlock(
        horizontal(
          button("Zurück", _ => pop),
          glue,
          button("Weiter", _ => push(PublishCalendarSelection(credential, invert))))))

}