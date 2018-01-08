package ch.awae.esgcal

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import javax.swing.JButton

import ch.awae.esgcal.Implicit.ArbitraryObjectPipelining

class Button(text: String)(λ: Button => Unit) {

  val button = new JButton(text) Λ {
    _ addActionListener new ActionListener {
      def actionPerformed(action: ActionEvent) = λ(Button.this)
    }
  }

  def enable = button setEnabled true
  def disable = button setEnabled false

}