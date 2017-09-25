package ru.spbau.jvm.scala
package lecture03

import akka.actor.{ActorSystem, Props}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import ru.spbau.jvm.scala.lecture03.bot.{AskActor, MemoriesBot}
import ru.spbau.jvm.scala.lecture03.database.MemoriesActor

object Main extends App {

  val token = "400581389:AAFe68dhaPyp5r8JwpEXl1yxTE2C3UnTnKI"
  val system = ActorSystem()
  val scheduler = QuartzSchedulerExtension(system)
  val database = system.actorOf(Props(classOf[MemoriesActor]))

  private val bot = new MemoriesBot(token, database)
  val actor = system.actorOf(Props(classOf[AskActor], bot))

  scheduler.createSchedule("every minute", None, "	0/1 * * 1/1 * ? *")
  scheduler.schedule("every minute", actor, "Ask")

  bot.run()
}
