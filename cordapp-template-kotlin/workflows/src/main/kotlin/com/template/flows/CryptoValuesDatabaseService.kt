package com.flowdb


import com.template.flows.SPECS_TABLE
import com.template.flows.TABLE_NAME
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import java.time.Instant

/**
 * A database service subclass for handling a table of crypto values.
 *
 * @param services The node's service hub.
 */
@CordaService
class CryptoValuesDatabaseService(services: ServiceHub) : DatabaseService(services) {
    init {
        setUpStorage()
        setUpSpecification()
    }

    /**
     * Adds a crypto token and associated value to the table of crypto values.
     */
    fun registerPart(partType : String,
                     partName : String,
                     partNumber : String,
                     serialNumber : String,
                     manufacturer : String,
                     price : Int,
                     status : String,
                     issuer : String,
                     date2String : String,
                     sender2String : String,
                     receiver2String : String
                ) {
        val query = "insert into $TABLE_NAME values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        val params = mapOf(1 to partType, 2 to partName, 3 to partNumber, 4 to serialNumber, 5 to manufacturer,
                            6 to price, 7 to status, 8 to issuer, 9 to date2String, 10 to sender2String, 11 to receiver2String
                           )

        executeUpdate(query, params)
        log.info("Part number $partNumber added to airplane_parts table.")
    }


    /**
     * Adds a Specs
     */

    fun addSpecs( serialNumber: String,
                  specificationNumber: String,
                  specificationTitle: String,
                  specificationUnit: String) {
        val query = "insert into $SPECS_TABLE values(?, ?, ?, ?)"

        val params = mapOf(1 to serialNumber, 2 to specificationNumber, 3 to specificationTitle, 4 to specificationUnit)

        executeUpdate(query, params)
        log.info("Specification $specificationTitle added to specification table.")
    }

    /**
     * Updates the value of a crypto token in the table of crypto values.
     */
//    fun updateTokenValue(token: String, value: Int, comment: String) {
//        val query = "update $TABLE_NAME set value = ?, comment = ? where token = ?"
//
//        val params = mapOf(1 to value, 2 to comment, 3 to token)
//
//        executeUpdate(query, params)
//        log.info("Token $token updated in crypto_values table.")
//    }
//
//    /**
//     * Retrieves the value of a crypto token in the table of crypto values.
//     */
//    fun queryTokenValue(token: String) : Int {
//        val query = "select value, comment from $TABLE_NAME where token = ?"
//
//        val params = mapOf(1 to token)
//
//        val results = executeQuery(query, params) { it -> it.getInt("value")}
//        val results1 = executeQuery(query, params) {it -> it.getString("comment")}
//
//        if (results.isEmpty()) {
//            throw IllegalArgumentException("Token $token not present in database.")
//        }
//        val value = results.single()
//        log.info("Token $token read from crypto_values table.")
////        fun aaa() : Int { println (results); println(results1)}
//        return value
//        println("Comment: " + results1)
//    }

    /**
     * Initialises the table of crypto values.
     */

//    select * from node_infos

    private fun setUpStorage() {
        val query = """
                     create table if not exists $TABLE_NAME(
                     partType varchar(64), 
                     partName varchar(64), 
                     partNumber varchar(64), 
                     serialNumber varchar(64), 
                     manufacturer varchar(64),
                     price INTEGER,
                     status varchar(64),
                     issuer varchar(64),
                     dateAdded varchar(64),
                     sender varchar(64),
                     receiver varchar(64),
                     PRIMARY KEY (serialNumber)
                     )"""

        executeUpdate(query, emptyMap())
        log.info("Created airplane_parts table.")
    }

    private fun setUpSpecification() {
        val query = """
                     create table if not exists $SPECS_TABLE(
                     serialNumber varchar(64), 
                     specificationNumber varchar(64),
                     specificationTitle varchar(64),
                     specificationUnit varchar(64),
                     PRIMARY KEY (specificationNumber),
                     FOREIGN KEY (serialNumber) REFERENCES $TABLE_NAME (serialNumber)
                     
                     )"""

        executeUpdate(query, emptyMap())
        log.info("Created airplane_parts table.")
    }
}