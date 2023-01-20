package com.fadavidos.akka.streams

import akka.stream.scaladsl.{FileIO, Flow, Sink, Source, Keep}

import scala.concurrent.Future
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.util.ByteString

import java.nio.file.Paths

object Step01QuickStartFactorial extends App with Factorial{

  implicit val system: ActorSystem = ActorSystem("Quickstart")
  implicit val ec = system.dispatcher

  val result: Future[IOResult] = factorial.map(num => ByteString(s"$num\n"))
    .runWith(FileIO.toPath(Paths.get("factorial.txt")))

  result.onComplete( _ => system.terminate())
}
