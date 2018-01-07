package ch.awae.esgcal.scene

import scala.util.Failure
import scala.util.Success

import ch.awae.esgcal.Button
import ch.awae.esgcal.Scene
import ch.awae.esgcal.agent.LoginAgent
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.language.postfixOps
import java.awt.Color
import java.awt.Dimension

case class LoginScene() extends Scene {

  val errorLabel = label(" ", Color.RED)

  val panel =
    vcenter(
      vlock(
        hcenter(
          label(" "))),
      vlock(
        hcenter(
          button("Login", doLogin))),
      vlock(
        hcenter(
          errorLabel)))


  def mock(b: Button) = push(MainScene(null))

  def doLogin(b: Button) = {
    b.disable
    errorLabel setText " "
    val agent = new LoginAgent
    agent.authenticate(8080, 20 seconds) onComplete {
      case Success(cred) => push(MainScene(cred))
      case Failure(err) =>
        errorLabel setText "Login fehlgeschlagen"
        report.idle
        b.enable
    }
  }

}