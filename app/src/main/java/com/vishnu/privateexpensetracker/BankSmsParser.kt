package com.vishnu.privateexpensetracker

import java.util.Locale
import kotlin.math.abs

object BankSmsParser {
    private val amountRegex = Regex("(?:INR|Rs\\.?|₹)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", RegexOption.IGNORE_CASE)
    private val alternateAmountRegex = Regex("(?:debited|credited|spent|paid|received)[^0-9]{0,30}([0-9,]+(?:\\.[0-9]{1,2})?)", RegexOption.IGNORE_CASE)
    private val referenceRegex = Regex("(?:UPI|UTR|ref(?:erence)?)[^A-Za-z0-9]*([A-Za-z0-9_-]{6,})", RegexOption.IGNORE_CASE)

    fun parse(body: String, receivedAt: Long): Expense? {
        val lower = body.lowercase(Locale.ROOT)
        val isDebit = listOf("debited", "debit", "spent", "paid", "purchase", "withdrawn").any { lower.contains(it) }
        if (!isDebit) return null

        val amountText = amountRegex.find(body)?.groupValues?.getOrNull(1)
            ?: alternateAmountRegex.find(body)?.groupValues?.getOrNull(1)
            ?: return null
        val amount = amountText.replace(",", "").toDoubleOrNull() ?: return null
        if (amount <= 0.0 || amount > 100_000_000.0) return null

        val reference = referenceRegex.find(body)?.groupValues?.getOrNull(1).orEmpty()
        val merchant = extractMerchant(body)
        val category = categorize(merchant, body)
        val confidence = if (merchant != "Bank transaction") 0.85 else 0.55

        return Expense(
            merchant = merchant,
            amount = amount,
            timestamp = receivedAt,
            category = category,
            source = "SMS",
            reference = reference.ifBlank { stableReference(body, receivedAt, amount) },
            confidence = confidence
        )
    }

    private fun extractMerchant(body: String): String {
        val patterns = listOf(
            Regex("(?:at|to|merchant)\\s+([A-Za-z][A-Za-z0-9 .&_-]{2,40})", RegexOption.IGNORE_CASE),
            Regex("VPA\\s*[:=-]?\\s*([A-Za-z0-9._-]+@[A-Za-z0-9._-]+)", RegexOption.IGNORE_CASE)
        )
        for (pattern in patterns) {
            val value = pattern.find(body)?.groupValues?.getOrNull(1)?.trim()?.trim('.', ',', ';')
            if (!value.isNullOrBlank()) return value
        }
        return "Bank transaction"
    }

    private fun categorize(merchant: String, body: String): String {
        val text = "$merchant $body".lowercase(Locale.ROOT)
        return when {
            listOf("swiggy", "zomato", "restaurant", "food", "cafe", "uber eats").any { text.contains(it) } -> "Food"
            listOf("uber", "ola", "rapido", "metro", "fuel", "petrol", "diesel").any { text.contains(it) } -> "Transport"
            listOf("amazon", "flipkart", "myntra", "shopping", "mall").any { text.contains(it) } -> "Shopping"
            listOf("netflix", "spotify", "movie", "bookmyshow", "entertainment").any { text.contains(it) } -> "Entertainment"
            listOf("hospital", "pharmacy", "medical", "health").any { text.contains(it) } -> "Health"
            else -> "Uncategorized"
        }
    }

    private fun stableReference(body: String, timestamp: Long, amount: Double): String =
        "SMS-${abs((body.trim().hashCode() * 31L) + timestamp + amount.toLong())}"
}
