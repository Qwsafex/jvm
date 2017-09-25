package ru.spbau.jvm.scala
package lecture03
package database

import akka.persistence.PersistentActor

import scala.collection.mutable

/**
  * @author Alefas
  */
class MemoriesActor extends PersistentActor {

  import MemoriesActor._

  val map: mutable.HashMap[Int, String] =
    mutable.HashMap.empty
  var memId: Int = 0

  def receiveEvent(event: Event): Unit = {
    event match {
      case Memorize(id, text) =>
        map += memId -> text
        memId += 1

      case Forget(id, idToDelete) =>
        map -= idToDelete
    }
  }

  override def receiveRecover: Receive = {
    case evt: Event => receiveEvent(evt)
  }

  override def receiveCommand: Receive = {
    case evt: Event => persist(evt)(receiveEvent)
    case GetMemories(id) =>
      sender ! Memories(map)
  }

  override def persistenceId = "memories-database"
}

object MemoriesActor {

  trait Event

  case class Memorize(id: Long, memoryText: String) extends Event

  case class Forget(id: Long, memoryId: Int) extends Event

  trait Query

  case class GetMemories(id: Long)

  case class Memories(buffer: mutable.HashMap[Int, String])
}
