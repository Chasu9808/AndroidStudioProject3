package com.busanit501.androidstudioproject3

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebaseexample.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity2 : AppCompatActivity() {

    private lateinit var eventName: EditText
    private lateinit var eventDescription: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventTime: EditText
    private lateinit var createEventButton: Button
    private lateinit var eventList: LinearLayout
    private lateinit var button1: Button
    private lateinit var uploadFileButton: Button
    private lateinit var imagePreview: ImageView

    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private val PICK_IMAGE_REQUEST = 1
    private var fileUri: Uri? = null
    private var currentEventToUpdate: Event? = null // To hold the event being updated

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize views
        eventName = findViewById(R.id.event_name)
        eventDescription = findViewById(R.id.event_description)
        eventDate = findViewById(R.id.event_date)
        eventTime = findViewById(R.id.event_time)
        createEventButton = findViewById(R.id.create_event)
        eventList = findViewById(R.id.event_list)
        button1 = findViewById(R.id.button1)
        uploadFileButton = findViewById(R.id.upload_file_button)
        imagePreview = findViewById(R.id.image_preview)

        // Initialize Firebase Database and Storage references
        database = FirebaseDatabase.getInstance().reference.child("events")
        storage = FirebaseStorage.getInstance().reference

        // Display saved events
//        displayEvents()

        // Set click listener for creating an event
        createEventButton.setOnClickListener {
            if (fileUri != null) {
                uploadFile()
            } else {
                // Directly create an event without an image
                createEvent("")
            }
        }

        // Set click listener for button1 to navigate to MainActivity
        button1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for file upload button
        uploadFileButton.setOnClickListener {
            openFileChooser()
        }
    }

    private fun createEvent(imageUrl: String) {
        val name = eventName.text.toString()
        val description = eventDescription.text.toString()
        val date = eventDate.text.toString()
        val time = eventTime.text.toString()

        if (name.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a new key for the event
        val newEventRef = database.push()
        val key = newEventRef.key ?: ""

        // Get the current count of events
        database.get().addOnSuccessListener { snapshot ->
            val count = snapshot.childrenCount.toInt() + 1

            // Create the event with the count and key
            val event = Event(name, description, date, time, imageUrl, count, key)
            newEventRef.setValue(event)
//            displayEvents()

            // Clear fields
            eventName.text.clear()
            eventDescription.text.clear()
            eventDate.text.clear()
            eventTime.text.clear()
            fileUri = null // Clear fileUri after creating the event
            resetImagePreview() // Hide the image preview

            // Navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener {
            Toast.makeText(this, "이벤트를 생성하는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

//    @SuppressLint("SetTextI18n")
//    private fun displayEvents() {
//        eventList.removeAllViews() // Clear existing views
//        database.get().addOnSuccessListener { snapshot ->
//            for (eventSnapshot in snapshot.children) {
//                val event = eventSnapshot.getValue(Event::class.java)
//                event?.let { event ->
//                    val eventView = layoutInflater.inflate(R.layout.event_item, null)
//                    val nameView = eventView.findViewById<TextView>(R.id.event_name_view)
//                    val descriptionView = eventView.findViewById<TextView>(R.id.event_description_view)
//                    val dateView = eventView.findViewById<TextView>(R.id.event_date_view)
//                    val timeView = eventView.findViewById<TextView>(R.id.event_time_view)
//                    val countView = eventView.findViewById<TextView>(R.id.event_count_view)
//                    val imageView = eventView.findViewById<ImageView>(R.id.event_image_view)
//                    val deleteButton = eventView.findViewById<Button>(R.id.delete_event_button)
//                    val editButton = eventView.findViewById<Button>(R.id.edit_event_button)
//
//                    nameView.text = event.name
//                    descriptionView.text = "설명: ${event.description}"
//                    dateView.text = "날짜: ${event.date}"
//                    timeView.text = "시간: ${event.time}"
//                    countView.text = "이벤트 번호: ${event.count}"
//
//                    if (event.imageUrl.isNotEmpty()) {
//                        Glide.with(this)
//                            .load(event.imageUrl)
//                            .into(imageView)
//                        imageView.visibility = View.VISIBLE
//                    } else {
//                        imageView.visibility = View.GONE
//                    }
//
//                    // Set click listener to open EventDetailActivity
//                    eventView.setOnClickListener {
//                        val intent = Intent(this, EventDetailActivity::class.java)
//                        intent.putExtra("eventName", event.name)
//                        intent.putExtra("eventDescription", event.description)
//                        intent.putExtra("eventDate", event.date)
//                        intent.putExtra("eventTime", event.time)
//                        intent.putExtra("eventImageUrl", event.imageUrl)
//                        startActivity(intent)
//                    }
//
//                    // Set click listener for the delete button
//                    deleteButton.setOnClickListener {
//                        deleteEvent(event.count)
//                    }
//
//                    // Set click listener for the edit button
//                    editButton.setOnClickListener {
//                        showUpdateDialog(event)
//                    }
//
//                    eventList.addView(eventView)
//                }
//            }
//        }.addOnFailureListener {
//            Toast.makeText(this, "이벤트를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun deleteEvent(eventNumber: Int) {
//        database.get().addOnSuccessListener { snapshot ->
//            for (eventSnapshot in snapshot.children) {
//                val event = eventSnapshot.getValue(Event::class.java)
//                if (event?.count == eventNumber) {
//                    eventSnapshot.ref.removeValue().addOnSuccessListener {
//                        Toast.makeText(this, "이벤트가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                        displayEvents() // Refresh the event list after deletion
//                    }.addOnFailureListener {
//                        Toast.makeText(this, "이벤트 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                    return@addOnSuccessListener // Exit the loop after deleting the event
//                }
//            }
//            Toast.makeText(this, "해당 번호의 이벤트를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(this, "이벤트를 삭제하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

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
            if (currentEventToUpdate != null) {
                uploadFileForEvent(currentEventToUpdate!!)
                previewImage() // Show the selected image in the dialog
            } else {
                previewImage()
            }
        }
    }

    private fun previewImage() {
        if (fileUri != null) {
            Glide.with(this)
                .load(fileUri)
                .into(imagePreview)
            imagePreview.visibility = View.VISIBLE
        }
    }

    private fun resetImagePreview() {
        imagePreview.visibility = View.GONE
    }

    private fun uploadFile() {
        if (fileUri != null) {
            val fileRef = storage.child("uploads/${System.currentTimeMillis()}.jpg")
            fileRef.putFile(fileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        createEvent(imageUrl)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "File Upload Failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadFileForEvent(event: Event) {
        if (fileUri != null) {
            val fileRef = storage.child("uploads/${System.currentTimeMillis()}.jpg")
            fileRef.putFile(fileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val updatedEvent = event.copy(imageUrl = imageUrl)
//                        updateEvent(event.count, updatedEvent)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "File Upload Failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

//    private fun updateEvent(eventNumber: Int, updatedEvent: Event) {
//        database.get().addOnSuccessListener { snapshot ->
//            for (eventSnapshot in snapshot.children) {
//                val event = eventSnapshot.getValue(Event::class.java)
//                if (event?.count == eventNumber) {
//                    eventSnapshot.ref.setValue(updatedEvent).addOnSuccessListener {
//                        Toast.makeText(this, "이벤트가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
//                        displayEvents() // Refresh the event list after updating
//                    }.addOnFailureListener {
//                        Toast.makeText(this, "이벤트 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                    return@addOnSuccessListener // Exit the loop after updating the event
//                }
//            }
//            Toast.makeText(this, "해당 번호의 이벤트를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(this, "이벤트를 업데이트하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

//    private fun showUpdateDialog(event: Event) {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_update_event, null)
//        val nameEditText = dialogView.findViewById<EditText>(R.id.update_event_name)
//        val descriptionEditText = dialogView.findViewById<EditText>(R.id.update_event_description)
//        val dateEditText = dialogView.findViewById<EditText>(R.id.update_event_date)
//        val timeEditText = dialogView.findViewById<EditText>(R.id.update_event_time)
//        val editImageButton = dialogView.findViewById<Button>(R.id.update_event_image_button)
//        val imagePreview = dialogView.findViewById<ImageView>(R.id.update_event_image_preview)
//
//        // Pre-fill existing event data
//        nameEditText.setText(event.name)
//        descriptionEditText.setText(event.description)
//        dateEditText.setText(event.date)
//        timeEditText.setText(event.time)
//
//        if (event.imageUrl.isNotEmpty()) {
//            Glide.with(this)
//                .load(event.imageUrl)
//                .into(imagePreview)
//            imagePreview.visibility = View.VISIBLE
//        } else {
//            imagePreview.visibility = View.GONE
//        }
//
//        // Handle the Edit Image button click
//        editImageButton.setOnClickListener {
//            currentEventToUpdate = event // Store the event being updated
//            openFileChooser() // Open file chooser for new image
//        }
//
//        AlertDialog.Builder(this)
//            .setTitle("Update Event")
//            .setView(dialogView)
//            .setPositiveButton("Update") { _, _ ->
//                val updatedName = nameEditText.text.toString()
//                val updatedDescription = descriptionEditText.text.toString()
//                val updatedDate = dateEditText.text.toString()
//                val updatedTime = timeEditText.text.toString()
//
//                if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedDate.isEmpty() || updatedTime.isEmpty()) {
//                    Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
//                    return@setPositiveButton
//                }
//
//                val updatedEvent = event.copy(
//                    name = updatedName,
//                    description = updatedDescription,
//                    date = updatedDate,
//                    time = updatedTime
//                )
//
//                updateEvent(event.count, updatedEvent)
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
}
