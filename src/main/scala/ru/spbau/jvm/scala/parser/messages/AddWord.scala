package ru.spbau.jvm.scala
package lecture03
package parser
package messages

trait UserMessage

case class Memorize(memoryText: String) extends UserMessage

case class Forget(memoryId: Int) extends UserMessage

case object GetMemories extends UserMessage

case object WrongMessage extends UserMessage
