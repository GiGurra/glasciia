package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx
import com.github.gigurra.glasciia.AudioPlayer.{Music, Sound, SoundInstance, SoundLoopInstance}
import com.github.gigurra.glasciia.Glasciia._

import scala.collection.mutable
import scala.concurrent.Promise
import scala.util.Random

class AudioPlayer(private var _soundVolume: Float = 0.50f,
                  private var _musicVolume: Float = 0.35f) extends Logging {

  private val loadedSoundFiles = new mutable.HashMap[String, gdx.audio.Sound]
  private val loadedMusicFiles = new mutable.HashMap[String, gdx.audio.Music]
  private val loopingSoundLkup = new mutable.HashMap[Long, SoundLoopInstance]
  private var musicPlaylist = Vector[Music]()

  private def loadNewSound(soundName: String): gdx.audio.Sound = {
    Gdx.audio.newSound(soundName)
  }

  private def loadNewMusic(musicName: String): gdx.audio.Music = {
    val m = Gdx.audio.newMusic(musicName)
    m.setVolume(musicVolume)
    m
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

  def playList: Vector[String] = {
    musicPlaylist.map(_.name)
  }

  def currentPlaylistItem: Option[String] = {
    musicPlaylist.find(_.playing).map(_.name)
  }

  def currentPlaylistItemIndex: Option[Int] = {
    Option(musicPlaylist.indexWhere(_.playing)).filter(_ != -1)
  }

  def soundVolume(newVolume: Float): this.type = {
    for (sound <- loopingSoundLkup.values) {
      sound.volume(math.max(1.0f, sound.volume * newVolume / soundVolume))
    }
    _soundVolume = newVolume
    this
  }

  def musicVolume(newVolume: Float): this.type = {
    for (music <- loadedMusicFiles.values) {
      music.setVolume(math.max(1.0f, music.getVolume * newVolume / musicVolume))
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
    Music(
      initVolume = musicVolume,
      name = name,
      gdxMusic = loadedMusicFiles(name),
      player = this
    )
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

  def stopMusic(): this.type = {
    musicPlaylist.foreach(_.stop())
    musicPlaylist = Vector.empty
    this
  }

  def stop(): this.type = {
    stopMusic()
    stopLoopSounds()
    this
  }

  def nextMusic(): this.type = {
    val iCur = musicPlaylist.indexWhere(_.playing)
    if (iCur != -1) {
      changeCurrentPlaylistItemTo((iCur + 1)%musicPlaylist.length)
    } else {
      restartPlaylist()
    }
    this
  }

  def prevMusic(): this.type = {
    val iCur = musicPlaylist.indexWhere(_.playing)
    if (iCur != -1) {
      changeCurrentPlaylistItemTo((iCur - 1 + musicPlaylist.length)%musicPlaylist.length)
    } else {
      restartPlaylist()
    }
    this
  }

  def changeCurrentPlaylistItemTo(i: Int): this.type = {
    if (i>= 0 && i < musicPlaylist.length) {
      stopMusic()
      musicPlaylist(i).play()
    } else {
      log.error(s"Cannot change music to index $i - outside playlist size (=${musicPlaylist.size})!")
    }

    this
  }

  def changeCurrentPlaylistItemTo(name: String): this.type = {
    val i = musicPlaylist.indexWhere(_.name == name)
    if (i != -1) {
      changeCurrentPlaylistItemTo(i)
    } else {
      log.error(s"Cannot change music to $name - no such music in playlist!")
    }
    this
  }

  def restartPlaylist(): this.type = {
    if (musicPlaylist.nonEmpty) {
      changeCurrentPlaylistItemTo(0)
    }
    this
  }

  def changeToRandomMusic(): this.type = {
    if (musicPlaylist.size == 1) {
      restartPlaylist()
    } else if (musicPlaylist.size > 1) {
      for {
        iCur <- currentPlaylistItemIndex
      } {
        val validIndices = (musicPlaylist.indices.toBuffer - iCur).toVector
        val validMusics = validIndices.map(musicPlaylist.apply)
        pickRandom(validMusics).play()
      }
    }
    this
  }

  def setPlayList(trackNames: Vector[String],
                  shuffle: Boolean = false,
                  volume: Float = musicVolume): this.type = {

    // Stop prevous music
    stopMusic()

    if (trackNames.nonEmpty) {

      trackNames.foreach(ensureLoadedMusic)
      musicPlaylist = trackNames.map(music)
      musicPlaylist.foreach(_.volume(volume))
      musicPlaylist.zipWithIndex.foreach {
        case (item, i) =>
          item.onComplete{
            if (shuffle) {
              pickRandom((musicPlaylist.toBuffer - item).toVector).play()
            } else {
              musicPlaylist((i + 1) % musicPlaylist.length).play()
            }
          }
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

  private def removeMusic(name: String): Unit = {
    loadedMusicFiles.remove(name)
  }
}

object AudioPlayer {

  case class Music(initVolume: Float,
                   name: String,
                   private val gdxMusic: gdx.audio.Music,
                   private val player: AudioPlayer) {

    // API below just copied from gdx.audio.Music

    /** Starts the play back of the music stream. In case the stream was paused this will resume the play back. In case the music
      * stream is finished playing this will restart the play back. */
    def play(): Music = {
      gdxMusic.play()
      this
    }

    /** Pauses the play back. If the music stream has not been started yet or has finished playing a call to this method will be
      * ignored. */
    def pause(): Music = {
      gdxMusic.pause()
      this
    }

    /** Stops a playing or paused Music instance. Next time play() is invoked the Music will start from the beginning. */
    def stop(): Music = {
      gdxMusic.stop()
      this
    }

    /** @return whether this music stream is playing */
    def playing: Boolean = {
      gdxMusic.isPlaying
    }

    /** Sets whether the music stream is looping. This can be called at any time, whether the stream is playing.
      *
      * @param isLooping whether to loop the stream */
    def loop(isLooping: Boolean): Music = {
      gdxMusic.setLooping(isLooping)
      this
    }

    /** @return whether the music stream is playing. */
    def looping: Boolean = {
      gdxMusic.isLooping
    }

    /** Sets the volume of this music stream. The volume must be given in the range [0,1] with 0 being silent and 1 being the
      * maximum volume.
      */
    def volume(volume: Float): Music = {
      gdxMusic.setVolume(volume)
      this
    }

    /** @return the volume of this music stream. */
    def volume: Float = {
      gdxMusic.getVolume
    }

    /** Sets the panning and volume of this music stream.
      *
      * @param pan    panning in the range -1 (full left) to 1 (full right). 0 is center position.
      * @param volume the volume in the range [0,1]. */
    def pan(pan: Float, volume: Float): Music = {
      gdxMusic.setPan(pan, volume)
      this
    }

    /** Set the playback position in seconds. */
    def position(position: Float): Music = {
      gdxMusic.setPosition(position)
      this
    }

    /** Returns the playback position in seconds. */
    def position: Float = {
      gdxMusic.getPosition
    }

    /** Needs to be called when the Music is no longer needed. */
    def dispose(): Unit = {
      gdxMusic.dispose()
      player.removeMusic(name)
    }

    /** Register a callback to be invoked when the end of a music stream has been reached during playback.
      * */
    def onComplete(f: => Unit): Unit = {
      gdxMusic.setOnCompletionListener(new gdx.audio.Music.OnCompletionListener {
        override def onCompletion(music: gdx.audio.Music): Unit = {
          f
        }
      })
    }
  }

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