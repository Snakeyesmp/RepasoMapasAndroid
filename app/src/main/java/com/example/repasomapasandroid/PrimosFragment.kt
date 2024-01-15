package com.example.repasomapasandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrimeFragment : Fragment() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_primos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberInput: EditText = view.findViewById(R.id.number_input)
        val calculateButton: Button = view.findViewById(R.id.calculate_button)
        val resultText: TextView = view.findViewById(R.id.result_text)

        calculateButton.setOnClickListener {
            val number = numberInput.text.toString().toIntOrNull()
            if (number != null) {
                scope.launch {
                    val isPrime = withContext(Dispatchers.Default) { calculatePrime(number) }
                    resultText.text = if (isPrime) "$number is a prime number" else "$number is not a prime number"
                }
            } else {
                resultText.text = "Please enter a valid number"
            }
        }
    }

    private fun calculatePrime(num: Int): Boolean {
        if (num <= 1) return false
        for (i in 2 until num) {
            if (num % i == 0) return false
        }
        return true
    }
}