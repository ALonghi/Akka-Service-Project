
package com.nowtv.service

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MethodRejection, RejectionHandler, Route}
import akka.stream.Materializer
import akka.util.Timeout
import com.nowtv.protocol.JsonProtocols

import scala.concurrent.duration._

trait RouteService extends App with JsonProtocols with SprayJsonSupport {
    implicit val system: ActorSystem
    implicit val materializer: Materializer
    implicit val timeout = Timeout(5 seconds)

    // Exception handler for all Routes
    implicit def myExceptionHandler =
        RejectionHandler.newBuilder()
          .handleAll[MethodRejection] { methodRejections =>
              val names = methodRejections.map(_.supported.name)
              complete((StatusCodes.MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
          }
          .handleNotFound {
              complete((StatusCodes.NotFound, "Rejected. Found Nothing here!"))
          }
          .result()


    // Actors ref
    val assetsDb: ActorRef
    val usersDb: ActorRef

    // Application routes
    val routes: Route

    // Server starting
    def startService: Unit

}
