//package com.template.models
////
////import net.corda.core.contracts.UniqueIdentifier
////import net.corda.core.identity.Party
////import java.time.Instant
////
////data class userAccountModel(
////        var partType : String,
////        var partName : String,
////        var partNumber : String,
////        var serialNumber : String,
////        var manufacturer : String,
////        var price: Int,
////        val status: String,
////        val dateAdded: Instant = Instant.now(),
////        val issuer : String,
////        val sender: Party,
////        val receiver: Party,
////        override val participants: List<Party> = listOf(sender, receiver),
////        override  val linearId: UniqueIdentifier = UniqueIdentifier()
////)