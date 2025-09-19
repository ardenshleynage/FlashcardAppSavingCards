package com.org.flashcardapp_savingcards

import   android.os.Bundle
import android.widget.ImageView
import android.widget.EditText
import android.content.Intent
import android.content.pm.ActivityInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddCardActivity : AppCompatActivity() {
    private var questionsAdded = 0
    private var questionsToAdd = 0

    private lateinit var progressText2 : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_card)

        progressText2 = findViewById(R.id.progress_text_2)
        val cancelButton = findViewById<ImageView>(R.id.cancel_bouton)
        val saveButton = findViewById<ImageView?>(R.id.save_bouton)
        val questionField = findViewById<EditText?>(R.id.add_question)
        val correctAnswerField = findViewById<EditText?>(R.id.add_correct_answer)
        val questionToEdit = intent.getStringExtra("question_key").orEmpty()
        val correctAnswerToEdit = intent.getStringExtra("correct_answer_key").orEmpty()

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        questionsAdded = intent.getIntExtra("questions_added", 0)
        questionsToAdd = intent.getIntExtra("questions_to_add", 0)
        updateProgressText()

        saveButton?.setOnClickListener {
            val question = questionField?.text?.toString().orEmpty()
            val correct = correctAnswerField?.text?.toString().orEmpty()

            if (question.isBlank() || correct.isBlank()) {
                Toast.makeText(this, "You must enter the question and the answer", Toast.LENGTH_SHORT).show()
            } else {
                val db = FlashcardDatabase(this)
                db.insertCard(Flashcard(question, correct))

                val updatedCount = questionsAdded + 1

                if (updatedCount >= questionsToAdd) {
                    val intent = Intent(this, CongratulationActivity::class.java)
                    intent.putExtra("cards_added_count", updatedCount)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, AddCardActivity::class.java)
                    intent.putExtra("questions_added", updatedCount)
                    intent.putExtra("questions_to_add", questionsToAdd)
                    startActivity(intent)
                }
                finish()
            }
        }

        if (questionToEdit.isNotEmpty() && correctAnswerToEdit.isNotEmpty()) {
            questionField?.setText(questionToEdit)
            correctAnswerField?.setText(correctAnswerToEdit)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun updateProgressText() {
        progressText2.text = "Question ${questionsAdded + 1}/$questionsToAdd"
    }
}