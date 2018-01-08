package ch.awae.esgcal.scene

import java.awt.Color
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import javax.swing.JCheckBox

import ch.awae.esgcal.Navigation
import ch.awae.esgcal.Navigation._
import ch.awae.esgcal.PublishModel
import ch.awae.esgcal.PublishModel.SelectEvents
import ch.awae.esgcal.Scene
import ch.awae.esgcal.FunctionalActionListeners._

case class PublishCalendarSelection(data: PublishModel.SelectCalendars) extends Scene {

  val navigation = Navigation("Zurück", "Weiter") {
    case (LEFT, b)  => pop
    case (RIGHT, b) => push(PublishEventSelection(SelectEvents(data.agent, data.inverted, selectedCalendars)))
  }

  def selectedCalendars = for {
    (box, model) <- entries
    if box.isSelected
  } yield model

  val errorLabel = label(" ", Color.RED)

  val entries = data.calendars map { entry =>
    val box = new JCheckBox
    if (entry._2.size > 0)
      box setSelected !data.inverted
    else {
      box setEnabled false
    }
    box addActionListener { () => checkBoxes }
    (box, entry)
  }

  val elements = hlock(vlock(vertical(entries flatMap {
    case (box, ((from, to), events)) =>
      val color = if (events.size == 0) Color.GRAY else null
      horizontal(
        box, gap(20), vertical(vlock(label(s"von: ${from.getSummary} (${
          events.size match {
            case 0 => "keine Ereignisse"
            case 1 => "1 Ereignis"
            case n => s"$n Ereignisse"
          }
        })", color)), vlock(label(s"nach: ${to.getSummary}", color))),
        glue) :: gap(20) :: Nil
  }: _*)))

  val panel =
    vertical(
      vlock(hcenter(label(s"Publikation ${if (data.inverted) "widerrufen" else "erfassen"}"))),
      glue, elements, vlock(hcenter(errorLabel)), glue, navigation.panel)

  def checkBoxes: Unit = if ((entries find (_._1.isSelected)).isEmpty) {
    errorLabel setText "Mindestens einen Kalender auswählen!"
    navigation.right.setEnabled(false)
  } else {
    errorLabel setText " "
    navigation.right.setEnabled(true)
  }

  checkBoxes

}