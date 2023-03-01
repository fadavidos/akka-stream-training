package com.fadavidos.akka.streams

import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DevoxxBelguim03BankGraph {

  implicit val system = ActorSystem("complexGraph")
  implicit val ex = system.dispatcher

  case class Transaction(
                        amount: Double,
                        sender: String,
                        receiver: String,
                        date: Int
                        )

  /*
  transaction is suspect
    - amount > 10k
    - receiver is in a blacklist
    - date is wrong when date > 1000 days
   */

  val suspicionThreshold = 10000
  val blacklist = Set("badguy", "badlady", "yourkid")
  val transactions = List(
    Transaction(30, "goodguy1", "goodguy2", 0),
    Transaction(30000, "goodguy1", "badguy", 0),
    Transaction(300, "badguy", "goodguy2", 3000),
    Transaction(3530, "goodguy1", "yourkids", 30000),
  )

  val countSink = Sink.fold[Int, Transaction](0)((count, _) => count + 1)

  val sumAllCounts: (Future[Int], Future[Int], Future[Int]) => Future[Int] = (f1, f2, f3) => for {
    count1 <- f1
    count2 <- f2
    count3 <- f3
  } yield count1 + count2 + count3

  val amlGraph = GraphDSL.createGraph(countSink, countSink, countSink)(sumAllCounts) { implicit builder =>
    (amountSink, blacklistSink, dateSink) =>
      import GraphDSL.Implicits._
      val txnSource = builder.add(Source(transactions))
      val broadcast = builder.add(Broadcast[Transaction](3))

      val amountDetector = builder.add(Flow[Transaction].filter(txn => txn.amount > suspicionThreshold))
      val blacklistedDetector = builder.add(Flow[Transaction].filter(txn => blacklist.contains(txn.receiver)))
      val dateDetector = builder.add(Flow[Transaction].filter(txn => txn.date > 1000))

      // visual graph
      txnSource ~>  broadcast ~>  amountDetector ~> amountSink
                    broadcast ~>  blacklistedDetector ~>  blacklistSink
                    broadcast ~>  dateDetector ~> dateSink

      ClosedShape

  }

  def main(arg: Array[String]): Unit = {
    println("Hello")
    val finalCountFuture = RunnableGraph.fromGraph(amlGraph).run()
    finalCountFuture.foreach(finalCount => println(s"TOTAL warnings: ${finalCount}"))
  }

}
