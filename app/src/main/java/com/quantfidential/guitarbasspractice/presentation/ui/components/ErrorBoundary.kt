package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ErrorBoundary(
    error: Throwable?,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (error != null) {
        ErrorDisplay(
            error = error,
            onRetry = onRetry,
            onDismiss = onDismiss,
            modifier = modifier
        )
    } else {
        content()
    }
}

@Composable
fun ErrorDisplay(
    error: Throwable,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = error.message ?: "An unexpected error occurred",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                onRetry?.let { retry ->
                    FilledTonalButton(
                        onClick = retry,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retry")
                    }
                }
                
                onDismiss?.let { dismiss ->
                    OutlinedButton(
                        onClick = dismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingBoundary(
    isLoading: Boolean,
    loadingMessage: String = "Loading...",
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        LoadingDisplay(
            message = loadingMessage,
            modifier = modifier
        )
    } else {
        content()
    }
}

@Composable
fun LoadingDisplay(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

// State holder for error boundary state
@Stable
class ErrorBoundaryState(
    initialError: Throwable? = null
) {
    private var _error by mutableStateOf(initialError)
    val error: Throwable? get() = _error
    
    fun setError(throwable: Throwable?) {
        _error = throwable
    }
    
    fun clearError() {
        _error = null
    }
    
    fun hasError(): Boolean = _error != null
}

@Composable
fun rememberErrorBoundaryState(
    initialError: Throwable? = null
): ErrorBoundaryState {
    return remember { ErrorBoundaryState(initialError) }
}

@Preview(showBackground = true)
@Composable
fun ErrorBoundaryPreview() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ErrorDisplay(
                error = RuntimeException("Network connection failed"),
                onRetry = { /* Handle retry */ },
                onDismiss = { /* Handle dismiss */ }
            )
            
            LoadingDisplay(
                message = "Creating your exercise..."
            )
        }
    }
}