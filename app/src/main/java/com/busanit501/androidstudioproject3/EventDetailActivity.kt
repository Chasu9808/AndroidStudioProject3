package com.example.test1

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebaseexample.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import android.widget.EditText

class EventDetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private var eventCount: Int = 0
    private var fileUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // Initialize Firebase Database and Storage reference
        database = FirebaseDatabase.getInstance().reference.child("events")
        storage = FirebaseStorage.getInstance().reference

        // Initialize views
        val eventNameView = findViewById<TextView>(R.id.event_name_view)
        val eventDescriptionView = findViewById<TextView>(R.id.event_description_view)
        val eventDateView = findViewById<TextView>(R.id.event_date_view)
        val eventTimeView = findViewById<TextView>(R.id.event_time_view)
        val eventImageView = findViewById<ImageView>(R.id.event_image_view)
        val deleteButton = findViewById<Button>(R.id.delete_event_button)
        val updateButton = findViewById<Button>(R.id.update_event_button)
        val button1 = findViewById<Button>(R.id.button1) // 변수명 수정

        // Get event details from intent
        val eventName = intent.getStringExtra("eventName")
        val eventDescription = intent.getStringExtra("eventDescription")
        val eventDate = intent.getStringExtra("eventDate")
        val eventTime = intent.getStringExtra("eventTime")
        val eventImageUrl = intent.getStringExtra("eventImageUrl")
        eventCount = intent.getIntExtra("eventCount", 0)

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

        // Set click listener for button1 to navigate to another activity
        button1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
                    return@addOnSuccessListener
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
        val editImageButton = dialogView.findViewById<Button>(R.id.update_event_image_button)
//        val imagePreview = dialogView.findViewById<ImageView>(R.id.update_event_image_preview)

        // Pre-fill existing event data
        nameEditText.setText(intent.getStringExtra("eventName"))
        descriptionEditText.setText(intent.getStringExtra("eventDescription"))
        dateEditText.setText(intent.getStringExtra("eventDate"))
        timeEditText.setText(intent.getStringExtra("eventTime"))

        if (!intent.getStringExtra("eventImageUrl").isNullOrEmpty()) {
            Glide.with(this)
                .load(intent.getStringExtra("eventImageUrl"))
//                .into(imagePreview)
//            imagePreview.visibility = View.VISIBLE
        } else {
//            imagePreview.visibility = View.GONE
        }

        // Handle the Edit Image button click
        editImageButton.setOnClickListener {
            openFileChooser() // Open file chooser for new image
        }

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

                if (fileUri != null) {
                    uploadFileForEvent(updatedName, updatedDescription, updatedDate, updatedTime) // Upload the new image if selected
                } else {
                    val updatedEvent = Event(
                        name = updatedName,
                        description = updatedDescription,
                        date = updatedDate,
                        time = updatedTime,
                        imageUrl = intent.getStringExtra("eventImageUrl") ?: "",
                        count = eventCount
                    )
                    updateEvent(eventCount, updatedEvent) // Update event without changing image
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            previewImage() // Show the selected image in the dialog
        }
    }

    private fun previewImage() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_event, null)
//        val imagePreview = dialogView.findViewById<ImageView>(R.id.update_event_image_preview)

        if (fileUri != null) {
            Glide.with(this)
                .load(fileUri)
//                .into(imagePreview)
//            imagePreview.visibility = View.VISIBLE
        }
    }

    private fun uploadFileForEvent(updatedName: String, updatedDescription: String, updatedDate: String, updatedTime: String) {
        if (fileUri != null) {
            val fileRef = storage.child("uploads/${System.currentTimeMillis()}.jpg")
            fileRef.putFile(fileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val updatedEvent = Event(
                            name = updatedName,
                            description = updatedDescription,
                            date = updatedDate,
                            time = updatedTime,
                            imageUrl = imageUrl,
                            count = eventCount
                        )
                        updateEvent(eventCount, updatedEvent)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "파일 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
        }
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
