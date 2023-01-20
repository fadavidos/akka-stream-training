package com.fadavidos.akka.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink}
import akka.util.ByteString

import java.nio.file.Paths
import scala.concurrent.Future

object Step03MainReusableSink extends App with Factorial{

  implicit val system: ActorSystem = ActorSystem.create("factorial2")

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String].map(s => ByteString(s + "\n")).toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  val printlnFactorial: Flow[String, String, NotUsed] = Flow[String].map( value =>{
    println(s"the factorial number is: ${value}")
    value
  })

  factorial
    .map(_.toString())
    .via(printlnFactorial)
    .runWith(lineSink("factorial2.txt"))

}
