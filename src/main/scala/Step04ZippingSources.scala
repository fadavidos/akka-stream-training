package com.fadavidos.akka.streams

import akka.NotUsed
import akka.stream.scaladsl.{Source, Flow}
import akka.actor.ActorSystem
import scala.concurrent.duration._


object Step04ZippingSources extends App with Factorial {

  implicit val actorSystem: ActorSystem = ActorSystem("Zipping")


  def createStringG[A, B]: Flow[(A, B), String, NotUsed] =
    Flow[(A, B)].map(t => s"${t._1} != ${t._2}")

  factorial
    .zipWith(Source(0 to 100))((num, idx) => (num, idx)) // We combine two sources
    .filterNot(t => t._1.toInt == t._2) // Removing where both number are equals
    .via(createStringG) // transforming tuple of numbers to a String
    .throttle(5, 10.second) // each 10 seconds we will process 5 elements
    .runForeach(println)

}
