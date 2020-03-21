package com.nowtv.protocol

import org.mongodb.scala.bson.ObjectId
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

/**
 * This protocol manager deserialized the field ObjectId from MongoDB
 * This is needed to parse JSON protocols for the Actors
 */
object MongoDBProtocol extends DefaultJsonProtocol {

    implicit object ObjectIdSerializer extends RootJsonFormat[ObjectId] {
        override def write(obj: ObjectId): JsValue = {
            JsString(obj.toHexString)
        }

        override def read(json: JsValue): ObjectId = {
            val ob = new ObjectId(json.toString())
            ob
        }
    }

}
