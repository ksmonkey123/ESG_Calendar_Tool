package ch.awae.esgcal

trait SceneManager {
  def push(scene: Scene)
  def pop(levels: Int)
}