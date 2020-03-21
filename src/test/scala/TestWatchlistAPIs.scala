import akka.actor.{ActorRef, Props}
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.pattern.ask
import akka.util.Timeout
import com.nowtv.actors.events.AssetEvents._
import com.nowtv.actors.events.UserEvents._
import com.nowtv.actors.{AssetActor, UserActor}
import com.nowtv.model.Asset.Asset
import com.nowtv.protocol.JsonProtocols
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class TestWatchlistAPIs extends WordSpec with Matchers with ScalatestRouteTest with JsonProtocols with SprayJsonSupport {

    val assetsDb: ActorRef = system.actorOf(Props[AssetActor], "AssetActor")
    val usersDb: ActorRef = system.actorOf(Props[UserActor], "UserActor")
    implicit val timeout = Timeout(2 seconds)

    val log = Logging(system.eventStream, "NowTvApp")

    val myAsset: Asset =
        Asset(
            contentID = "new0n3",
            name = "The Witcher",
            description = "The witcher Geralt, a mutated monster hunter, struggles to find his place in a world in which people often prove more wicked than beasts."
        )


    val routes =
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


    "The Watchlist service" should {

        "consume only JSON objects " in {
            // tests:
            Get("/api/library") ~> routes ~> check {
                contentType shouldBe ContentTypes.`application/json`
            }

            Get("/api/123/asset") ~> routes ~> check {
                contentType shouldBe ContentTypes.`application/json`
            }
        }

        "allow user with id 123 to view his/her Watchlist's assets" in {
            // tests:
            Get("/api/123/asset") ~> routes ~> check {
                status shouldBe StatusCodes.OK
                contentType shouldBe ContentTypes.`application/json`
            }
        }

        "leave GET requests to other paths unhandled" in {
            // tests:
            Get("/contact") ~> routes ~> check {
                handled shouldBe false
            }
        }

        "allow user to ADD an asset TO his/her watchlist" in {
            // tests:
            Post("/api/123/asset", myAsset) ~> routes ~> check {
                status shouldBe StatusCodes.OK
            }
        }


        "allow the user to REMOVE an asset FROM his/her watchlist" in {
            // tests:
            Delete("/api/123/asset", myAsset) ~> routes ~> check {
                status shouldBe StatusCodes.OK
            }
        }


    }

}