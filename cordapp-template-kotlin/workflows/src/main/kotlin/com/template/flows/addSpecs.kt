//package com.template.flows
//
//import co.paralleluniverse.fibers.Suspendable
//import com.flowdb.CryptoValuesDatabaseService
//import com.template.contracts.contract
//import net.corda.core.flows.*
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//import com.template.flow.*
//import com.template.states.SpecificationsState
//import com.template.states.airplaneState
//import net.corda.core.contracts.*
//import net.corda.core.identity.Party
//import net.corda.core.node.services.vault.QueryCriteria
//import net.corda.core.utilities.ProgressTracker
//import java.time.Instant
//
//
//@InitiatingFlow
//@StartableByRPC
//class addSpecs(private val listOfSpecifications: SpecificationsState, private val linearId : UniqueIdentifier): FlowFunction() {
//    override val progressTracker = ProgressTracker(INITIALIZING, BUILDING, SIGNING, COLLECTING, FINALIZING)
//
//    @Suspendable
//    override fun call(): SignedTransaction {
//        progressTracker.currentStep = INITIALIZING
//
//        val databaseService = serviceHub.cordaService(CryptoValuesDatabaseService::class.java)
////        databaseService.registerPart(partType, partName, partNumber, serialNumber, manufacturer, price, status, issuer, date2String, sender2String, receiver2String,linearId = UniqueIdentifier())
//        databaseService.addSpecs(listOfSpecifications.serialNumber, listOfSpecifications.specificationNumber, listOfSpecifications.specificationTitle,  listOfSpecifications.specificationUnit)
//        val transaction: TransactionBuilder = transaction()
//        val signedTransaction: SignedTransaction = verifyAndSign(transaction)
//        val aerotrax = stringToPartySpy("Aerotrax")
//        val sessions = initiateFlow(aerotrax)
//        val transactionSignedByAllParties: SignedTransaction = collectSignature(signedTransaction, listOf(sessions))
//        return recordTransaction(transactionSignedByAllParties, listOf(sessions))
//    }
//
//    val input = inputStateRef(linearId)
//
//    private fun outputState(): airplaneState
//    {   val aerotrax = stringToPartySpy("Aerotrax")
//        val input = inputStateRef(linearId).state.data
//        return airplaneState(
//                partType = input.partType,
//                partName = input.partName,
//                partNumber = input.partNumber,
//                serialNumber = input.serialNumber,
//                manufacturer = input.manufacturer,
//                price = input.price,
//                status = input.status,
//                issuer = input.issuer,
//                dateAdded = input.dateAdded,
//                sender = ourIdentity,
//                receiver = aerotrax,
//                linearId = linearId,
//                listOfSpecifications = SpecificationsState(serialNumber = listOfSpecifications.serialNumber, specificationNumber = listOfSpecifications.specificationNumber, specificationTitle = listOfSpecifications.specificationTitle, specificationUnit = listOfSpecifications.specificationUnit)
//        )
//    }
//
//    private fun transaction(): TransactionBuilder {
//        progressTracker.currentStep = BUILDING
//       val aerotrax = stringToPartySpy("Aerotrax")
//        val notary = serviceHub.networkMapCache.notaryIdentities.first()
//        val issueCommand = Command(contract.Commands.Issue(), listOf(aerotrax,ourIdentity).map(Party::owningKey))
//        val builder = TransactionBuilder(notary = notary )
//        builder.addInputState(input)
//        builder.addOutputState(outputState(), contract.IOU_CONTRACT_ID)
//        builder.addCommand(issueCommand)
//        return builder
//    }
//}
//
//@InitiatedBy(addSpecs::class)
//class addSpecsResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
//    @Suspendable
//    override fun call(): SignedTransaction {
//        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
//            override fun checkTransaction(stx: SignedTransaction) = requireThat {
//                val output = stx.tx.outputs.single().data
//                "This must be an IOU transaction" using (output is airplaneState)
//            }
//        }
//
//        val txWeJustSignedId = subFlow(signedTransactionFlow)
//        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = txWeJustSignedId.id))
//    }
//}