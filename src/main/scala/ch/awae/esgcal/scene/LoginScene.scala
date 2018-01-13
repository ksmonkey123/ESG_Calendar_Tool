package ch.awae.esgcal.scene

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Success

import java.awt.Color

import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.ui.Scene
import ch.awae.esgcal.agent.LoginAgent

case class LoginScene() extends Scene {

  val errorLabel = label(" ", Color.RED)

  val panel =
    vcenter(
      vlock(hcenter(label(" "))),
      vlock(hcenter(button("Login", doLogin))),
      vlock(hcenter(errorLabel)))

  def doLogin(b: Button) = {
    b.disable
    errorLabel setText " "
    new LoginAgent().authenticate(8080, 120 seconds) onComplete {
      case Success(cred) => push(MainScene(cred))
      case Failure(err) =>
        errorLabel setText "Login fehlgeschlagen"
        report.idle
        b.enable
    }
  }

}