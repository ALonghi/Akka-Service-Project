package com.nowtv.actors

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.stream.Materializer
import com.nowtv.actors.events.AssetEvents._
import com.nowtv.database.Mongo
import com.nowtv.model.Asset.Asset
import com.nowtv.utility.AssetDBFacility
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.codecs.Macros._

/**
 * This Actor handles all [[com.nowtv.actors.events.AssetEvents]]
 * by communicating with MongoDB - only collection "assets"
 */
class AssetActor extends Actor with ActorLogging {

    // Implicits + ExecutionContext
    implicit val system = ActorSystem("AssetActor") // ActorMaterializer requires an implicit ActorSystem
    implicit val materializer: Materializer = Materializer(system) // bindAndHandle requires an implicit materializer

    import context.dispatcher


    // Getting Assets DB Collection
    val codecRegistry = fromRegistries(fromProviders(classOf[Asset]), DEFAULT_CODEC_REGISTRY)
    val assets: MongoCollection[Asset] =
        Mongo.getDBConnection()
          .withCodecRegistry(codecRegistry)
          .getCollection("assets")

    val Assets = new AssetDBFacility(assets)


    /**
     * Overriding method to handle [[com.nowtv.actors.events.AssetEvents]]
     */
    override def receive: Receive = {

        case FindAllAssets =>
            log.info("Searching for all assets..")
            val mySender = sender
            Assets.findAll.map { allAssets =>
                mySender ! allAssets
            }

        case GetAssets(ids) =>
            val mySender = sender

            if (ids.isEmpty) {
                log.info("Ids in input are empty")
                mySender ! Seq.empty[Asset]
            }
            else {
                log.info(s"Getting assets ids: $ids")
                Assets.findByIds(ids).map { assets =>
                    log.info(s"\nFound:\n $assets")
                    mySender ! assets
                }
            }

        case GetAsset(id) =>
            log.info(s"Searching asset by id: $id")
            val mySender = sender
            Assets.findById(id).map { asset =>
                mySender ! asset
            }

    }


}
