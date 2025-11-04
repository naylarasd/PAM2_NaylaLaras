package nl.mobile.tugaspam2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import nl.mobile.tugaspam2.ui.theme.TugasPAM2Theme

class MainActivity : ComponentActivity() {
    private val formViewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TugasPAM2Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FormScreen(viewModel = formViewModel)
                }
            }
        }
    }
}

// ===============================
// ViewModel dan UI di file yang sama
// ===============================

data class FormUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val fullName: String = "",
    val errorFirstName: String? = null,
    val errorLastName: String? = null,
    val errorEmail: String? = null
)

class FormViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState

    fun onFirstNameChange(value: String) {
        _uiState.update { it.copy(firstName = value, errorFirstName = null) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value, errorLastName = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorEmail = null) }
    }

    fun onSubmit() {
        val state = _uiState.value
        var valid = true

        val firstNameError = if (state.firstName.isBlank()) {
            valid = false; "Nama depan wajib diisi"
        } else null

        val lastNameError = if (state.lastName.isBlank()) {
            valid = false; "Nama belakang wajib diisi"
        } else null

        val emailError = if (!state.email.contains("@")) {
            valid = false; "Email tidak valid"
        } else null

        if (valid) {
            val full = "${state.firstName.trim()} ${state.lastName.trim()}"
            _uiState.update { it.copy(fullName = full) }
        } else {
            _uiState.update {
                it.copy(
                    errorFirstName = firstNameError,
                    errorLastName = lastNameError,
                    errorEmail = emailError
                )
            }
        }
    }
}

@Composable
fun FormScreen(viewModel: FormViewModel) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.firstName,
            onValueChange = viewModel::onFirstNameChange,
            label = { Text("Nama Depan") },
            isError = uiState.errorFirstName != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.errorFirstName != null) {
            Text(
                text = uiState.errorFirstName ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.lastName,
            onValueChange = viewModel::onLastNameChange,
            label = { Text("Nama Belakang") },
            isError = uiState.errorLastName != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.errorLastName != null) {
            Text(
                text = uiState.errorLastName ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            isError = uiState.errorEmail != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.errorEmail != null) {
            Text(
                text = uiState.errorEmail ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { viewModel.onSubmit() }, modifier = Modifier.fillMaxWidth()) {
            Text("Submit")
        }

        Spacer(Modifier.height(24.dp))

        if (uiState.fullName.isNotBlank()) {
            Text(
                text = "Nama Lengkap: ${uiState.fullName}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForm() {
    TugasPAM2Theme {
        FormScreen(viewModel = FormViewModel())
    }
}
