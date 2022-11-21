package com.jason.kotlinplayground.kotlin

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * A junior developer has submitted a pull request for their first controller implementation.
 * - Suggest best practices, design patterns, and optimizations.
 * -- Write some of the code yourself so the junior dev understands.
 * - Ensure testing is adequate.  Write additional test cases you think could help.
 * - Find issues in logic and correct the code.
 */
// key focus areas in order of importance:
// identifying the bug with isDebit
// better test cases (e.g. that catch the bug)
// any other improvements.  could be performance, design pattern, and/or separation of concerns.
// - exception handling.
// - parallel fetching
// - caching

//@Controller - assume this is a spring boot controller.
class TransactionsController{
    //Inversion of control/Dependency Injection should be done here.
    val chaseBankClient = ChaseBankClient()
    val wellsFargoClient = WellsFargoClient()

    //@GetMapping(path='transactions', verb="GET")
    fun getTransactions(userId: String) : TransactionResponse{
        //this work doesn't belong in the controller.  Candidate should identify SOA, separation of concerns, etc.
        //can be optimized to be done in parallel so total time is that of the slowest client
        //can be optimized to add caching
        //exception handling. Timeouts
        //we could give a hint about latch or Thread.join
        val chaseTransactions = chaseBankClient.getTransactions(userId)
        val wellsFargoTransactions = wellsFargoClient.getTransactions(userId)

        //this work belongs in a factory function
        val transactions = mutableListOf<Transaction>()
        //addAll might be better approach.
        chaseTransactions.forEach{
            //"Chase" should be a constant.  isDebit logic should be put in a common/shared & testable function
            transactions.add(Transaction(it.id, it.amount, it.charger, it.amount > 0, "Chase"))
        }

        //the isDebit logic is wrong here.  this won't be covered by a test case.  the reviewer should spot this and add a test case for it.
        wellsFargoTransactions.forEach{
            //"Wells Fargo" should be a constant.  isDebit logic should be put in a common/shared & testable function
            transactions.add(Transaction(it.identifier.toString(), it.transactionAmount, it.biller, 0 > it.transactionAmount, "Wells Fargo"))
        }

        return TransactionResponse(transactions)
    }
}

data class Transaction(
    val id: String,
    val amount: Double,
    val chargeOrigination: String,
    val isDebit: Boolean,
    val sourceBank: String
)

data class TransactionResponse(
    val transactions: List<Transaction>
)

//Chase Client ====================================

//code formatting improvement opportunity.  inconsistent style
data class ChaseTransaction( val id: String, val amount: Double, val charger: String)

class ChaseBankClient{
    fun getTransactions(userId: String): List<ChaseTransaction>{
        Thread.sleep(Random.nextLong(0, 200))
        return listOf(
            ChaseTransaction("1", 10.00, "Starbucks"),
            ChaseTransaction("2", -30.00, "Venmo")
        )
    }
}

//WellsFargo Client ====================================

//code formatting improvement opportunity.  inconsistent style
data class WellsFargoTransaction(val identifier: Int, val transactionAmount: Double, val biller: String)

class WellsFargoClient{
    fun getTransactions(userId: String): List<WellsFargoTransaction>{
        if( Random.nextLong(0, 2) > 0L) throw Exception("Client connection failed")
        Thread.sleep(Random.nextLong(0, 200))
        return listOf(
            WellsFargoTransaction(1, 10.00, "Taco Stand"),
            WellsFargoTransaction(2, 13.35, "Taco Stand")
        )
    }
}

//tests ================================================
object Tests{
    //there should be other tests that breakdown mapping, logic, etc.
    fun shouldGetTransactions(){
        val transactionsController = TransactionsController();
        val transactionResponse = transactionsController.getTransactions("1")

        //fails to assert the total number of transactions returned.

        //this is not the taco stand record.
        val tacoStandTransaction = transactionResponse.transactions.find{ it.id == "1"}
        assertion(tacoStandTransaction?.amount == 10.00)
        //fails to test other fields.

        //inconsistent testing criteria leads to missing the incorrect isDebit calculation in Wells Fargo
        val venmoTransaction = transactionResponse.transactions.find{ it.id == "2" && it.chargeOrigination == "Venmo"}
        assertion(venmoTransaction?.sourceBank == "Chase")
        assertion(venmoTransaction?.isDebit == false)
    }
}

fun assertion(isTrue: Boolean){
    assert(isTrue)
    if(!isTrue){
        throw Exception("Assertion of value equality was not true.")
    }
}

class CodeReviewInterviewQuestionTests {
    @Test
    fun `should get transactions`() = runBlocking{
        Tests.shouldGetTransactions()
    }
}

