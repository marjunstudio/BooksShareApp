package to.msn.wings.booksshareapp.ui.profile

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import to.msn.wings.booksshareapp.BuildConfig
import to.msn.wings.booksshareapp.ui.auth.AuthViewModel
import to.msn.wings.booksshareapp.util.showToast

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.authState.collectAsState()

    // Google Sign-In の設定
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.WEB_CLIENT_ID)
        .requestEmail()
        .build()

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("ProfileScreen", "Result code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {  // -1が成功コード
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                Log.d("ProfileScreen", "Google sign in result received")
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    Log.d("ProfileScreen", "Google sign in successful, token received")
                    val credential = GoogleAuthProvider.getCredential(token, null)
                    viewModel.signInWithCredential(credential)
                } ?: run {
                    Log.e("ProfileScreen", "Google sign in failed: No ID token")
                    context.showToast("ログインに失敗しました: トークンが取得できません")
                }
            } catch (e: ApiException) {
                Log.e("ProfileScreen", "Google sign in failed", e)
                context.showToast("ログインに失敗しました: ${e.message}")
            }
        } else {
            Log.d("ProfileScreen", "Google sign in cancelled with code: ${result.resultCode}")
            context.showToast("ログインがキャンセルされました")
        }
    }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            context.showToast("ログインしました")
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            context.showToast(error)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isAuthenticated) {
                Text(
                    text = "ログイン中",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        viewModel.signOut()
                        context.showToast("ログアウトしました")
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("ログアウト")
                }
            } else {
                Text(
                    text = "ログインしていません",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Googleでログイン")
                }
            }

            state.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
} 