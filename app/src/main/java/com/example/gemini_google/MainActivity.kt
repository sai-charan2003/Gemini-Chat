package com.example.gemini_google

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.example.gemini_google.ui.theme.Gemini_googleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Gemini_googleTheme {


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(topBar = {
                        TopAppBar(title = { Text(text = "Gemini Chat")})
                    }) {
                        MainContent(it = it)
                    }
                }
            }
        }
    }
}

    @Composable

    fun MainContent(it:PaddingValues) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background,
        ) {
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro",
                apiKey = BuildConfig.apiKey
            )


            val SummarizeViewModel = SummarizeViewModel(generativeModel)

            val userCommands by SummarizeViewModel.userCommands.observeAsState()

            val isloading by SummarizeViewModel.isLoading.collectAsState()


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    userCommands?.let {
                        items(it.size) {
                            Row(modifier=Modifier.fillMaxSize()) {
                                Icon(painter = painterResource(id = R.drawable.round_person_24), contentDescription = null)
                                Text("You",textAlign = TextAlign.Center,modifier=Modifier.padding(top=3.dp,bottom=10.dp))

                            }
                            Text(text = userCommands!![it].input, modifier=Modifier.padding(start = 29.dp, bottom = 10.dp))
                            Row(modifier=Modifier.fillMaxSize()) {
                                Icon(painter = painterResource(id = R.drawable.vector), contentDescription =null )
                                Text(text = "Gemini",modifier=Modifier.padding(start=5.dp,top=3.dp,bottom=10.dp), textAlign = TextAlign.Center)
                            }

                            Text(text = userCommands!![it].output,modifier=Modifier.padding(start=29.dp,bottom=10.dp))
                        }
                    }
                }

                ChatInput(
                    onMessageSent = { messageText ->
                        SummarizeViewModel.setUserInput(messageText)
                    },
                    summarizeViewModel = SummarizeViewModel
                )
            }
        }
    }

    @Composable
    fun ChatInput(onMessageSent: (String) -> Unit,summarizeViewModel: SummarizeViewModel) {
        val isloading by summarizeViewModel.isLoading.collectAsState()

        var message by remember { mutableStateOf("") }



        OutlinedTextField(
            shape = RoundedCornerShape(10.dp),

            value = message,
            placeholder = {
                if(!isloading) {

                    Text(text = "Enter Prompt")
                }
                else{
                    Text(text = message)
                }

                          },

            onValueChange = { message = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                ,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            enabled = !isloading,
            keyboardActions = KeyboardActions(
                onSend = {
                    if (message.isNotBlank()) {
                        onMessageSent(message)

                    }
                }
            ),
            trailingIcon = {
                Row {
                    if(message.isNotBlank()){
                        IconButton(onClick = { message="" }) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = null)

                        }
                    }
                    IconButton(onClick = {
                        if (message.isNotBlank()) {
                            onMessageSent(message)

                        }
                    }, enabled = message.isNotBlank()) {
                        if (!isloading) {

                            Icon(imageVector = Icons.Filled.Send, contentDescription = null)
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(25.dp),
                                strokeCap = StrokeCap.Round
                            )
                        }

                    }


                }
            }
        )


    }





