package com.github.gigurra.glasciia.impl

import java.io.FileNotFoundException
import java.nio.charset.{Charset, StandardCharsets}

import com.badlogic.gdx.files.FileHandle

import scala.io.Codec
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-02.
  */
trait FileReadImplicits {

  implicit class FileHandleReadString(file: FileHandle) {
    def mkString(): String = mkString(StandardCharsets.UTF_8)
    def mkString(charset: Charset): String = {
      val stream = file.read()
      try {
        scala.io.Source.fromInputStream(stream)(Codec(charset)).mkString
      } finally {
        stream.close()
      }
    }
  }

  implicit def filePathToFileHandle(path: String): FileHandle = LoadFile(path).getOrElse(throw new FileNotFoundException(s"Could not find file: [$path]"))

}

object FileReadImplicits extends FileReadImplicits
