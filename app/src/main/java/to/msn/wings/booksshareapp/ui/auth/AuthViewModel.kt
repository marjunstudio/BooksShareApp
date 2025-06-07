package to.msn.wings.booksshareapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import to.msn.wings.booksshareapp.data.model.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        auth.currentUser?.let { user ->
            _authState.value = AuthState(
                isAuthenticated = true,
                userId = user.uid
            )
            _currentUser.value = user
            Log.d("AuthViewModel", "Current user found: ${user.uid}")
        } ?: run {
            Log.d("AuthViewModel", "No current user")
        }
    }

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting sign in with credential")
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    Log.d("AuthViewModel", "Sign in successful: ${user.uid}")
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        userId = user.uid
                    )
                    _currentUser.value = user
                } ?: run {
                    Log.e("AuthViewModel", "Sign in failed: No user returned")
                    _authState.value = AuthState(
                        isAuthenticated = false,
                        error = "ログインに失敗しました"
                    )
                    _currentUser.value = null
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign in error", e)
                _authState.value = AuthState(
                    isAuthenticated = false,
                    error = "ログインエラー: ${e.message}"
                )
                _currentUser.value = null
            }
        }
    }

    fun signOut() {
        try {
            auth.signOut()
            Log.d("AuthViewModel", "Sign out successful")
            _authState.value = AuthState()
            _currentUser.value = null
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Sign out error", e)
            _authState.value = AuthState(
                error = "ログアウトエラー: ${e.message}"
            )
        }
    }
} 