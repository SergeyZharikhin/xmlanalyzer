package com.agileengine

import cats.data.ValidatedNec
import cats.syntax.validated._
import cats.syntax.contravariantSemigroupal._

import scala.util.Try

package object analyzer {

  type ValidationResult[A] = ValidatedNec[String, A]

  def validate(
      args: Array[String]): ValidationResult[(String, String, String)] =
    (validateId(args), validateOriginalFile(args), validateDiffFile(args))
      .mapN((_, _, _))

  private def validateId(args: Array[String]): ValidationResult[String] =
    getArg(0, args, "Please provide element id to search for")

  private def validateOriginalFile(
      args: Array[String]): ValidationResult[String] =
    getArg(1, args, "Please provide original file")

  private def validateDiffFile(args: Array[String]): ValidationResult[String] =
    getArg(2, args, "Please provide diff file")

  private def getArg(index: Int, args: Array[String], errorMessage: String) =
    Try(args(index)).map(_.validNec).getOrElse(errorMessage.invalidNec)

}
