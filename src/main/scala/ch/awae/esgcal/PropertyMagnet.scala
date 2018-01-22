package ch.awae.esgcal

trait PropertyMagnet[T] {
  def get(source: String): Option[T]
}

object PropertyMagnet {

  implicit object StringMagnet extends PropertyMagnet[String] {
    def get(source: String) = Option(source)
  }

  implicit object IntMagnet extends PropertyMagnet[Int] {
    def get(source: String) =
      try {
        Some(source.toInt)
      } catch {
        case _: NullPointerException  => None
        case _: NumberFormatException => None
      }
  }

}