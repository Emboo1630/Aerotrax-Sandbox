package com.template.states

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import com.template.contracts.contract
import net.corda.core.contracts.LinearState
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@BelongsToContract(contract::class)
data class airplaneState (
                          var partType : String,
                          var partName : String,
                          var partNumber : String,
                          var serialNumber : String,
                          var manufacturer : String,
                          var price: Int,
                          val status: String,
                          val issuer : String,
                          val listOfSpecifications: MutableList<SpecificationsState>,
                          val dateAdded: Instant = Instant.now(),
                          val sender: Party,
                          val receiver: Party,
                          override val participants: List<Party> = listOf(sender, receiver),
                          override  val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState

@CordaSerializable
data class SpecificationsState (val serialNumber: String,
                                val specificationTitle: String,
                                val specificationNumber: String,
                                val specificationUnit: String)
