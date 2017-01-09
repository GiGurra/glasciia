package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx
import com.github.gigurra.glasciia.AudioPlayer.{Music, Sound, SoundLoopInstance}
import com.github.gigurra.glasciia.Glasciia._

import scala.collection.mutable
import scala.util.Random

class AudioPlayer(private var _soundVolume: Float = 0.50f,
                  private var _musicVolume: Float = 0.35f) extends Logging {

  private val loadedSoundFiles = new mutable.HashMap[String, Sound]
  private val loadedMusicFiles = new mutable.HashMap[String, Music]
  private val loopingSoundLkup = new mutable.HashMap[Long, SoundLoopInstance]
  private var musicPlaylist = Vector[Music]()
  private var _soundMuted: Boolean = false
  private var _musicMuted: Boolean = false

  private def loadNewSound(soundName: String): Sound = {
    new Sound(
      name = soundName,
      gdxSound = Gdx.audio.newSound(soundName),
      player = this
    )
  }

  private def loadNewMusic(musicName: String): Music = {
    new Music(
      name = musicName,
      gdxMusic = Gdx.audio.newMusic(musicName),
      player = this
    )
  }

  def ensureLoadedSound(soundName: String): Unit = {
    loadedSoundFiles.getOrElseUpdate(soundName, loadNewSound(soundName))
  }

  def ensureLoadedMusic(musicName: String): Unit = {
    loadedMusicFiles.getOrElseUpdate(musicName, loadNewMusic(musicName))
  }

  def muteMusic(state: Boolean = true): this.type = {
    _musicMuted = state
    flushMusicVolume()
    this
  }

  def muteSound(state: Boolean = true): this.type = {
    _soundMuted = state
    flushSoundVolume()
    this
  }

  def soundMuted: Boolean = {
    _soundMuted
  }

  def musicMuted: Boolean = {
    _musicMuted
  }

  def mute(state: Boolean = true): this.type = {
    muteSound(state)
    muteMusic(state)
    this
  }

  def soundVolume: Float = {
    if (soundMuted) 0.0f else _soundVolume
  }

  def musicVolume: Float = {
    if (musicMuted) 0.0f else _musicVolume
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
    _soundVolume = newVolume
    flushSoundVolume()
    this
  }

  def musicVolume(newVolume: Float): this.type = {
    _musicVolume = newVolume
    flushMusicVolume()
    this
  }

  def flushMusicVolume(): this.type = {
    for (music <- loadedMusicFiles.values) {
      music.flushVolume()
    }
    this
  }

  def flushSoundVolume(): this.type = {
    for (sound <- loopingSoundLkup.values) {
      sound.flushVolume()
    }
    this
  }

  def flushVolume(): this.type = {
    flushSoundVolume()
    flushMusicVolume()
    this
  }

  def sound(name: String): Sound = {
    ensureLoadedSound(name)
    loadedSoundFiles(name)
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

  def oneOf(soundNames: Vector[String]): Sound = {
    sound(pickRandom(soundNames))
  }

  def stopLoopSounds(): Unit = {
    loopingSounds.values.foreach(_.stop())
  }

  def loopingSounds: Map[Long, SoundLoopInstance] = {
    loopingSoundLkup.toMap
  }

  def stopMusic(clearPlaylist: Boolean): this.type = {
    musicPlaylist.foreach(_.stop())
    if (clearPlaylist) {
      musicPlaylist = Vector.empty
    }
    this
  }

  def stop(clearPlaylist: Boolean): this.type = {
    stopMusic(clearPlaylist)
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
      stopMusic(clearPlaylist = false)
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
      val iCur = currentPlaylistItemIndex.getOrElse(-1)
      val validIndices = (musicPlaylist.indices.toBuffer - iCur).toVector
      changeCurrentPlaylistItemTo(pickRandom(validIndices))
    }
    this
  }

  def setPlayList(trackNames: Vector[String],
                  shuffle: Boolean = false,
                  unscaledVolume: Float = 1.0f): this.type = {

    // Stop prevous music
    stopMusic(clearPlaylist = true)

    if (trackNames.nonEmpty) {

      trackNames.foreach(ensureLoadedMusic)
      musicPlaylist = trackNames.map(music)
      musicPlaylist.foreach(_.unscaledVolume(unscaledVolume))
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

  class Music(val name: String,
              gdxMusic: gdx.audio.Music,
              player: AudioPlayer) {


    // API below just copied from gdx.audio.Music
    private var _unscaledVolume: Float = 1.0f

    def play(unscaledVolume: Float = _unscaledVolume): Music = {
      _unscaledVolume = unscaledVolume
      flushVolume()
      gdxMusic.play()
      this
    }

    def flushVolume(): Unit = {
      gdxMusic.setVolume(scaledVolume)
    }

    def pause(): Music = {
      gdxMusic.pause()
      this
    }

    def stop(): Music = {
      gdxMusic.stop()
      this
    }

    def playing: Boolean = {
      gdxMusic.isPlaying
    }

    def loop(isLooping: Boolean): Music = {
      gdxMusic.setLooping(isLooping)
      this
    }

    def looping: Boolean = {
      gdxMusic.isLooping
    }

    def unscaledVolume(newValue: Float): Music = {
      _unscaledVolume = newValue
      flushVolume()
      this
    }

    def unscaledVolume: Float = {
      _unscaledVolume
    }

    def scaledVolume: Float = {
      unscaledVolume * player.musicVolume
    }

    def pan(pan: Float): Music = {
      gdxMusic.setPan(pan, scaledVolume)
      this
    }

    def position(position: Float): Music = {
      gdxMusic.setPosition(position)
      this
    }

    def position: Float = {
      gdxMusic.getPosition
    }

    def dispose(): Unit = {
      gdxMusic.dispose()
      player.removeMusic(name)
    }

    def onComplete(f: => Unit): Unit = {
      gdxMusic.setOnCompletionListener(new gdx.audio.Music.OnCompletionListener {
        override def onCompletion(music: gdx.audio.Music): Unit = {
          f
        }
      })
    }
  }

  class Sound(val name: String,
              gdxSound: gdx.audio.Sound,
              player: AudioPlayer) {

    def play(unscaledVolume: Float = 1.0f): SoundInstance = {

      val id = gdxSound.play(player.soundVolume * unscaledVolume)

      new SoundInstance(
        name = name,
        initUnscaledVolume = unscaledVolume,
        gdxSound = gdxSound,
        instanceId = id,
        player = player
      )
    }

    def loop(unscaledVolume: Float = 1.0f): SoundLoopInstance = {

      val id = gdxSound.loop(player.soundVolume * unscaledVolume)

      val instance = new SoundLoopInstance(
        name = name,
        initUnscaledVolume = unscaledVolume,
        gdxSound = gdxSound,
        instanceId = id,
        player = player
      )

      player.putLooping(id, instance)
      instance.onStop(player.removeLooping(id))
      instance
    }
  }

  class SoundInstance(val name: String,
                      initUnscaledVolume: Float,
                      gdxSound: gdx.audio.Sound,
                      instanceId: Long,
                      player: AudioPlayer) {

    private var _unscaledVolume: Float = initUnscaledVolume

    def unscaledVolume: Float = {
      _unscaledVolume
    }

    def unscaledVolume(newValue: Float): this.type = {
      _unscaledVolume = newValue
      flushVolume()
      this
    }

    def flushVolume(): this.type = {
      gdxSound.setVolume(instanceId, scaledVolume)
      this
    }

    def scaledVolume: Float = {
      unscaledVolume * player.soundVolume
    }

    def stop(): this.type = {
      gdxSound.stop(instanceId)
      this
    }

    def pause(): this.type = {
      gdxSound.pause(instanceId)
      this
    }

    def resume(): this.type = {
      gdxSound.resume(instanceId)
      this
    }

    def pan(pan: Float): this.type = {
      gdxSound.setPan(instanceId, pan, scaledVolume)
      this
    }

    def pitch(state: Float): this.type = {
      gdxSound.setPitch(instanceId, state)
      this
    }
  }

  class SoundLoopInstance(name: String,
                          initUnscaledVolume: Float,
                          gdxSound: gdx.audio.Sound,
                          instanceId: Long,
                          player: AudioPlayer) extends SoundInstance(name, initUnscaledVolume, gdxSound, instanceId, player) {

    private val stopPromise: SameThreadPromise[Unit] = SameThreadPromise[Unit]()

    def stopped: Boolean = {
      stopPromise.isCompleted
    }

    override def stop(): this.type = {
      super.stop()
      stopPromise.success(())
      this
    }

    def onStop(f: => Unit): this.type = {
      stopPromise.future.onComplete(_ => f)
      this
    }
  }
}
