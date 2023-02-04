package com.fadavidos.akka.streams

import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Zip}
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem


object DevoxxBelguim02FirstComplexGraph extends DevoxxBelguim {

  implicit val system = ActorSystem("complexGraph")
  implicit val ex = system.dispatcher

  val incrementer = Flow[Int].map(x => x + 10)
  val doubler = Flow[Int].map(x => x * 10)
  val intToString = Flow[Int].map(x => x.toString)
  val output = Sink.foreach[(Int, Int)](println)

  val complexGraph = GraphDSL.create(){ implicit builder =>
    import GraphDSL.Implicits._

    // 'builder' allows us create components of the Graph and connections between them

    val broadcast = builder.add(Broadcast[Int](2))
    val zipper = builder.add(Zip[Int, Int])

    source ~> broadcast ~> incrementer ~> zipper.in0
              broadcast ~> doubler     ~> zipper.in1
                                          zipper.out ~> output

    ClosedShape
  }

  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(complexGraph).run()
  }
}
