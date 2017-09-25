package ru.spbau.jvm.scala
package lecture03
package bot

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}
import ru.spbau.jvm.scala.lecture03.database.MemoriesActor
import ru.spbau.jvm.scala.lecture03.database.MemoriesActor.Memories
import ru.spbau.jvm.scala.lecture03.parser.MessageParser
import ru.spbau.jvm.scala.lecture03.parser.messages._

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.util.Success

class AskActor(bot: MemoriesBot) extends Actor {
  override def receive = {
    case _ => bot.askUsers()
  }
}

class MemoriesBot(val token: String,
                   val database: ActorRef) extends TelegramBot with Polling with Commands {
  def askUsers(): Unit = {

  }

  val map: mutable.HashMap[Long, String] = mutable.HashMap.empty

  onMessage {
    implicit message =>
      message.text.foreach { text =>
        MessageParser.parse(text) match {
          case Memorize(memoryText) =>
            database !
              MemoriesActor.Memorize(message.chat.id, memoryText)
            reply("Я запомнил :)")
          case Forget(memoryId) =>
            database !
              MemoriesActor.Forget(message.chat.id, memoryId)
            reply("Об этом больше не вспомню.")
          case GetMemories =>
            implicit val timeout: Timeout = Timeout(1.second)
            (database ? MemoriesActor.GetMemories(message.chat.id)).onComplete {
              case Success(Memories(buffer)) =>
                reply(buffer.map {
                  case (id, memoryText) => s"$id -> $memoryText"
                }.mkString("\n"))
              case _ =>
                reply("Ошибка базы данных!:(")
            }
          case WrongMessage =>
              reply("Неверная команда:(")
        }
      }
  }
}
