package ru.spbau.jvm.scala
package lecture03
package parser


import ru.spbau.jvm.scala.lecture03.parser.messages._

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers

/**
  * @author Alefas
  */
class MessageParser extends RegexParsers {
  override def skipWhitespace = true

  override val whiteSpace: Regex = "[ \t\r\f]+".r

  val wordParser: Parser[String] = raw"[\S ]+".r
  val intParser: Parser[Int] = "[0-9]*".r ^^ {
    _.toInt
  }

  val remember: Parser[Memorize] =
    "[Зз]апомнить".r ~> wordParser  ^^ { text => Memorize(text) }

  val forget: Parser[Forget] = "[Зз]абыть".r ~> intParser ^^ { id => Forget(id) }

  val getMemories: Parser[UserMessage] = "[Вв]оспоминания".r ^^ { _ => GetMemories }

  val userMessage: Parser[UserMessage] =
    remember | forget | getMemories
}

object MessageParser extends MessageParser {
  def parse(text: String): UserMessage = {
    parse(userMessage, text) match {
      case Success(message, _) => message
      case _ => WrongMessage
    }
  }
}
