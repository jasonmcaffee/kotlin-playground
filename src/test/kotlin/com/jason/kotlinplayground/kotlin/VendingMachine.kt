package com.jason.kotlinplayground.kotlin

import org.junit.jupiter.api.Test

/**
 * implement a coin based soda vending machine with 3 product selections
 * provide an initial inventory so that each product selection has 1 item.  (ie 3 total items)
 *
 * AC:
 * - should accept initial inventory
 * - should accept money in any denomination (e.g. .10, .5, .25)
 * - should allow the user to select a product
 *      - should allow for selecting a product before money has been deposited
 * - should allow the user to request a refund
 * - should provide change (assume infinite money of infinite denominations) immediately after vending.
 * - should notify user when money is refunded or change is provided
 * - should notify user with display messages (out of stock, deposit more money, refunding)
 * - should allow for different soda prices
 * - should dispense a product when selection is made
 * - should keep track of inventory
 * - should notify the user that the product has been vended.
 *
 *
 * bonus:
 * - certain messages should only display for a few seconds before being reset.  e.g. error message of no money to refund should only display for 3 seconds.
 * - should keep track of how much money it has available, and whether it can provide change based on the deposit amount and available selections.
 * - should only accept money if it can make exact change.
 * - add more inventory
 * - ensure proper/matching inventory are grouped together. e.g. can't add diet coke to pepsi slot.
 * - multi-threading - select product, vending, refund, etc all have a 1 second delay, and the user presses the button twice with more than enough money deposited, but inventory is low.
 */

data class Product(val name: String)
data class ProductSlot(val productCost: Double, val products: MutableList<Product>)

interface IVendingMachine {
    //user selects product
    fun selectProduct(buttonId: String)
    //user deposits money
    fun depositMoney(amount: Double)
    //user presses refund button
    fun refundDepositedMoney()

    val messageDisplayed: (message: String) -> Unit
    val refundOrChangeDispensed: (amount: Double) -> Unit
    val productDispensed: (product: Product) -> Unit
}

class VendingMachine(
    private val productSlots: Map<String, ProductSlot>,
    override val messageDisplayed: (message: String) -> Unit,
    override val refundOrChangeDispensed: (amount: Double) -> Unit,
    override val productDispensed: (product: Product) -> Unit
) : IVendingMachine {
    private var currentAmountDeposited = 0.0
    init{
        displayMessageToUser("please deposit money or make a selection")
    }

    fun displayMessageToUser(message: String){
        messageDisplayed(message)
    }

    override fun selectProduct(buttonId: String) {
        //first check the selection is valid
        val productSlot = productSlots[buttonId] ?: return displayMessageToUser("invalid selection")
        //check if there is inventory
        if(productSlot.products.size <= 0){
            return displayMessageToUser("out of stock.  please make another selection")
        }
        //check if enough money has been deposited
        val change = currentAmountDeposited - productSlot.productCost
        if(change < 0){
            return displayMessageToUser("please deposit: ${change * -1} more to purchase product")
        }
        //get a product and dispense it
        val product = productSlot.products.removeAt(0)
        //reset amount deposited
        currentAmountDeposited = 0.0
        //notify the user
        displayMessageToUser("vending...")
        productDispensed(product)
        if(change > 0){
            displayMessageToUser("your change is: $change")
            refundOrChangeDispensed(change)
        }
        displayMessageToUser("please deposit money or make a selection")
    }

    override fun depositMoney(amount: Double) {
        if(amount <= 0){
            displayMessageToUser("invalid amount")
        }
        currentAmountDeposited += amount
        displayMessageToUser("$currentAmountDeposited deposited")
    }

    override fun refundDepositedMoney() {
        if(currentAmountDeposited <= 0){
            return displayMessageToUser("No money to refund")
        }
        val amount = currentAmountDeposited
        currentAmountDeposited = 0.0
        refundOrChangeDispensed(amount)
    }
}

class VendingMachineTests {

    @Test fun `should accept money`(){
        val productSlots = mapOf(
            "1" to ProductSlot(1.25, mutableListOf(Product("coke"), )),
            "2" to ProductSlot(1.50, mutableListOf(Product("sprite"), )),
            "3" to ProductSlot(1.75, mutableListOf(Product("dr pepper"), )),
        )

        var messageCount = 0
        val expectedMessages = listOf(
            "please deposit money or make a selection",
            "0.25 deposited",
            "invalid selection",
            "please deposit: 1.25 more to purchase product",
            "1.5 deposited",
            "vending...",
            "please deposit money or make a selection",
            "1.5 deposited",
            "out of stock.  please make another selection",
            "vending...",
            "your change is: 0.25",
            "please deposit money or make a selection",
        )
        fun messageDisplayed(message: String){
            val expectedMessage = expectedMessages[messageCount]
            assert(expectedMessage == message)
            messageCount++
        }

        var refundCount = 0
        val expectedRefunds = listOf(
            .25,
            .25
        )
        fun refundOrChangeDispensed(amount: Double){
            val expectedRefund = expectedRefunds[refundCount]
            assert(expectedRefund == amount)
            refundCount++
        }

        var productDispensedCount = 0
        val expectedProducts = listOf(
            Product("sprite"),
            Product("coke")
        )
        fun productDispensed (product: Product){
            val expectedProduct = expectedProducts[productDispensedCount]
            assert(expectedProduct == product)
            productDispensedCount++
        }
        val vendingMachine = VendingMachine(productSlots, ::messageDisplayed, ::refundOrChangeDispensed, ::productDispensed)

        vendingMachine.depositMoney(.25)
        vendingMachine.selectProduct("5")
        vendingMachine.selectProduct("2")
        vendingMachine.refundDepositedMoney()
        vendingMachine.depositMoney(1.5)
        vendingMachine.selectProduct("2")

        vendingMachine.depositMoney(1.5)
        vendingMachine.selectProduct("2")
        vendingMachine.selectProduct("1")

        assert(messageCount == expectedMessages.size)
        assert(refundCount == expectedRefunds.size)
        assert(productDispensedCount == expectedProducts.size)

    }
}