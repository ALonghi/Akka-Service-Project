package com.nowtv.actors.events

import com.nowtv.model.Asset.Asset
import com.nowtv.protocol.JsonProtocols

/**
 * List of events the [[com.nowtv.actors.UserActor]] will handle
 */
object UserEvents extends JsonProtocols {

    // Get User
    case class FindUser(userID: String)

    // Get all assets for the user
    case class FindUserAssets(userID: String)

    // Add Asset to User's Watchlist
    case class AddAsset(userID: String, asset: Asset)

    // Remove Asset from User's Watchlist
    case class RemoveAsset(userID: String, asset: Asset)

    // Operation ended successfully
    case object OperationSuccessfull


}
