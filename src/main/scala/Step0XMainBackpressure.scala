package com.fadavidos.akka.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

/*
Reactive stream specification

Publisher ; Emit elements asynchronously
Subscriber: consume elements asynchronously
Processor: transform thing a long the way

[]    ->    []    ->    []
publisher   processor   subscriber
source      flow        sink
 */

object Step0XMainBackpressure extends App {

  implicit val system = ActorSystem.create("akka-stream-system")

  val source = Source(1 to 1000)
  val flow = Flow[Int].map( _ * 10)
  val sink = Sink.foreach[Int](println)

  // it is like a blueprint
  val graph = source.via(flow).to(sink)

  // it should be run
  //graph.run()

  val slowSink = Sink.foreach[Int]{ x =>
    Thread.sleep(1000)
    println(s"-> [Sink] ${x}")
  }
  val debuggingFlow = Flow[Int].map{ x =>
    println(s"-> [Flow] -> ${x}")
    x
  }

  // No Backpressure
  // These three components are execute in the same actor, it is called "fusion"
  //source.via(debuggingFlow).to(slowSink).run()

  // With Backpressure
  // {      in the same actor    }{ in another actor }
  source.via(debuggingFlow).async.to(slowSink).run()
}
