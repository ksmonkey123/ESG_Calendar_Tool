package ch.awae.esgcal.ui

trait SceneManager {
  def push(scene: Scene)
  def pop(levels: Int)
  def popTo(level: Int)
  def getFileSaveLocation(file: String): Option[String]
}