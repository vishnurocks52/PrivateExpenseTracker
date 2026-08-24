package com.vishnu.privateexpensetracker

object CategoryEngine {
    private val rules = mapOf(
        "swiggy" to "Food", "zomato" to "Food", "restaurant" to "Food",
        "uber" to "Transport", "ola" to "Transport", "metro" to "Transport",
        "amazon" to "Shopping", "flipkart" to "Shopping",
        "netflix" to "Entertainment", "spotify" to "Entertainment",
        "pharmacy" to "Health", "hospital" to "Health",
        "rent" to "Housing", "electricity" to "Utilities"
    )

    fun classify(merchant: String): Pair<String, Double> {
        val hit = rules.entries.firstOrNull { merchant.lowercase().contains(it.key) }
        return if (hit != null) hit.value to 0.92 else "Uncategorized" to 0.0
    }
}
