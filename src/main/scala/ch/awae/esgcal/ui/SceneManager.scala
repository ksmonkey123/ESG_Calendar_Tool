package ch.awae.esgcal.ui

trait SceneManager {
  def push(scene: Scene)
  def pop(levels: Int)
}