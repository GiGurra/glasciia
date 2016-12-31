package com.github.gigurra.glasciia

import java.util.UUID

import org.scalatest.mock.MockitoSugar
import org.scalatest.Matchers
import org.scalatest.OneInstancePerTest
import org.scalatest.WordSpec
import scala.language.reflectiveCalls

class ResourceLoaderSpec extends WordSpec
  with MockitoSugar
  with Matchers
  with OneInstancePerTest {

  def uuid(s: String): UUID = UUID.fromString(s)

  "ResourceLoader" should {

    "Load as many resources as possible within a given time (1)" in {

      val dut = new Resources {
        var nItems = 0
        override protected def loadSome(): Boolean = {
          Thread.sleep(200)
          nItems += 1
          nItems >= 10
        }
      }

      dut.load(maxTimeMillis = 100)
      dut.nItems shouldBe 1
      dut.finished shouldBe false
    }

    "Load as many resources as possible within a given time (2)" in {

      val dut = new Resources {
        var nItems = 0
        override protected def loadSome(): Boolean = {
          Thread.sleep(200)
          nItems += 1
          nItems >= 10
        }
      }

      dut.load(maxTimeMillis = 300)
      dut.nItems shouldBe 2
      dut.finished shouldBe false
    }

    "Load as many resources as possible within a given time (all)" in {

      val dut = new Resources {
        var nItems = 0
        override protected def loadSome(): Boolean = {
          Thread.sleep(100)
          nItems += 1
          nItems >= 10
        }
      }

      dut.load(maxTimeMillis = 10000)
      dut.nItems shouldBe 10
      dut.finished shouldBe true
    }

  }

}
