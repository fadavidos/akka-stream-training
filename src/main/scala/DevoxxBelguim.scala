package com.fadavidos.akka.streams

import akka.NotUsed
import akka.stream.scaladsl.Source

trait DevoxxBelguim {

  val source: Source[Int, NotUsed] = Source(1 to 100)
}
