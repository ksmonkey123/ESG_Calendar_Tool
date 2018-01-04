package ch.awae.esgcal

import java.awt.Component

import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import scala.concurrent.ExecutionContext

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
  final def push(scene: Scene) = if (!locked) _manager.push(scene)
  final def pop: Unit = pop(1)
  final def pop(levels: Int): Unit = if (!locked) _manager.pop(levels)

}