package com.fadavidos.akka.streams

import akka.stream.scaladsl.{Source, Flow, Sink}
import akka.NotUsed
import akka.actor.ActorSystem

object QuickStart extends App {

  implicit val system: ActorSystem = ActorSystem("Quickstart")
  implicit val ec = system.dispatcher

  // Source has two types, the first one is the type of element that this source emits. The second one is called materialized.
  val source: Source[Int, NotUsed] = Source(1 to 100)

  val result = source.runForeach(println)


  result.onComplete( _ => system.terminate())

}
