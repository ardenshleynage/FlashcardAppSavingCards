package com.org.flashcardapp_savingcards

import  android.content.Intent
import android.content.pm.ActivityInfo
import  android.os.Bundle
import android.widget.ImageView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowInsetsCompat

class AddQuestionsAnswersActivity : AppCompatActivity() {
    private var questionsToAdd = 0
    private var questionsAdded = 0
    private val addCardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val question = data?.getStringExtra("question_key").orEmpty()
            val correctAnswer = data?.getStringExtra("correct_answer_key").orEmpty()

            if (question.isNotEmpty() && correctAnswer.isNotEmpty()) {
                // ✅ Enregistrer dans la DB
                val db = FlashcardDatabase(this)
                val flashcard = Flashcard(question, correctAnswer)
                db.insertCard(flashcard)
                questionsAdded++
                if (questionsAdded < questionsToAdd) {
                    launchAddCard()
                } else {
                    Toast.makeText(this, "All $questionsToAdd cards saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_questions_answers)

        val answerField = findViewById<EditText?>(R.id.user_answer)
        val continueButton = findViewById<ImageView?>(R.id.continue_button)
        val backButton = findViewById<ImageView>(R.id.back_bouton)

        continueButton?.setOnClickListener {
            val answer = answerField?.text?.toString().orEmpty()

            if (answer.isBlank()) {
                Toast.makeText(this, "You must enter a number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val number = answer.toIntOrNull()
            if (number == null) {
                Toast.makeText(this, "Only whole numbers are allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val questionsToAdd = number
            val questionsAdded = 0

            val intent = Intent(this, AddCardActivity::class.java)
            intent.putExtra("questions_added", questionsAdded)
            intent.putExtra("questions_to_add", questionsToAdd)
            startActivity(intent)
        }


        backButton?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            addCardLauncher.launch(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun launchAddCard() {
        val intent = Intent(this, AddCardActivity::class.java)
        addCardLauncher.launch(intent)
    }
}