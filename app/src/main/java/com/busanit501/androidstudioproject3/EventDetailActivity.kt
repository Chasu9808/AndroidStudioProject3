package com.example.test1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebaseexample.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import android.widget.EditText

import androidx.appcompat.app.AlertDialog


class EventDetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var eventCount: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("events")

        // Initialize views
        val eventNameView = findViewById<TextView>(R.id.event_name_view)
        val eventDescriptionView = findViewById<TextView>(R.id.event_description_view)
        val eventDateView = findViewById<TextView>(R.id.event_date_view)
        val eventTimeView = findViewById<TextView>(R.id.event_time_view)
        val eventImageView = findViewById<ImageView>(R.id.event_image_view)
        val deleteButton = findViewById<Button>(R.id.delete_event_button)
        val updateButton = findViewById<Button>(R.id.update_event_button)

        // Get event details from intent
        val eventName = intent.getStringExtra("eventName")
        val eventDescription = intent.getStringExtra("eventDescription")
        val eventDate = intent.getStringExtra("eventDate")
        val eventTime = intent.getStringExtra("eventTime")
        val eventImageUrl = intent.getStringExtra("eventImageUrl")
        eventCount = intent.getIntExtra("eventCount", 0)  // assuming eventCount is passed in the intent

        // Set event details to views
        eventNameView.text = eventName
        eventDescriptionView.text = eventDescription
        eventDateView.text = eventDate
        eventTimeView.text = eventTime

        if (!eventImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(eventImageUrl)
                .into(eventImageView)
        } else {
            eventImageView.visibility = View.GONE
        }

        // Set click listener for the delete button
        deleteButton.setOnClickListener {
            deleteEvent(eventCount)
        }

        // Set click listener for the update button
        updateButton.setOnClickListener {
            showUpdateDialog()
        }
    }

    private fun deleteEvent(eventNumber: Int) {
        database.get().addOnSuccessListener { snapshot ->
            for (eventSnapshot in snapshot.children) {
                val event = eventSnapshot.getValue(Event::class.java)
                if (event?.count == eventNumber) {
                    eventSnapshot.ref.removeValue().addOnSuccessListener {
                        Toast.makeText(this, "이벤트가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "이벤트 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    return@addOnSuccessListener // Exit the loop after deleting the event
                }
            }
            Toast.makeText(this, "해당 번호의 이벤트를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "이벤트를 삭제하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUpdateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_event, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.update_event_name)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.update_event_description)
        val dateEditText = dialogView.findViewById<EditText>(R.id.update_event_date)
        val timeEditText = dialogView.findViewById<EditText>(R.id.update_event_time)

        // Pre-fill existing event data
        nameEditText.setText(intent.getStringExtra("eventName"))
        descriptionEditText.setText(intent.getStringExtra("eventDescription"))
        dateEditText.setText(intent.getStringExtra("eventDate"))
        timeEditText.setText(intent.getStringExtra("eventTime"))

        AlertDialog.Builder(this)
            .setTitle("게시글 수정")
            .setView(dialogView)
            .setPositiveButton("수정") { _, _ ->
                val updatedName = nameEditText.text.toString()
                val updatedDescription = descriptionEditText.text.toString()
                val updatedDate = dateEditText.text.toString()
                val updatedTime = timeEditText.text.toString()

                if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedDate.isEmpty() || updatedTime.isEmpty()) {
                    Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedEvent = Event(
                    name = updatedName,
                    description = updatedDescription,
                    date = updatedDate,
                    time = updatedTime,
                    imageUrl = intent.getStringExtra("eventImageUrl") ?: "",
                    count = eventCount
                )

                updateEvent(eventCount, updatedEvent)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateEvent(eventNumber: Int, updatedEvent: Event) {
        database.get().addOnSuccessListener { snapshot ->
            for (eventSnapshot in snapshot.children) {
                val event = eventSnapshot.getValue(Event::class.java)
                if (event?.count == eventNumber) {
                    eventSnapshot.ref.setValue(updatedEvent)
                        .addOnSuccessListener {
                            Toast.makeText(this, "이벤트가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "이벤트 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    return@addOnSuccessListener // Exit the loop after updating the event
                }
            }
            Toast.makeText(this, "해당 번호의 이벤트를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "이벤트를 업데이트하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
