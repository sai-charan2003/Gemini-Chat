package com.example.gemini_google

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummarizeViewModel(
        private val generativeModel: GenerativeModel
) : ViewModel() {
    private val _userCommands = MutableLiveData<List<ChatMessage>>()
    val userCommands: LiveData<List<ChatMessage>> get() = _userCommands
    private var _isloading= MutableStateFlow(false)
    var isLoading=_isloading.asStateFlow()

    private val _userInput = MutableLiveData<String>()
    val userInput: LiveData<String> get() = _userInput

    private val _chatGptOutput = MutableLiveData<String>()
    val chatGptOutput: LiveData<String> get() = _chatGptOutput

    fun setUserInput(input: String) {
        _isloading.value=true
        _userInput.value = input
        val currentCommands = _userCommands.value.orEmpty().toMutableList()
        currentCommands.add(ChatMessage(input = input, output = ""))
        promtoutput(input)
    }

    fun setChatGptOutput(output: String) {
        _chatGptOutput.value = output
        // Add the user's input and ChatGPT's output to user commands list
        val currentCommands = _userCommands.value.orEmpty().toMutableList()
        currentCommands.add(ChatMessage(input = _userInput.value.orEmpty(), output = output))
        _userCommands.value = currentCommands
        _isloading.value=false
    }


    fun promtoutput(inputText: String) {


        val prompt = inputText

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { setChatGptOutput(it) }


            } catch (e: Exception) {

            }
        }


    }
}