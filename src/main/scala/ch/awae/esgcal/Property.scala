package ch.awae.esgcal

import java.util.Properties

import ch.awae.esgcal.Implicit.UseCloseable

object Property {

  lazy val properties = {
    val inputStream = getClass.getClassLoader
      .getResourceAsStream("configuration.properties")

    val p = inputStream use { s =>
      val prop = new Properties
      prop.load(s)
      prop
    }
    println(s"loaded ${p.size} properties")
    p
  }

  class PropertyEntry(value: String) {
    def apply[T](implicit magnet: PropertyMagnet[T]) =
      magnet.get(value).get

    def apply[T](default: => T)(implicit magnet: PropertyMagnet[T]) =
      magnet.get(value).getOrElse(default)
  }

  def apply(key: String) = new PropertyEntry({
    val prop = properties.getProperty(key)
    println(s"loaded property: $key=$prop")
    prop
  })

}