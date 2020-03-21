package com.nowtv.actors

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.stream.Materializer
import akka.util.Timeout
import com.nowtv.actors.events.UserEvents._
import com.nowtv.database.Mongo
import com.nowtv.model.Asset.Asset
import com.nowtv.model.User.User
import com.nowtv.protocol.JsonProtocols
import com.nowtv.utility.UserDBFacility
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.duration._

/**
 * This Actor handles all [[com.nowtv.actors.events.UserEvents]]
 * by communicating with MongoDB - only collection "users"
 */
class UserActor extends Actor with ActorLogging with JsonProtocols {

    // Implicits + ExecutionContext
    implicit val system = ActorSystem("UserActor") // ActorMaterializer requires an implicit ActorSystem
    implicit val materializer: Materializer = Materializer(system) // bindAndHandle requires an implicit materializer

    import system.dispatcher

    implicit val timeout = Timeout(5 second)

    // Getting Users DB Collection
    private val codecRegistry = fromRegistries(fromProviders(classOf[User], classOf[Asset]), DEFAULT_CODEC_REGISTRY)
    private val users: MongoCollection[User] =
        Mongo.getDBConnection()
          .withCodecRegistry(codecRegistry)
          .getCollection("users")

    lazy val Users = new UserDBFacility(users)


    /**
     * Overriding method to handle [[com.nowtv.actors.events.UserEvents]]
     */
    override def receive: Receive = {

        case FindUser(id) =>
            log.info(s"Logging user with id: $id")
            val mySender = sender
            Users.findById(id).map { user =>
                log.info(s"User found is ${user.get}")
                mySender ! user
            }


        case FindUserAssets(id) =>
            log.info(s"Gettin assets for accountID: $id")
            val mySender = sender
            Users.findUserAssets(id).map { assetsIds =>
                mySender ! assetsIds
            }

        case AddAsset(id, asset) =>
            log.info(s"Adding asset $asset to account $id")
            val mySender = sender
            Users.addAsset(id, asset)
            mySender ! OperationSuccessfull

        case RemoveAsset(id, asset) =>
            log.info(s"Removing asset $asset from account $id")
            val mySender = sender
            Users.removeAsset(id, asset)
            mySender ! OperationSuccessfull


    }


}

