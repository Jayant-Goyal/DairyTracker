// Path: app/src/main/java/com/example/milktracker/MainActivity.kt
package com.example.milktracker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // Note: This requires google-services.json to work fully, 
    // but will compile with the dummy file in CI.
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firestore safely
        try {
             db = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
             // Handle missing config gracefully
        }

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val rgShift = findViewById<RadioGroup>(R.id.rgShift)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toDouble()
                val shiftId = rgShift.checkedRadioButtonId
                val shift = if (shiftId == R.id.rbMorning) "Morning" else "Evening"
                
                saveToFirestore(amount, shift)
                tvStatus.text = "Saved $amount L ($shift)"
            }
        }
    }

    private fun saveToFirestore(amount: Double, shift: String) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val data = hashMapOf(
            "date" to date,
            "shift" to shift,
            "amount" to amount,
            "timestamp" to System.currentTimeMillis()
        )

        try {
            db.collection("users").document("default_user")
                .collection("logs").add(data)
                .addOnSuccessListener {
                    updateWidget()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "DB Error: Check google-services.json", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateWidget() {
        val intent = Intent(this, MilkWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, MilkWidgetProvider::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
