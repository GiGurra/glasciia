package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.{BitmapFont, NinePatch, ParticleEffect, ParticleEmitter, TextureAtlas, TextureRegion, Animation => GdxAnimation}
import com.badlogic.gdx.graphics.{Cursor, Texture, TextureAccess}
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.github.gigurra.glasciia._
import com.github.gigurra.glasciia.impl.ResourceManager.{ExplicitTypeRequired, Resource}

import scala.annotation.implicitNotFound
import scala.collection.mutable
import scala.language.existentials
import scala.reflect.Manifest

/**
  * Created by johan on 2016-10-01.
  */
trait ResourceManager { self: App =>

  def contextLossHandler: PartialFunction[Any, Seq[Texture]] = defaultContextLossHandler

  def addResource[T : Manifest : ExplicitTypeRequired](path: String, ctor: => T, closer: T => Unit = (x: T) => ())(implicit a: T =:= T): Unit = executeOnRenderThread {
    val resource = ctor
    resources.put(path, Resource(path, resource, () => closer(resource), implicitly[Manifest[T]])).foreach(_.close())
  }

  def getResource[T: Manifest : ExplicitTypeRequired](path: String): Option[T] = doGetResource(path).map {
    case resource: T => resource
    case resource => throw new ClassCastException(s"Resource of incorrect type (exp: ${implicitly[Manifest[T]]}, actual: ${resource.getClass}")
  }

  def resource[T : Manifest : ExplicitTypeRequired](path: String): T = {
    getResource[T](path) match {
      case Some(resource) => resource
      case None => throw new NoSuchElementException(s"No resource stored on path '$path'")
    }
  }

  def listResources: Seq[Resource] = {
    resources.values.toArray[Resource]
  }

  def texturesReferencedBy(resource: Any): Seq[Texture] = {
    contextLossHandler.applyOrElse(resource, (_: Any) => {
      // Some warning perhaps when there exists no handler?
      System.err.println(s"Don't know how to handle context loss for, $resource, ignoring")
      Nil
    })
  }

  /**
    * Call if managing assets manually (e.g. if you have dynamic resources) when you receive a Resume event
    * Calling this any other time results in a memory leak (gpu side)
    */
  def reloadTexturesAfterContextLoss(): Unit = {
    val uniqueTexturesToBeReloaded = new mutable.HashSet[Texture]()
    for (resource <- listResources) {
      val textures = texturesReferencedBy(resource)
      uniqueTexturesToBeReloaded ++= textures
    }
    for (texture <- uniqueTexturesToBeReloaded) {
      TextureAccess.reloadOnContextLoss(texture)
    }
  }




  /////////////////////////////////////////////
  // Expectations

  def executeOnRenderThread(f: => Unit): Unit


  /////////////////////////////////////////////
  // Private
  import scala.collection.JavaConversions._

  private val defaultContextLossHandler: PartialFunction[Any, Seq[Texture]] = {
    case x: Resource => texturesReferencedBy(x.data)
    case x: Texture => Seq(x)
    case x: TextureRegion => Seq(x.getTexture)
    case x: GdxAnimation => x.getKeyFrames.map(_.getTexture)
    case x: Animation => texturesReferencedBy(x.animation : GdxAnimation)
    case x: Animation.Instance => texturesReferencedBy(x.animation : Animation)
    case x: TextureAtlas => x.getTextures.toSeq
    case x: InMemoryLoader[_] => x.fallback.toSeq.flatMap(texturesReferencedBy)
    case x: AtlasTextureRegionLoader => texturesReferencedBy(x.atlas)
    case x: DynamicTextureAtlas => x.getTextures.toSeq
    case x: NinePatch => Seq(x.getTexture)
    case x: ParticleSource => x.getEmitters.flatMap(texturesReferencedBy).toSeq
    case x: ParticleEmitter => Seq(x.getSprite.getTexture)
    case x: ParticleEffect => x.getEmitters.flatMap(texturesReferencedBy).toSeq
    case x: BitmapFont => x.getRegions.map(_.getTexture).toSeq
    case x: MultiLayer[_] => x.layers.flatMap(texturesReferencedBy)
    case x: Layer[_] => x.pieces.flatMap(texturesReferencedBy)
    case x: Piece[_] => texturesReferencedBy(x.image)
    case x: Cursor => Seq.empty // Cursors don't exist on touch devices where context loss can occur. No factor
    // To handle skins, actors, guis, you should let them depend on resources in this ResourceManager, and not allocate any free floating textures of their own
    case x: Stage => Seq.empty
    case x: Actor => Seq.empty
    case x: Skin => Seq.empty
  }

  private def doGetResource(path: String): Option[Any] = {
    resources.get(path).map(_.data)
  }

  private val resources = new scala.collection.concurrent.TrieMap[String, Resource]
}

object ResourceManager {
  case class Resource(path: String, data: Any, closer: () => Unit, manifest: Manifest[_]) {
    def close() = closer()
    def class_ : Class[_] = manifest.runtimeClass
    override def toString: String = {
      def toStringManifest(m: Manifest[_]): String = {
        val simpleName = m.runtimeClass.getSimpleName
        m.typeArguments match {
          case Nil => simpleName
          case typeArgs => s"$simpleName[${m.typeArguments.map(toStringManifest).mkString(", ")}]"
        }
      }
      s"$path: ${toStringManifest(manifest)}"
    }

  }

  @implicitNotFound("Explicit typing required for accessing resources")
  trait ExplicitTypeRequired[T]

  object ExplicitTypeRequired {
    private val evidence: ExplicitTypeRequired[Any] = new Object with ExplicitTypeRequired[Any]

    implicit def notNothingEv[T](implicit n: T =:= T): ExplicitTypeRequired[T] =
      evidence.asInstanceOf[ExplicitTypeRequired[T]]
  }
}
