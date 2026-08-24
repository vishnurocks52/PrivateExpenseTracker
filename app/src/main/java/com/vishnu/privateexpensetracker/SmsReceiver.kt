package com.vishnu.privateexpensetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val pending = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                val body = messages.joinToString(separator = "") { it.messageBody.orEmpty() }
                if (body.isBlank()) return@launch
                val timestamp = messages.firstOrNull()?.timestampMillis ?: System.currentTimeMillis()
                val expense = BankSmsParser.parse(body, timestamp) ?: return@launch
                ExpenseDatabase.get(appContext).expenseDao().insert(expense)
            } finally {
                pending.finish()
            }
        }
    }
}
