package ch.awae.esgcal

import scala.language.reflectiveCalls

import ch.awae.esgcal.scene.button
import ch.awae.esgcal.scene.glue
import ch.awae.esgcal.scene.horizontal
import ch.awae.esgcal.scene.vlock
import ch.awae.esgcal.scene.gap

class Navigation private (_left: String, _right: String, λάμδα: (Navigation.Direction, Button) => Unit) {

  val left = button(_left, λάμδα(Navigation.LEFT, _))
  val right = button(_right, λάμδα(Navigation.RIGHT, _))
  val panel =
    vlock(
      horizontal(
        gap(10),
        left,
        glue,
        right,
        gap(10)))

}

object Navigation {

  sealed trait Direction
  case object LEFT extends Direction
  case object RIGHT extends Direction

  def apply(left: String, right: String)(λάμδα: (Direction, Button) => Unit) =
    new Navigation(left, right, λάμδα)

}