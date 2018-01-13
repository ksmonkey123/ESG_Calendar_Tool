package ch.awae.esgcal.ui

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

object FunctionalActionListeners {

  implicit class UnitActionListener(val λ: () => Unit) extends ActionListener {
    def actionPerformed(e: ActionEvent) = λ()
  }

}