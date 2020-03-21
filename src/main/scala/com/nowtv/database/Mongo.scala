package com.nowtv.database

import org.mongodb.scala.{MongoClient, MongoDatabase}

/**
 * This object contains the connection to the database
 * Note: the username and password are available only for this Test's purpose
 * therefore they will be delete after the test has been reviewed
 *
 */
object Mongo {

    def getDBConnection(): MongoDatabase = {

        //        val config: Config = ConfigFactory.load()
        //
        //        val mongoUser: String = config.getString("mongo.user")
        //        val mongoPassword: String = config.getString("mongo.password")
        //        val mongoCluster: String = config.getString("mongo.cluster")
        //        val mongoDatabase: String = config.getString("mongo.database")

        //        val uri = s"mongodb+srv://$mongoUser:$mongoPassword@$mongoCluster/test?retryWrites=true&w=majority"

        val uri = "mongodb+srv://NowTV:NowTvTest@cluster0-2lrin.mongodb.net/test?retryWrites=true&w=majority"
        val client: MongoClient = MongoClient(uri)

        client.getDatabase("NowTv")
    }


}
