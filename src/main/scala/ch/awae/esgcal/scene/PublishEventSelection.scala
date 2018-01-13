package ch.awae.esgcal.scene

import scala.util.Failure
import scala.util.Success

import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

import javax.swing.JCheckBox
import javax.swing.JScrollPane
import javax.swing.JTabbedPane

import ch.awae.esgcal.ui.Button
import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.ui.Navigation
import ch.awae.esgcal.ui.Navigation._
import ch.awae.esgcal.PublishModel
import ch.awae.esgcal.ui.Scene

case class PublishEventSelection(data: PublishModel.SelectEvents) extends Scene {

  val navigation = Navigation("Zurück", "Ausführen") {
    case (LEFT, _)  => pop
    case (RIGHT, b) => process(b)
  }

  val model = data.calendars map {
    case (cals, events) => (cals, events map {
      case event => (new JCheckBox() Λ { _ setSelected !data.inverted }, event)
    })
  }

  val sections = model map {
    case (cals, events) => (cals, events map {
      case (box, event) =>
        horizontal(
          box, gap(10), vertical(label(event.getSummary) Λ { l =>
            l setFont l.getFont.deriveFont(l.getFont.getSize * 1.2f).deriveFont(Font.BOLD)
          },
            label(event.getStart.toLocal.niceString + " - " + event.getEnd.toLocal.niceString))
            Λ {
              _ addMouseListener new MouseAdapter {
                override def mouseClicked(e: MouseEvent) = {
                  if (e.getButton == MouseEvent.BUTTON1)
                    box.setSelected(!box.isSelected())
                }
              }
            },
          glue)
    })
  }

  val scrolls = sections map {
    case ((from, to), panels) =>
      ((if (data.inverted) from else to).getSummary,
        new JScrollPane(hcenter(hlock(vertical(vlock(vertical(panels.flatMap(List(_, gap(10))): _*)), glue)))))
  }

  val tabs = new JTabbedPane

  scrolls foreach { case (t, s) => tabs.addTab(t, s) }

  val panel = vertical(tabs, vlock(navigation.panel))

  def process(b: Button) = {
    b.disable

    val job = model map {
      case (movement, list) =>
        (list filter { _._1.isSelected } map { _._2 }) -> movement
    }

    data.agent.moveEvents(job).onComplete {
      case Success(_) => pop(3)
      case Failure(_) => pop
    }
  }

}