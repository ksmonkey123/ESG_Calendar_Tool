package ch.awae.esgcal.scene

import scala.util.Failure
import scala.util.Success

import ch.awae.esgcal.Button
import ch.awae.esgcal.Scene
import ch.awae.esgcal.agent.LoginAgent

case class LoginScene() extends Scene {

  val panel =
    vcenter(
      center(
        button("Login", mock)))

  def mock(b: Button) = push(MainScene(null))

  def doLogin(b: Button) = {
    b.disable
    val agent = new LoginAgent
    agent authenticate 8080 onComplete {
      case Success(cred) => push(MainScene(cred))
      case Failure(err) => b.enable
    }
  }

}