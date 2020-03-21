package com.nowtv.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.Materializer
import com.nowtv.actors.events.AssetEvents._
import com.nowtv.actors.events.UserEvents.{AddAsset, FindUserAssets, RemoveAsset}
import com.nowtv.actors.{AssetActor, UserActor}
import com.nowtv.model.Asset.Asset

/**
 * This class is used to launch the server with its routes and actors
 */
object RunnableRoutes extends App with RouteService {

    override implicit val system: ActorSystem = ActorSystem("NowTv")
    override implicit val materializer: Materializer = Materializer(system)
    val log = Logging(system.eventStream, "NowTvApp")

    import system.dispatcher

    override val assetsDb: ActorRef = system.actorOf(Props[AssetActor], "AssetActor")
    override val usersDb: ActorRef = system.actorOf(Props[UserActor], "UserActor")

    override val routes =
        pathPrefix("api") {
            pathPrefix(Segment / "asset") { accountID =>
                post {
                    log.info("Inside post!")
                    entity(as[Asset]) {
                        asset =>
                            log.info(s"Logged asset to add$asset")
                            complete((usersDb ? AddAsset(accountID, asset)).map(_ => StatusCodes.OK))
                    }
                } ~
                  delete {
                      log.info("Inside delete!")
                      entity(as[Asset]) { asset =>
                          log.info(s"Logged asset to remove $asset")
                          complete((usersDb ? RemoveAsset(accountID, asset)).map(_ => StatusCodes.OK))
                      }
                  } ~
                  (pathEndOrSingleSlash & get) {
                      val userFuture =
                          (usersDb ? FindUserAssets(accountID)).mapTo[Seq[Asset]]
                      complete(userFuture)
                  }
            } ~
              pathPrefix("library") {
                  log.info("Inside library!")
                  path(Segment) { assetId =>
                      get {
                          log.info(s"Logged requested id $assetId")
                          val assetFuture = (assetsDb ? GetAsset(assetId)).mapTo[Option[Asset]]
                          complete(assetFuture)
                      }
                  } ~
                    (pathEndOrSingleSlash & get) {
                        log.info("Inside library!")
                        val allAssets =
                            (assetsDb ? FindAllAssets)
                              .mapTo[Seq[Asset]]
                        complete(StatusCodes.OK -> allAssets)
                    }
              }
        }

    startService

    override def startService: Unit =
        Http().bindAndHandle(routes, "localhost", 8081)
}
