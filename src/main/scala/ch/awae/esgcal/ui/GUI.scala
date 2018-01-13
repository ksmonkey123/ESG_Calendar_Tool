package ch.awae.esgcal.ui

import scala.concurrent.ExecutionContext.Implicits.global
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JProgressBar
import javax.swing.border.EmptyBorder
import ch.awae.esgcal.scene._
import ch.awae.esgcal.ActivityReporter
import ch.awae.esgcal.ConsoleReporter

class GUI extends SceneManager with ActivityReporter {
  implicit val self = this

  // ==== Base Frame ====
  val frame = new JFrame("ESG Calendar Tool")
  frame setLayout new BorderLayout
  frame setPreferredSize new Dimension(500, 700)
  frame setMinimumSize new Dimension(500, 700)
  frame setMaximumSize new Dimension(500, 700)

  def init = {
    updateScene
    frame.pack
    frame setVisible true
    frame setDefaultCloseOperation JFrame.EXIT_ON_CLOSE
  }

  // ==== Progress Bar and Activity Label ====
  val progressBar = new JProgressBar(0, 100)
  val sta_label = label(" ")
  val progressPane = vertical(progressBar, horizontal(sta_label, glue))
  progressPane.setBorder(new EmptyBorder(10, 10, 10, 10))
  frame.add(progressPane, BorderLayout.SOUTH)

  // ==== Scene Management ====
  var scenes = List[Scene](new LoginScene)

  def push(scene: Scene) = {
    scenes ::= scene
    updateScene
  }

  def pop(levels: Int): Unit = (levels, scenes.size) match {
    case (_, 1) =>
    case (0, _) => updateScene
    case (x, _) => scenes = scenes.tail; pop(x - 1)
  }

  private var currentScene: Scene = _
  private def updateScene = synchronized {
    if (currentScene != scenes.head) {
      if (currentScene != null) {
        currentScene.lock
        frame remove currentScene.panel
      }
      val scene = scenes.head
      frame add scene.panel
      scene bind this
      scene.unlock
      frame.pack()
      frame.repaint()
      currentScene = scene
    }
  }

  // ==== ActivityReporting ====
  def idle = {
    ConsoleReporter.idle
    progressBar setValue 0
    progressBar setIndeterminate false
    sta_label setText " "
  }

  def busy(message: String) = {
    ConsoleReporter busy message
    progressBar setIndeterminate true
    sta_label setText s"$message "
  }

  def working(progress: Int, message: String) = {
    ConsoleReporter.working(progress, message)
    progressBar setIndeterminate false
    progressBar setValue progress
    sta_label setText s"$message "
  }

}