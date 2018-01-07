package ch.awae.esgcal

case class Month(index: Int, text: String)(λ: Int => Int) {
  def dayCount(year: Int) = λ(year)
  override def toString = text
}

object Month {

  private val _31 = (x: Int) => 31
  private val _30 = (x: Int) => 30
  private val _feb: Int => Int = {
    case x if x % 1000 == 0 => 29
    case x if x % 100 == 0 => 28
    case x if x % 4 == 0 => 29
    case _ => 28
  }

  lazy val months = List[((Int, String), Int => Int)](
    0 -> "Januar" -> _31,
    1 -> "Februar" -> _feb,
    2 -> "März" -> _31,
    3 -> "April" -> _30,
    4 -> "Mai" -> _31,
    5 -> "Juni" -> _30,
    6 -> "Juli" -> _31,
    7 -> "August" -> _31,
    8 -> "September" -> _30,
    9 -> "Oktober" -> _31,
    10 -> "November" -> _30,
    11 -> "Dezember" -> _31) map { case ((i, t), λ) => Month(i, t)(λ) }
}
