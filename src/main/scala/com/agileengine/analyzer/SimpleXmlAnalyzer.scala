package com.agileengine.analyzer

object SimpleXmlAnalyzer extends App {

  def run(args: Array[String]): String = validate(args).toEither match {
    case Right((id, orig, diff)) => outputOf(Analyzer.analyze(id, orig, diff))
    case Left(errors)            => errors.toNonEmptyList.toList.mkString("\n")
  }

  private def outputOf(result: Either[String, String]) = result match {
    case Left(processingError) => processingError
    case Right(path)           => s"Element found: $path"
  }

  println(run(args))
}
