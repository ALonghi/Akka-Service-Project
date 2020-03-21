package com.nowtv.protocol

import com.nowtv.model.Asset.Asset
import com.nowtv.model.User.User
import spray.json.DefaultJsonProtocol

// JSON Trait needed
trait JsonProtocols extends DefaultJsonProtocol {

    // Class for Mongo ObjectId
    import MongoDBProtocol._

    implicit val assetFormat = jsonFormat4(Asset)
    implicit val userFormat = jsonFormat4(User)

}

