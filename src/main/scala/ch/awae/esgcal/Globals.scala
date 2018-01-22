package ch.awae.esgcal

object Globals {

  val DEBUG: Boolean = false
  lazy val VERSION: String = Property("version")[String]

}