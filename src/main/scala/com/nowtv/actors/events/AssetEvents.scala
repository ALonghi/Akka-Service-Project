package com.nowtv.actors.events

import com.nowtv.protocol.JsonProtocols

/**
 * List of events the [[com.nowtv.actors.AssetActor]] will handle
 */
object AssetEvents extends JsonProtocols {

    // Get one asset
    case class GetAsset(assetID: String)

    // Get all assets from a given list ( e.g. get User's assets )
    case class GetAssets(assetsList: Seq[String])

    // Get all available assets
    case object FindAllAssets

}
