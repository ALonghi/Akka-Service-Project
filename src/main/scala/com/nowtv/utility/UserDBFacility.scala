package com.nowtv.utility

import com.nowtv.model.Asset.Asset
import com.nowtv.model.User.User
import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates._

import scala.concurrent.{ExecutionContext, Future}

/**
 * MongoDB Facility for collection "assets"
 * @param collection
 * @param ec implicit dispatcher
 */

class UserDBFacility(collection: MongoCollection[User])(implicit ec: ExecutionContext) {


    def findByPk(id: String): Future[Option[User]] =
        collection
          .find(Document("_id" -> new ObjectId(id)))
          .first
          .head
          .map(Option(_))

    def findById(id: String): Future[Option[User]] =
        collection
          .find(equal("userID", id))
          .first
          .head
          .map(Option[User])


    def findUserAssets(accountId: String): Future[Seq[Asset]] =
        collection
          .find(equal("userID", accountId))
          //                            .projection(fields(include("assets"), excludeId()))
          .first
          .head
          .map(user => user.assets)
          .mapTo[Seq[Asset]]

    def addAsset(accountId: String, asset: Asset): Unit =
        collection
          .findOneAndUpdate(
              equal("userID", accountId),
              push("assets", asset)
          )
          .head

    def removeAsset(accountId: String, asset: Asset) = {
        collection
          .findOneAndUpdate(
              equal("userID", accountId),
              pull("assets", asset),
          )
          .head

    }

    def save(user: User): Future[String] =
        collection
          .insertOne(user)
          .head
          .map { _ => user._id.toHexString }
}
