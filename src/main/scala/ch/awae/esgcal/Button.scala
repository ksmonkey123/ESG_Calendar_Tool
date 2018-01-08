package ch.awae.esgcal

import javax.swing.JButton

import ch.awae.esgcal.FunctionalActionListeners._
import ch.awae.esgcal.Implicit.ArbitraryObjectPipelining

class Button(text: String)(λ: Button => Unit) {
  val button = new JButton(text) Λ { _ addActionListener { () => λ(Button.this) } }

  def enable = button setEnabled true
  def disable = button setEnabled false
}