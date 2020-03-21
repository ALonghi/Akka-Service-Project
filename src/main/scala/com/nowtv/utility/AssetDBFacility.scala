package com.nowtv.utility

import com.nowtv.model.Asset.Asset
import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.{all, equal}

import scala.concurrent.{ExecutionContext, Future}

/**
 * MongoDB Facility for collection "assets"
 * @param collection
 * @param ec implicit dispatcher
 */
class AssetDBFacility(collection: MongoCollection[Asset])(implicit ec: ExecutionContext) {

    def findByPk(id: String): Future[Option[Asset]] =
        collection
          .find(Document("_id" -> id))
          .first
          .head
          .map(Option(_))

    def findAll() =
        collection
          .find
          .collect
          .toFuture


    def findById(id: String): Future[Option[Asset]] =
        collection
          .find(equal("contentID", id))
          .first
          .head
          .map(Option[Asset])


    def findByIds(ids: Seq[String]): Future[Seq[Asset]] =
        collection
          .find(all("contentID", ids))
          .collect
          .toFuture


    def save(asset: Asset): Future[String] =
        collection
          .insertOne(asset)
          .head
          .map { _ => asset._id.toString }
}
