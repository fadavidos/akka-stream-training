package com.fadavidos.akka.streams

import akka.stream.scaladsl.{Sink, Source, GraphDSL, Broadcast}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DevoxxBelguim03BankGraph extends App {

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
    Transaction(300, "badguy", "goodguy2", 0),
    Transaction(3530, "goodguy1", "yourkids", 0),
  )

  val countSink = Sink.fold[Int, Transaction](0)((count, txn) => count + 1)
  val sumAllCounts: (Future[Int], Future[Int], Future[Int]) => Future[Int] = (f1, f2, f3) => for {
    count1 <- f1
    count2 <- f2
    count3 <- f3
  } yield count1 + count2 + count3


}
