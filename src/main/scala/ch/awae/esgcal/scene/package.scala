package ch.awae.esgcal

package object scene {
  import java.awt.Color
  import java.awt.Component
  import java.awt.Dimension
  import javax.swing._
  import javax.swing.border.LineBorder

  def glue = Box.createGlue()

  def label(text: String) = new JLabel(text)
  
  def gap(size: Int) = Box.createRigidArea(new Dimension(size, size))

  def center[T <: JComponent](x: T): T = {
    x.setAlignmentX(Component.CENTER_ALIGNMENT)
    x.setAlignmentY(Component.CENTER_ALIGNMENT)
    x
  }

  def vlock[T <: Component](element: T): T = {
    element.setMaximumSize(new Dimension(element.getMaximumSize.width, element.getPreferredSize.height))
    element
  }

  def vertical(elements: Component*) = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS))
    elements foreach panel.add
    if (Globals.DEBUG) panel.setBorder(new LineBorder(Color.RED))
    panel
  }

  def horizontal(elements: Component*) = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS))
    elements foreach panel.add
    if (Globals.DEBUG) panel.setBorder(new LineBorder(Color.BLACK))
    panel
  }

  def hcenter(elements: Component*) = {
    val p = horizontal(glue :: elements.toList: _*)
    p add glue
    p
  }

  def vcenter(elements: Component*) = {
    val p = vertical(glue :: elements.toList: _*)
    p add glue
    p
  }

  def button(text: String, λ: Button => Unit) = {
    new Button(text)(λ).button
  }
}