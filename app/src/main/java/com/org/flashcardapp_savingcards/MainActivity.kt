package com.org.flashcardapp_savingcards

import  android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var startText: TextView
    private lateinit var questionText: TextView
    private lateinit var correctAnswerText: TextView
    private lateinit var nextButton: ImageView
    private lateinit var previousButton: ImageView
    private lateinit var addQuestionButton: ImageView
    private lateinit var flashcardDb: FlashcardDatabase
    private var flashcards: List<Flashcard> = emptyList()
    private var currentIndex = 0
    private lateinit var progressText: TextView

    private val addCardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val question = data?.getStringExtra("question_key").orEmpty()
            val correctAnswer = data?.getStringExtra("correct_answer_key").orEmpty()
            nextButton = findViewById(R.id.next_button)
            previousButton = findViewById(R.id.previous_button)

            if (question.isNotEmpty() && correctAnswer.isNotEmpty()) {
                val newCard = Flashcard(question, correctAnswer)
                flashcardDb.insertCard(newCard)
                flashcards = flashcardDb.getAllCards()
                currentIndex = flashcards.size - 1
                showFlashcard(flashcards[currentIndex])

                Snackbar.make(findViewById(R.id.main), "Card successfully created!", Snackbar.LENGTH_SHORT).show()
            } else {
                showStartScreen()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        startText = findViewById(R.id.flashcard_start)
        questionText = findViewById(R.id.flashcard_question)
        correctAnswerText = findViewById(R.id.flashcard_correct_answer)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        addQuestionButton = findViewById(R.id.add_question_bouton)
        progressText = findViewById(R.id.progress_text)
        flashcardDb = FlashcardDatabase(this)
        flashcards = flashcardDb.getAllCards()

        val showLast = intent.getBooleanExtra("show_last_card", false)
        if (flashcards.isNotEmpty()) {
            currentIndex = if (showLast) {
                flashcards.size - 1
            } else {
                0
            }
            showFlashcard(flashcards[currentIndex])
        } else {
            showStartScreen()
        }

        addQuestionButton.setOnClickListener {
            val intent = Intent(this, AddQuestionsAnswersActivity::class.java)
            addCardLauncher.launch(intent)
        }

        questionText.setOnClickListener {
            questionText.visibility = View.INVISIBLE
            correctAnswerText.visibility = View.VISIBLE
        }

        correctAnswerText.setOnClickListener {
            questionText.visibility = View.VISIBLE
            correctAnswerText.visibility = View.INVISIBLE
        }

        nextButton.setOnClickListener {
            if (flashcards.isNotEmpty() && currentIndex < flashcards.size - 1) {
                currentIndex++
                showFlashcard(flashcards[currentIndex])
            }
        }

        previousButton.setOnClickListener {
            if (flashcards.isNotEmpty() && currentIndex > 0) {
                currentIndex--
                showFlashcard(flashcards[currentIndex])
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showFlashcard(card: Flashcard) {
        startText.visibility = View.INVISIBLE
        questionText.visibility = View.VISIBLE
        correctAnswerText.visibility = View.INVISIBLE
        nextButton.visibility = View.VISIBLE
        previousButton.visibility = View.INVISIBLE
        addQuestionButton.visibility = View.VISIBLE
        questionText.text = card.question
        progressText.visibility = View.VISIBLE
        correctAnswerText.text = card.answer
        updateUI()
    }

    private fun showStartScreen() {
        startText.visibility = View.VISIBLE
        questionText.visibility = View.INVISIBLE
        correctAnswerText.visibility = View.INVISIBLE
        addQuestionButton.visibility = View.VISIBLE
        nextButton.visibility = View.INVISIBLE
        previousButton.visibility = View.INVISIBLE
        progressText.visibility = View.GONE

    }

    private fun updateUI() {
        previousButton.visibility = if (currentIndex > 0) View.VISIBLE else View.INVISIBLE
        nextButton.visibility = if (currentIndex < flashcards.size - 1) View.VISIBLE else View.INVISIBLE
        progressText.text = "${currentIndex + 1}/${flashcards.size}"
    }
}
