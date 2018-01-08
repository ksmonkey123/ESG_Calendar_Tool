package ch.awae.esgcal

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global

import java.awt.BorderLayout
import java.awt.Dimension

import javax.swing.JFrame
import javax.swing.JProgressBar
import javax.swing.border.EmptyBorder

import ch.awae.esgcal.scene._

class GUI extends SceneManager with ActivityReporter {

  implicit val self = this

  var scenes = List[Scene](LoginScene())

  val frame = new JFrame("ESG Calendar Tool")
  frame setLayout new BorderLayout
  frame setPreferredSize new Dimension(500, 700)
  frame setMinimumSize new Dimension(500, 700)
  frame setMaximumSize new Dimension(500, 700)

  val progressBar = new JProgressBar(0, 100)
  val sta_label = label(if (Globals.DEBUG) "IDLE" else " ")

  val progressPane = vertical(progressBar, horizontal(sta_label, glue))

  progressPane.setBorder(new EmptyBorder(10, 10, 10, 10))

  frame.add(progressPane, BorderLayout.SOUTH)

  frame add scenes.head.panel
  scenes.head.unlock
  scenes.head bind this

  if (Globals.DEBUG) {
    frame setTitle (scenes.size + ": " + scenes.head.getClass.getSimpleName)
    progressBar setStringPainted true
  }

  frame.pack
  frame setVisible true
  frame setDefaultCloseOperation JFrame.EXIT_ON_CLOSE

  def idle: Unit = {
    ConsoleReporter.idle
    progressBar setValue 0
    progressBar setIndeterminate false
    sta_label setText (if (Globals.DEBUG) "IDLE" else " ")
  }

  def busy(message: String) = {
    ConsoleReporter busy message
    progressBar setIndeterminate true
    sta_label setText ((if (Globals.DEBUG) "BUSY " else "") + message + " ")
  }

  def working(progress: Int, message: String): Unit = {
    ConsoleReporter.working(progress, message)
    progressBar setIndeterminate false
    progressBar setValue progress
    sta_label setText ((if (Globals.DEBUG) "WORKING " else "") + message + " ")
  }

  def push(scene: Scene): Unit = {
    scenes.head.lock
    frame remove scenes.head.panel
    frame add scene.panel
    scene bind this
    scene.unlock
    scenes = scene :: scenes

    if (Globals.DEBUG)
      frame setTitle (scenes.size + ": " + scenes.head.getClass.getSimpleName)

    frame.pack()
    frame.repaint()
  }

  def pop(levels: Int): Unit = if (scenes.size > 0) {
    @tailrec def drop(i: Int): Unit = if (i > 0 && scenes.size > i) {
      scenes = scenes.tail
      drop(i - 1)
    }

    scenes.head.lock
    frame remove scenes.head.panel
    drop(levels)
    frame add scenes.head.panel
    scenes.head.unlock

    if (Globals.DEBUG)
      frame setTitle (scenes.size + ": " + scenes.head.getClass.getSimpleName)

    frame.pack()
    frame.repaint()
  }

}