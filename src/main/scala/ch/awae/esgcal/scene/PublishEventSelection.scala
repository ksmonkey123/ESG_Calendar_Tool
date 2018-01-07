package ch.awae.esgcal.scene

import com.google.api.client.auth.oauth2.Credential
import ch.awae.esgcal.Scene
import javax.swing.JLabel
import ch.awae.esgcal.Navigation
import ch.awae.esgcal.Navigation.LEFT
import ch.awae.esgcal.Navigation.RIGHT

case class PublishEventSelection(credential: Credential, invert: Boolean) extends Scene {

  val navigation = Navigation("Zurück", "Ausführen") {
    case (LEFT, _) => pop
    case (RIGHT, _) => pop(3)
  }

  val panel =
    vertical(
      vlock(
        horizontal(
          label(s"Publikation ${if (invert) "widerrufen" else "erfassen"}"))),
      glue,
      center(
        label("Ereignisauswahl")),
      glue,
      navigation.panel)

}