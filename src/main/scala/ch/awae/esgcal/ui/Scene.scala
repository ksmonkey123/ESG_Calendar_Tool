package ch.awae.esgcal.ui

import scala.concurrent.ExecutionContext
import javax.swing.JPanel
import ch.awae.esgcal.ActivityReporter

trait Scene {

  val panel: JPanel

  private var _context: ExecutionContext = _
  private var _manager: SceneManager = _
  private var _report: ActivityReporter = _
  private var locked = true

  implicit final protected def report: ActivityReporter = _report
  implicit final protected def context: ExecutionContext = _context

  final def bind(manager: SceneManager)(implicit report: ActivityReporter, context: ExecutionContext) = {
    _manager = manager
    _report = report
    _context = context
  }

  final def lock = locked = true
  final def unlock = locked = false
  final def push(scene: Scene) = if (!locked) _manager push scene
  final def pop: Unit = pop(1)
  final def pop(levels: Int): Unit = if (!locked) _manager pop levels
  final def popTo(level: Int): Unit = if (!locked) _manager popTo level
  final def getFileSaveLocation(file: String = null, suffix: String = null): Option[String] =
    _manager.getFileSaveLocation((file, suffix) match {
      case (null, null) => "*"
      case (null, suff) => "*" + suff
      case (file, _)    => file
    }) map {
      case file if suffix == null        => file
      case file if file.endsWith(suffix) => file
      case file                          => file + suffix
    }

}