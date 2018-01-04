package ch.awae.esgcal.scene

import com.google.api.client.auth.oauth2.Credential
import ch.awae.esgcal.Scene
import javax.swing.JLabel

case class PublishEventSelection(credential: Credential, invert: Boolean) extends Scene {

  val panel =
    vertical(
      vlock(
        horizontal(
          label(s"Publikation ${if (invert) "widerrufen" else "erfassen"}"))),
      glue,
      center(new JLabel(s"Ereignisauswahl")),
      glue,
      vlock(
        horizontal(
          button("Zurück", _ => pop),
          glue,
          button("Ausführen", _ => pop(3)))))

}