package ch.awae.esgcal.ui

import javax.swing.JButton
import ch.awae.esgcal.ui.FunctionalActionListeners._
import ch.awae.esgcal.Implicit.ArbitraryObjectPipelining

class Button(text: String)(λ: Button => Unit) {
  val button = new JButton(text) Λ { _ addActionListener { () => λ(Button.this) } }

  def enable = button setEnabled true
  def disable = button setEnabled false
}