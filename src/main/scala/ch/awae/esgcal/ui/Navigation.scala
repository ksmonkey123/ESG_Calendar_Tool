package ch.awae.esgcal.ui

import scala.language.reflectiveCalls
import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.scene._

class Navigation private (_left: String, _right: String, λάμδα: (Navigation.Direction, Button) => Unit) {

  val left = button(_left, λάμδα(Navigation.LEFT, _))
  val right = if (_right == null) null else button(_right, λάμδα(Navigation.RIGHT, _))
  val panel = vlock(horizontal(gap(10), left, glue, if (right == null) glue else right, gap(10)))

}

object Navigation {

  sealed trait Direction
  case object LEFT extends Direction
  case object RIGHT extends Direction

  def apply(left: String, right: String)(λάμδα: (Direction, Button) => Unit) =
    new Navigation(left, right, λάμδα)

}