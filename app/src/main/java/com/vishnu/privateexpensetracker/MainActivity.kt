package com.vishnu.privateexpensetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ExpenseTrackerScreen() }
    }
}

@androidx.compose.runtime.Composable
private fun ExpenseTrackerScreen() {
    val context = LocalContext.current
    var smsEnabled by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var permissionMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        smsEnabled = result[Manifest.permission.READ_SMS] == true && result[Manifest.permission.RECEIVE_SMS] == true
        permissionMessage = if (smsEnabled) {
            "Automatic SMS tracking is active. Bank SMS is processed locally on this phone."
        } else {
            "SMS access was not granted. Automatic tracking remains off."
        }
    }

    LaunchedEffect(Unit) {
        expenses = ExpenseDatabase.get(context).expenseDao().all()
    }

    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.padding(20.dp)) {
                Text("Private Expense Tracker", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(6.dp))
                Text("Privacy-first • Offline-first", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(18.dp))

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Automatic SMS Tracking", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Reads bank transaction SMS locally and records eligible debits.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Switch(
                                checked = smsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        permissionLauncher.launch(
                                            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                                        )
                                    } else {
                                        smsEnabled = false
                                        permissionMessage = "Automatic SMS tracking is off."
                                    }
                                }
                            )
                        }
                        if (permissionMessage.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(permissionMessage, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                Text("Recent transactions", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))

                if (expenses.isEmpty()) {
                    Text("No transactions yet. Enable SMS tracking and wait for a bank transaction SMS.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(expenses.take(50), key = { it.id }) { expense ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(14.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(expense.merchant, style = MaterialTheme.typography.titleMedium)
                                        Text("₹%.2f".format(expense.amount), style = MaterialTheme.typography.titleMedium)
                                    }
                                    Text("${expense.category} • ${expense.source}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(expense.timestamp)),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
