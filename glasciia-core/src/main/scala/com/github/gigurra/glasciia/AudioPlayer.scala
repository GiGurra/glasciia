package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music.OnCompletionListener
import com.badlogic.gdx.audio.{Music, Sound}
import com.github.gigurra.glasciia.Glasciia._

import scala.collection.mutable
import scala.util.Random

class AudioPlayer extends Logging {

  private val loadedSoundFiles = new mutable.HashMap[String, Sound]
  private val loadedMusicFiles = new mutable.HashMap[String, Music]
  private val loopingSounds = new mutable.HashMap[Long, SoundInstance]
  private var musicPlaylist = Vector[Music]()

  private def loadNewSound(soundName: String): Sound = {
    Gdx.audio.newSound(soundName)
  }

  private def loadNewMusic(musicName: String): Music = {
    Gdx.audio.newMusic(musicName)
  }

  def loadSound(soundName: String): Sound = {
    loadedSoundFiles.getOrElseUpdate(soundName, loadNewSound(soundName))
  }

  def loadMusic(musicName: String): Music = {
    loadedMusicFiles.getOrElseUpdate(musicName, loadNewMusic(musicName))
  }

  def playSound(soundName: String, volume: Float = 0.5f): SoundInstance = {
    val sound = loadSound(soundName)
    val instance = sound.play(volume)
    SoundInstance(sound, instance, volume, this)
  }

  def loopSound(soundName: String, volume: Float = 0.5f): SoundInstance = {
    val instance = playSound(soundName = soundName, volume = volume).setLooping()
    loopingSounds.put(instance.instanceId, instance)
    instance
  }

  def getLoopSound(instanceId: Long): Option[SoundInstance] = {
    loopingSounds.get(instanceId)
  }

  def stopLoopSound(instanceId: Long): Unit = {
    loopingSounds.remove(instanceId).foreach(_.stop())
  }

  def playOneOf(soundNames: Vector[String], volume: Float = 0.5f): SoundInstance = {
    playSound(pickRandom(soundNames), volume)
  }

  def stopLoopSounds(): Unit = {
    loopingSounds.values.foreach(_.stop())
    loopingSounds.clear()
  }

  def stopMusic(): Unit = {
    musicPlaylist.foreach(_.stop())
    musicPlaylist = Vector.empty
  }

  def stop(): Unit = {
    stopMusic()
    stopLoopSounds()
  }

  def setPlayList(trackNames: Vector[String],
                  shuffle: Boolean = false,
                  volume: Float = 0.25f): Unit = {

    // Stop prevous sounds
    musicPlaylist.foreach(_.stop())

    if (trackNames.nonEmpty) {

      musicPlaylist = trackNames.map(loadMusic)
      musicPlaylist.foreach(_.setVolume(volume))
      musicPlaylist.zipWithIndex.foreach {
        case (item, i) =>
          item.setOnCompletionListener(new OnCompletionListener {
            override def onCompletion(music: Music): Unit = {
              if (shuffle) {
                pickRandom((musicPlaylist.toBuffer - music).toVector).play()
              } else {
                musicPlaylist((i + 1) % musicPlaylist.length).play()
              }
            }
          })
      }
      if (shuffle) {
        pickRandom(musicPlaylist).play()
      } else {
        musicPlaylist.head.play()
      }
    } else {
      musicPlaylist = Vector.empty
    }
  }

  private def pickRandom[T](items: Vector[T]): T = {
    require(items.nonEmpty, s"Cannot pick a random element from an empty Seq")
    items(Random.nextInt(items.size))
  }

  private[glasciia] def ensureLoopedSoundRemoved(id: Long): Unit = {
    loopingSounds.remove(id)
  }
}

case class SoundInstance(sound: Sound,
                         instanceId: Long,
                         initVolume: Float,
                         private val player: AudioPlayer) {

  private var _currentVolume: Float = initVolume

  def stop(): SoundInstance = {
    sound.stop(instanceId)
    player.ensureLoopedSoundRemoved(instanceId)
    this
  }

  def pause(): SoundInstance = {
    sound.pause(instanceId)
    this
  }

  def resume(): SoundInstance = {
    sound.resume(instanceId)
    this
  }

  def volume: Float = {
    _currentVolume
  }

  def setLooping(state: Boolean = true): SoundInstance = {
    sound.setLooping(instanceId, state)
    this
  }

  def setPan(pan: Float, volume: Float): SoundInstance = {
    _currentVolume = volume
    sound.setPan(instanceId, pan, volume.toFloat)
    this
  }

  def setPitch(state: Float): SoundInstance = {
    sound.setPitch(instanceId, state)
    this
  }

  def setVolume(volume: Float): SoundInstance = {
    _currentVolume = volume
    sound.setVolume(instanceId, volume)
    this
  }
}
