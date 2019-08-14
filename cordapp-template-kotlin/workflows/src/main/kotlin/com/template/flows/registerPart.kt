package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.flowdb.CryptoValuesDatabaseService
import com.template.contracts.contract
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import com.template.flow.*
import com.template.states.SpecificationsState

import com.template.states.airplaneState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import java.time.Instant

const val TABLE_NAME = "airplane_parts"
const val SPECS_TABLE = "specifications"

@InitiatingFlow
@StartableByRPC
class registerPart(private val partType : String,
                   private val partName : String,
                   private val partNumber : String,
                   private val serialNumber : String,
                   private val manufacturer : String,
                   private val price : Int,
                   private val status : String,
                   private val issuer : String,
                   private val specificationNumber : String = mutableListOf<SpecificationsState>().map { it.specificationNumber }.single(),
                   private val specificationTitle : String = mutableListOf<SpecificationsState>().map { it.specificationTitle }.single(),
                   private val specificationUnit : String = mutableListOf<SpecificationsState>().map { it.specificationUnit }.single()
//                           mutableListOf(SpecificationsState(serialNumber,specificationNumber = "",specificationTitle = "", specificationUnit = ""))
): FlowFunction() {
    override val progressTracker = ProgressTracker(INITIALIZING, BUILDING, SIGNING, COLLECTING, FINALIZING)

    @Suspendable
    override fun call(): SignedTransaction {
        progressTracker.currentStep = INITIALIZING
        val date = outputState().dateAdded
        val date2String = date.toString()
        val from = outputState().sender
        val sender2String = from.toString()
        val to = outputState().receiver
        val receiver2String = to.toString()
        val databaseService = serviceHub.cordaService(CryptoValuesDatabaseService::class.java)
        databaseService.registerPart(partType, partName, partNumber, serialNumber, manufacturer, price, status, issuer, date2String, sender2String, receiver2String)
        databaseService.addSpecs(serialNumber, specificationNumber, specificationTitle, specificationUnit)

        val transaction: TransactionBuilder = transaction()
        val signedTransaction: SignedTransaction = verifyAndSign(transaction)
        val aerotrax = stringToPartySpy("Aerotrax")
        val sessions = initiateFlow(aerotrax)
        val transactionSignedByAllParties: SignedTransaction = collectSignature(signedTransaction, listOf(sessions))
        return recordTransaction(transactionSignedByAllParties, listOf(sessions))
    }
//kahit di na isign ni aero
    private fun outputState(): airplaneState
    {val aerotrax = stringToPartySpy("Aerotrax")

        return airplaneState(
                 partType = partType,
                 partName = partName,
                 partNumber = partNumber,
                serialNumber = serialNumber,
                 manufacturer = manufacturer,
                 price = price,
                 status = status,
                 issuer = issuer,
                 dateAdded = Instant.now(),
                 sender = ourIdentity,
                 receiver = aerotrax,
                linearId = UniqueIdentifier(),
                listOfSpecifications = mutableListOf(SpecificationsState(serialNumber, specificationTitle, specificationNumber, specificationUnit))
        )
    }

    private fun transaction(): TransactionBuilder {
        progressTracker.currentStep = BUILDING
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(contract.Commands.Issue(), listOf(outputState().sender, outputState().receiver).map(Party::owningKey))
        val builder = TransactionBuilder(notary = notary )
        builder.addOutputState(outputState(), contract.IOU_CONTRACT_ID)
        builder.addCommand(issueCommand)
        return builder
    }
}

@InitiatedBy(registerPart::class)
class registerPartResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be an IOU transaction" using (output is airplaneState)
            }
        }

        val txWeJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = txWeJustSignedId.id))
    }
}