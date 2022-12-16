package com.fadavidos.akka.streams

import akka.stream.scaladsl.{FileIO, Flow, Sink, Source, Keep}

import scala.concurrent.Future
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.util.ByteString

import java.nio.file.Paths

object QuickStartFactorial extends App {

  implicit val system: ActorSystem = ActorSystem("Quickstart")
  implicit val ec = system.dispatcher

  // Source has two types, the first one is the type of element that this source emits. The second one is called materialized.
  // Source is a description of what you want to run.
  val source: Source[Int, NotUsed] = Source(1 to 100)

  // scan method is similar to fold but it isn't a terminal operation
  val factorial: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] = factorial.map(num => ByteString(s"$num\n"))
    .runWith(FileIO.toPath(Paths.get("factorial.txt")))

  result.onComplete( _ => system.terminate())
}
