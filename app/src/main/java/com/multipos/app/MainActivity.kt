package com.multipos.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.multipos.app.theme.MultiPosTheme
import com.multipos.app.screens.LoginScreen
import com.multipos.app.screens.POSScreen
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultiPosTheme {
                var user by remember { mutableStateOf<Triple<String, String, String>?>(null) } // username, role, storeName

                if (user == null) {
                    LoginScreen(onLogin = { username, role, storeName ->
                        Log.d("Login", "Logged in: $username as $role at $storeName")
                        user = Triple(username, role, storeName)
                    })
                } else {
                    // unwrap safely
                    val u = user!!
                    POSScreen(
                        storeName = u.third,
                        username = u.first,
                        role = u.second,
                        onLogout = { user = null }
                    )
                }
            }
        }
    }
}
