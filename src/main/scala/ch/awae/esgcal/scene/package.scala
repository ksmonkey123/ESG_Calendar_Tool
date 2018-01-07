package ch.awae.esgcal

package object scene {
  import java.awt.Color
  import java.awt.Component
  import java.awt.Dimension
  import javax.swing._
  import javax.swing.border.LineBorder

  sealed trait Axis
  final object Axis {
    case object HORIZONTAL extends Axis
    case object VERTICAL extends Axis
  }

  def glue = Box.createGlue

  def label(text: String, color: Color = null) = {
    val l = new JLabel(text)
    if (color != null)
      l setForeground color
    if (Globals.DEBUG) {
      l setBackground Color.orange
      l setOpaque true
    }
    l
  }

  def gap(size: Int) =
    Box createRigidArea new Dimension(size, size)

  def center[T <: JComponent](x: T): T = {
    x setAlignmentX Component.CENTER_ALIGNMENT
    x setAlignmentY Component.CENTER_ALIGNMENT
    x
  }

  def vlock[T <: Component](element: T) = {
    element setMaximumSize new Dimension(element.getMaximumSize.width, element.getPreferredSize.height)
    element
  }

  def hlock[T <: Component](element: T) = {
    element setMaximumSize new Dimension(element.getPreferredSize.width, element.getMaximumSize.height)
    element
  }

  def panel(λάμδα: JPanel => Unit): JPanel = {
    val pane = new JPanel
    λάμδα(pane)
    pane
  }

  def panel(axis: Axis)(elements: Component*): JPanel = {
    import ch.awae.esgcal.scene.Axis._

    val pane = new JPanel
    pane setLayout new BoxLayout(pane, axis match {
      case HORIZONTAL => BoxLayout.LINE_AXIS
      case VERTICAL => BoxLayout.PAGE_AXIS
    })
    elements foreach pane.add
    // DEBUGGING HINTS
    if (Globals.DEBUG) pane setBorder new LineBorder(axis match {
      case HORIZONTAL => Color.BLACK
      case VERTICAL => Color.RED
    })
    pane
  }

  def vertical(elements: Component*) = panel(Axis.VERTICAL)(elements: _*)

  def horizontal(elements: Component*) = panel(Axis.HORIZONTAL)(elements: _*)

  def hcenter(elements: Component*) =
    horizontal(glue :: elements.toList ::: glue :: Nil: _*)

  def vcenter(elements: Component*) =
    vertical(glue :: elements.toList ::: glue :: Nil: _*)

  def button(text: String, λ: Button => Unit) = {
    new Button(text)(λ).button
  }
}