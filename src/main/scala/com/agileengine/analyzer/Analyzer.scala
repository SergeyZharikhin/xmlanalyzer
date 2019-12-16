package com.agileengine.analyzer

import java.io.File

import org.jsoup.Jsoup.parse
import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConverters._
import scala.util.Try

object Analyzer {

  /**
    * 1. Searches an xml element by id in original HTML
    * 2. Then searches for similar element in diff file
    * 3. Generates Xpath if element was found
    */
  def analyze(elementId: String,
              originalHtml: String,
              diffHtml: String): Either[String, String] = {
    println("Started analyzing.")

    val origFile = new File(originalHtml)
    lazy val diffFile = new File(diffHtml)

    val maybeElement = for {
      origDoc <- Try(parse(origFile, "UTF-8", origFile.getAbsolutePath)).toOption
        .toRight("Failed to parse original file.")
      elById <- Option(origDoc.getElementById(elementId))
        .toRight("No elements found by provided id in original file.")
      diffDoc <- Try(parse(diffFile, "UTF-8", diffFile.getAbsolutePath)).toOption
        .toRight("Failed to parse diff file.")
      similarElement <- analyzeDiffHtml(diffDoc, elById)
        .toRight("No element found in diff file.")
    } yield similarElement
    println("Finished analyzing.")
    maybeElement.map(generateXpath)
  }

  private def analyzeDiffHtml(doc: Document, origElement: Element) =
    Option(doc.getElementById(origElement.id))
      .orElse(searchElementByTagAndAttributes(origElement, doc))

  private def searchElementByTagAndAttributes(origElement: Element,
                                              doc: Document) = {
    doc.getElementsByTag(origElement.tagName).asScala.to[List] match {
      case Nil         => findSimilar(doc.getAllElements.asScala, origElement)
      case withSameTag => findSimilar(withSameTag, origElement)
    }
  }

  private def findSimilar(elements: Seq[Element],
                          originalEl: Element): Option[Element] = {

    type WeightedElement = (Element, Int)

    def determineSimilar(mostSimilar: Option[(Element, Int)],
                         currentElement: Element,
                         currentWeight: Int) = {
      (currentWeight, mostSimilar) match {
        case (0, mostSimilar) => mostSimilar
        case (curWght, None)  => Option(currentElement, curWght)
        case (curWght, Some((_, topWght))) if curWght >= topWght =>
          Option(currentElement, curWght)
        case (_, mostSimilar @ Some(_)) => mostSimilar
      }
    }

    @scala.annotation.tailrec
    def traverse(elements: Seq[Element],
                 mostSimilar: Option[WeightedElement]): Option[Element] =
      elements match {
        case Nil => mostSimilar.map(_._1)
        case currentEl :: tail =>
          val currWeight = similarityWeight(originalEl, currentEl)
          traverse(tail, determineSimilar(mostSimilar, currentEl, currWeight))
      }

    traverse(elements, None)
  }

  private def similarityWeight(origElement: Element, diffElement: Element) =
    (diffElement.attributesAndText.toSet intersect origElement.attributesAndText.toSet).toMap.size

  /**
    * Generates Xpath for provided element
    */
  private def generateXpath(element: Element) = {
    @scala.annotation.tailrec
    def traverse(el: Element, path: String): String =
      Option(el.parent()) match {
        case None => path
        case Some(parent) =>
          val value = s"/${el.tagName()}[${el.elementSiblingIndex()}]$path"
          traverse(parent, value)
      }

    traverse(element.parent(),
             s"/${element.tagName()}[${element.elementSiblingIndex()}]")
  }

  private implicit class ElementOps(element: Element) extends AnyRef {
    lazy val attributesAndText: Map[String, String] = element
      .attributes()
      .asScala
      .map(attr => attr.getKey -> attr.getValue)
      .toMap +
      ("text" -> element.text)
  }

}
