package com.fadavidos.akka.streams

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.{ExecutionContext, Future}
import java.lang.Thread
import scala.util.Random

object DevoxxBelguim01Backpressure extends DevoxxBelguim {

  implicit val system = ActorSystem("DevoxxSystem")
  implicit val ec: ExecutionContext = system.dispatcher


  val asyncFlow: Flow[Int, Int, NotUsed] = Flow[Int].mapAsync(4) { x =>
    Future {
      println(s"The thread in asyncFlow is ${Thread.currentThread().getName}")
      Thread.sleep(Random.nextInt(1000))
      x * 2
    }
  }

  val fastSource: Source[Int, NotUsed] = source.via(asyncFlow).map { x =>
    println(s"The thread in fastSource is ${Thread.currentThread().getName}")
    println(s"waiting on the consumer: ${x}")
    x
  }.async

  val slowSink: Sink[Int, Future[Done]] = Sink.foreach[Int] { x =>
    println(s"The thread in slowSink is ${Thread.currentThread().getName}")
    Thread.sleep(1000)
    println(x)
  }

  def main(args: Array[String]): Unit = {
    println("Hello !!")
    fastSource.to(slowSink).run()
  }

}
