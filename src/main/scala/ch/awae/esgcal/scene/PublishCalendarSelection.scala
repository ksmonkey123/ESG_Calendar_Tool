package ch.awae.esgcal.scene

import com.google.api.client.auth.oauth2.Credential
import ch.awae.esgcal.Scene
import javax.swing.JLabel
import java.util.Date
import ch.awae.esgcal.Navigation
import ch.awae.esgcal.Navigation._
import ch.awae.esgcal.PublishModel
import javax.swing.JCheckBox
import java.awt.Color
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

case class PublishCalendarSelection(data: PublishModel.SelectCalendars) extends Scene {

  val navigation = Navigation("Zurück", "Weiter") {
    case (LEFT, b) => pop
    case (RIGHT, b) => //push(PublishEventSelection(credential, invert))
  }

  val errorLabel = label(" ", Color.RED)

  val entries = data.calendars map { entry =>
    val box = new JCheckBox
    if (entry._2.size > 0)
      box setSelected !data.inverted
    else {
      box setEnabled false
    }
    box addActionListener new ActionListener {
      def actionPerformed(e: ActionEvent) = checkBoxes
    }
    (box, entry)
  }

  val elements = hlock(vlock(vertical(entries flatMap {
    case (box, ((from, to), events)) =>
      val color = if (events.size == 0) Color.GRAY else null
      horizontal(
        box,
        gap(20),
        vertical(
          vlock(label(s"von: ${from.getSummary} (${
            events.size match {
              case 0 => "keine Ereignisse"
              case 1 => "1 Ereignis"
              case x => s"$x Ereignisse"
            }
          })", color)),
          vlock(label(s"nach: ${to.getSummary}", color))),
        glue) :: gap(20) :: Nil
  }: _*)))

  val panel =
    vertical(
      vlock(
        hcenter(
          label(s"Publikation ${if (data.inverted) "widerrufen" else "erfassen"}"))),
      glue,
      elements,
      vlock(
        hcenter(
          errorLabel)),
      glue,
      navigation.panel)

  def checkBoxes: Unit = if ((entries find (_._1.isSelected)).isEmpty) {
    errorLabel setText "Mindestens einen Kalender auswählen!"
    navigation.right.setEnabled(false)
  } else {
    errorLabel setText " "
    navigation.right.setEnabled(true)
  }

  checkBoxes

}