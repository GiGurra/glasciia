package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music.OnCompletionListener
import com.badlogic.gdx
import com.github.gigurra.glasciia.AudioPlayer.{Sound, SoundInstance, SoundLoopInstance}
import com.github.gigurra.glasciia.Glasciia._

import scala.collection.mutable
import scala.concurrent.Promise
import scala.util.Random

class AudioPlayer extends Logging {
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

  def sound(name: String, volume: Float = 0.5f): Sound = {
    ensureLoadedSound(name)
    Sound(
      gdxSound = loadedSoundFiles(name),
      initVolume = volume,
      player = this
    )
  }

  def getLoopSound(instanceId: Long): Option[SoundLoopInstance] = {
    loopingSoundLkup.get(instanceId)
  }

  def playOneOf(soundNames: Vector[String], volume: Float = 0.5f): SoundInstance = {
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

    def stop(): SoundLoopInstance = {
      if (!stopped) {
        gdxSound.stop(instanceId)
        stopPromise.success(())
      }
      this
    }

    def pause(): SoundLoopInstance = {
      gdxSound.pause(instanceId)
      this
    }

    def resume(): SoundLoopInstance = {
      assume(!stopPromise.isCompleted, "Cannot resume a stopped sound. Use pause instead")
      gdxSound.resume(instanceId)
      this
    }

    def stopped: Boolean = {
      stopPromise.isCompleted
    }

    def playing: Boolean = {
      !stopped
    }

    def volume: Float = {
      _currentVolume
    }

    def pan(pan: Float, volume: Float): SoundLoopInstance = {
      _currentVolume = volume
      gdxSound.setPan(instanceId, pan, volume.toFloat)
      this
    }

    def pitch(state: Float): SoundLoopInstance = {
      gdxSound.setPitch(instanceId, state)
      this
    }

    def volume(volume: Float): SoundLoopInstance = {
      _currentVolume = volume
      gdxSound.setVolume(instanceId, volume)
      this
    }

    def onStop(f: => Unit): Unit = {
      stopPromise.future.onComplete(_ => f)(SameThreadExecutionContext)
    }
  }

}