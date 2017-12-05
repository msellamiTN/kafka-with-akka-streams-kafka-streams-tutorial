package com.lightbend.modelserver.actor.queryablestate

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import scala.concurrent.duration._
import com.lightbend.modelServer.model.ModelToServeStats
import com.lightbend.modelserver.actor.actors.{GetModels, GetState}
import de.heikoseeberger.akkahttpjackson.JacksonSupport
import akka.pattern.ask

object QueriesAkkaHttpResource extends JacksonSupport {

  implicit val askTimeout = Timeout(30 seconds)

  def storeRoutes(modelserver: ActorRef): Route =
    get {
      path("state"/Segment) { datatype =>
         onSuccess(modelserver ? GetState(datatype)) {
          case info: ModelToServeStats =>
            complete(info)
        }
      } ~
      path("models") {
        onSuccess(modelserver ? GetModels()) {
          case models: Seq[String] =>
            complete(models)
        }
      }
    }
}