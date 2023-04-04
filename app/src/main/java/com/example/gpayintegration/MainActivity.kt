package com.example.gpayintegration

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.gpayintegration.ui.theme.GPayIntegrationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GPayIntegrationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PaymentUI()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PaymentUI() {
    CheckInternetService()
    val ctx = LocalContext.current
    var name by remember {
        mutableStateOf(TextFieldValue())
    }
    var upiId by remember {
        mutableStateOf(TextFieldValue())
    }
    var description by remember {
        mutableStateOf(TextFieldValue())
    }
    var amount by remember {
        mutableStateOf(TextFieldValue())
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "GPAY Integration")})
    }) {
        Box(modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
            contentAlignment = Alignment.Center
            ) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                OutlinedTextField(value = name, onValueChange = {name = it}, modifier = Modifier.padding(10.dp),
                        placeholder = { Text(text = "Enter name of the payee")}, singleLine = true
                    )
                OutlinedTextField(value = upiId, onValueChange = {upiId = it}, modifier = Modifier.padding(10.dp),
                        placeholder = { Text(text = "Enter payee UPI ID")}, singleLine = true
                    )
                OutlinedTextField(value = description, onValueChange = {description = it}, modifier = Modifier.padding(10.dp),
                        placeholder = { Text(text = "Enter description")}, singleLine = true
                    )
                OutlinedTextField(value = amount, onValueChange = {amount = it}, modifier = Modifier.padding(10.dp),
                        placeholder = { Text(text = "Enter the amount")}, singleLine = true
                    )
                Button(onClick = { paymentActivity(ctx,name.text,upiId.text,description.text,amount.text) }, modifier = Modifier.padding(10.dp)) {
                    Text(text = "PROCEED TO PAY")
                }

            }
        }
    }
}

fun paymentActivity(ctx : Context,name: String,upiId: String,description:String,amount:String) {
    val uri = Uri.Builder().scheme("upi")
        .authority("pay")
        .appendQueryParameter("pa",upiId)
        .appendQueryParameter("mc","")
        .appendQueryParameter("tr","25543678")
        .appendQueryParameter("pn",name)
        .appendQueryParameter("tn",description)
        .appendQueryParameter("cu","INR")
        .appendQueryParameter("am",amount).build()

    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = uri
    intent.setPackage("com.google.android.apps.nbu.paisa.user")
    ctx.startActivity(intent)
}

@Composable
fun CheckInternetService() {
    val ctx = LocalContext.current
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    val networkCallback  = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Toast.makeText(ctx,"Connected",Toast.LENGTH_SHORT).show()
            super.onAvailable(network)
        }

        override fun onLost(network: Network) {
            Toast.makeText(ctx,"Check your internet connection",Toast.LENGTH_SHORT).show()
            super.onLost(network)
        }
    }
    val connectivityManager = ctx.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    connectivityManager.requestNetwork(networkRequest, networkCallback)
}
