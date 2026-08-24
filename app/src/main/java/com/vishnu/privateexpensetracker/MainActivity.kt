package com.vishnu.privateexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Private Expense Tracker",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Offline-first expense tracking")
                        Spacer(Modifier.height(20.dp))
                        Text("Weekly • Monthly • Yearly")
                        Spacer(Modifier.height(8.dp))
                        Text("Bank statements, SMS and later Gmail import can be reconciled locally.")
                    }
                }
            }
        }
    }
}
