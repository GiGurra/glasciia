package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music.OnCompletionListener
import com.badlogic.gdx
import com.badlogic.gdx.audio.Music
import com.github.gigurra.glasciia.AudioPlayer.{Sound, SoundInstance, SoundLoopInstance}
import com.github.gigurra.glasciia.Glasciia._

import scala.collection.mutable
import scala.concurrent.Promise
import scala.util.Random

class AudioPlayer(private var _soundVolume: Float = 0.50f,
                  private var _musicVolume: Float = 0.35f) extends Logging {

  private val loadedSoundFiles = new mutable.HashMap[String, gdx.audio.Sound]
  private val loadedMusicFiles = new mutable.HashMap[String, gdx.audio.Music]
  private val loopingSoundLkup = new mutable.HashMap[Long, SoundLoopInstance]
  private var musicPlaylist = Vector[gdx.audio.Music]()

  private def loadNewSound(soundName: String): gdx.audio.Sound = {
    Gdx.audio.newSound(soundName)
  }

  private def loadNewMusic(musicName: String): gdx.audio.Music = {
    Gdx.audio.newMusic(musicName)
  }

  def ensureLoadedSound(soundName: String): Unit = {
    loadedSoundFiles.getOrElseUpdate(soundName, loadNewSound(soundName))
  }

  def ensureLoadedMusic(musicName: String): Unit = {
    loadedMusicFiles.getOrElseUpdate(musicName, loadNewMusic(musicName))
  }

  def soundVolume: Float = {
    _soundVolume
  }

  def musicVolume: Float = {
    _musicVolume
  }

  def soundVolume(newVolume: Float): this.type = {
    for (sound <- loopingSoundLkup.values) {
      sound.volume(math.max(1.0f, sound.volume * newVolume / soundVolume))
    }
    _soundVolume = newVolume
    this
  }

  def musicVolume(newVolume: Float): this.type = {
    for (song <- musicPlaylist) {
      song.setVolume(math.max(1.0f, song.getVolume * newVolume / musicVolume))
    }
    _musicVolume = newVolume
    this
  }

  def sound(name: String, volume: Float = soundVolume): Sound = {
    ensureLoadedSound(name)
    Sound(
      gdxSound = loadedSoundFiles(name),
      initVolume = volume,
      player = this
    )
  }

  /**
    * Use this to play longer items, but controlling the start and end yourself, such as recorded speech and dialogues
    */
  def music(name: String): Music = {
    ensureLoadedMusic(name)
    loadedMusicFiles(name)
  }

  def getLoopSound(instanceId: Long): Option[SoundLoopInstance] = {
    loopingSoundLkup.get(instanceId)
  }

  def playOneOf(soundNames: Vector[String], volume: Float = soundVolume): SoundInstance = {
    sound(pickRandom(soundNames), volume).play()
  }

  def stopLoopSounds(): Unit = {
    loopingSounds.values.foreach(_.stop())
  }

  def loopingSounds: Map[Long, SoundLoopInstance] = {
    loopingSoundLkup.toMap
  }

  def stopMusic(): Unit = {
    musicPlaylist.foreach(_.stop())
    musicPlaylist = Vector.empty
  }

  def stop(): this.type = {
    stopMusic()
    stopLoopSounds()
    this
  }

  def setPlayList(trackNames: Vector[String],
                  shuffle: Boolean = false,
                  volume: Float = musicVolume): this.type = {

    // Stop prevous sounds
    musicPlaylist.foreach(_.stop())

    if (trackNames.nonEmpty) {

      trackNames.foreach(ensureLoadedSound)
      musicPlaylist = trackNames.map(loadedMusicFiles.apply)
      musicPlaylist.foreach(_.setVolume(volume))
      musicPlaylist.zipWithIndex.foreach {
        case (item, i) =>
          item.setOnCompletionListener(new OnCompletionListener {
            override def onCompletion(music: gdx.audio.Music): Unit = {
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

    this
  }

  private def pickRandom[T](items: Vector[T]): T = {
    require(items.nonEmpty, s"Cannot pick a random element from an empty Seq")
    items(Random.nextInt(items.size))
  }

  private def putLooping(id: Long, instance: SoundLoopInstance): Unit = {
    loopingSoundLkup.put(id, instance)
  }

  private def removeLooping(id: Long): Unit = {
    loopingSoundLkup.remove(id)
  }
}

object AudioPlayer {

  case class Sound(initVolume: Float,
                   private val gdxSound: gdx.audio.Sound,
                   private val player: AudioPlayer) {

    def volume: Float = {
      initVolume
    }

    def play(): SoundInstance = {
      SoundInstance(
        initVolume = initVolume,
        gdxSound = gdxSound,
        instanceId = gdxSound.play(initVolume)
      )
    }

    def loop(): SoundLoopInstance = {
      val id = gdxSound.loop(initVolume)
      val instance = SoundLoopInstance(
        initVolume = initVolume,
        gdxSound = gdxSound,
        instanceId = id
      )
      player.putLooping(id, instance)
      instance.onStop(player.removeLooping(id))
      instance
    }
  }

  case class SoundInstance(initVolume: Float,
                           private val gdxSound: gdx.audio.Sound,
                           private val instanceId: Long) {

    private var _currentVolume: Float = initVolume

    def stop(): SoundInstance = {
      gdxSound.stop(instanceId)
      this
    }

    def volume: Float = {
      _currentVolume
    }

    def pause(): SoundInstance = {
      gdxSound.pause(instanceId)
      this
    }

    def resume(): SoundInstance = {
      gdxSound.resume(instanceId)
      this
    }

    def pan(pan: Float, volume: Float): SoundInstance = {
      _currentVolume = volume
      gdxSound.setPan(instanceId, pan, volume.toFloat)
      this
    }

    def pitch(state: Float): SoundInstance = {
      gdxSound.setPitch(instanceId, state)
      this
    }

    def volume(volume: Float): SoundInstance = {
      _currentVolume = volume
      gdxSound.setVolume(instanceId, volume)
      this
    }
  }

  case class SoundLoopInstance(initVolume: Float,
                               private val gdxSound: gdx.audio.Sound,
                               private val instanceId: Long) {

    private val stopPromise: Promise[Unit] = Promise[Unit]()
    private var _currentVolume: Float = initVolume

    def stopped: Boolean = {
      stopPromise.isCompleted
    }

    def volume: Float = {
      _currentVolume
    }

    def stop(): SoundLoopInstance = {
      if (!stopped) {
        gdxSound.stop(instanceId)
        stopPromise.success(())
      }
      this
    }

    def pause(): SoundLoopInstance = {
      if (!stopped) {
        gdxSound.pause(instanceId)
      }
      this
    }

    def resume(): SoundLoopInstance = {
      if (!stopped) {
        gdxSound.resume(instanceId)
      }
      this
    }

    def pan(pan: Float, volume: Float): SoundLoopInstance = {
      if (!stopped) {
        _currentVolume = volume
        gdxSound.setPan(instanceId, pan, volume.toFloat)
      }
      this
    }

    def pitch(state: Float): SoundLoopInstance = {
      if (!stopped) {
        gdxSound.setPitch(instanceId, state)
      }
      this
    }

    def volume(volume: Float): SoundLoopInstance = {
      if (!stopped) {
        _currentVolume = volume
        gdxSound.setVolume(instanceId, volume)
      }
      this
    }

    def onStop(f: => Unit): SoundLoopInstance = {
      stopPromise.future.onComplete(_ => f)(SameThreadExecutionContext)
      this
    }
  }

}