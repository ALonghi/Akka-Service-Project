package com.nowtv.model

import com.nowtv.model.Asset.Asset
import org.mongodb.scala.bson.ObjectId

/**
 * Asset Mongo Model with Case Class and fields validation
 */
object User {

    def apply(userID: String,
              name: String,
              assets: Seq[Asset]): User =
        User(new ObjectId(), userID, name, assets)

    case class User(
                     _id: ObjectId,
                     userID: String,
                     name: String,
                     assets: Seq[Asset]
                   ) {
        require(userID != null, "userID not informed")
        require(userID.nonEmpty, "userID cannot be empty")
    }


}

