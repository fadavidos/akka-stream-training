package com.fadavidos.akka.streams

import akka.NotUsed
import akka.stream.scaladsl.Source

trait Factorial {

  // Source has two types, the first one is the type of element that this source emits. The second one is called materialized.
  // Source is a description of what you want to run.
  val source: Source[Int, NotUsed] = Source(1 to 100)

  // scan method is similar to fold but it isn't a terminal operation
  val factorial: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) => acc * next)


}
