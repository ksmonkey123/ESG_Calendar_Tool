package ch.awae.esgcal

object Util {

  implicit class List2IndexedList[T](val list: List[T]) extends AnyVal {

    def toIndexedList: List[(Int, T)] = {
      val size = list.size

      {
        for {
          i <- (0 until size)
        } yield {
          (i, list(i))
        }
      }.toList

    }

  }

}