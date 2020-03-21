package com.nowtv.model


/**
 * Asset Mongo Model with Case Class and fields validation
 */
object Asset {

    def apply(contentID: String,
              name: String,
              description: String): Asset =
        Asset(java.util.UUID.randomUUID.toString, contentID, name, description)

    case class Asset(
                      _id: String = java.util.UUID.randomUUID.toString,
                      contentID: String,
                      name: String,
                      description: String
                    ) {
        require(contentID != null, "Asset id not informed")
        require(contentID.nonEmpty, "Asset id cannot be empty")
        require(name.nonEmpty, "Asset name cannot be empty")
        require(description.nonEmpty, "Asset description cannot be empty")
    }


}

