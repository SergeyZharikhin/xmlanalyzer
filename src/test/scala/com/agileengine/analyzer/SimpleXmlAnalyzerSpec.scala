package com.agileengine.analyzer

import com.agileengine.analyzer.SimpleXmlAnalyzer
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.TableDrivenPropertyChecks._

class SimpleXmlAnalyzerSpec extends AnyFlatSpec {

  val originalHtml = "src/test/resources/sample-0-origin.html"
  val existentId = "make-everything-ok-button"
  val testInput =
    Table(
      ("diffFile", "expectedPath"),
      ("src/test/resources/sample-1-evil-gemini.html",
       "Element found: /html[0]/body[1]/div[0]/div[1]/div[2]/div[0]/div[0]/div[1]/a[1]"),
      ("src/test/resources/sample-2-container-and-clone.html",
       "Element found: /html[0]/body[1]/div[0]/div[1]/div[2]/div[0]/div[0]/div[1]/div[0]/a[0]"),
      ("src/test/resources/sample-3-the-escape.html",
       "Element found: /html[0]/body[1]/div[0]/div[1]/div[2]/div[0]/div[0]/div[2]/a[0]"),
      ("src/test/resources/sample-4-the-mash.html",
       "Element found: /html[0]/body[1]/div[0]/div[1]/div[2]/div[0]/div[0]/div[2]/a[0]"),
    )

  forAll(testInput) { (diffFile, expectedPath) =>
    assert(
      SimpleXmlAnalyzer
        .run(Array(existentId, originalHtml, diffFile)) === expectedPath)
  }

}
