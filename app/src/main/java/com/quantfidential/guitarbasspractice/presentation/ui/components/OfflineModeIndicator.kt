package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quantfidential.guitarbasspractice.util.OfflineCapabilities

@Composable
fun OfflineModeIndicator(
    isOnline: Boolean,
    @Suppress("UNUSED_PARAMETER") capabilities: OfflineCapabilities,
    modifier: Modifier = Modifier
) {
    if (!isOnline) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.WifiOff,
                    contentDescription = "Offline",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Offline Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Core features available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun FeatureAvailabilityIndicator(
    featureName: String,
    isAvailable: Boolean,
    isOnlineRequired: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        isAvailable -> MaterialTheme.colorScheme.primaryContainer
        isOnlineRequired -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        isAvailable -> MaterialTheme.colorScheme.onPrimaryContainer
        isOnlineRequired -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                when {
                    isAvailable -> Icons.Default.CheckCircle
                    isOnlineRequired -> Icons.Default.CloudOff
                    else -> Icons.Default.Block
                },
                contentDescription = if (isAvailable) "Available" else "Unavailable",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = featureName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                
                Text(
                    text = when {
                        isAvailable -> "Available"
                        isOnlineRequired -> "Requires internet connection"
                        else -> "Unavailable"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun OfflineCapabilitiesDialog(
    capabilities: OfflineCapabilities,
    isOnline: Boolean,
    onDismiss: () -> Unit,
    @Suppress("UNUSED_PARAMETER") modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOnline) "Online Mode" else "Offline Mode",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = if (isOnline) 
                            "All features are available with internet connection." 
                        else 
                            "Core features are available offline. Online features require internet connection.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "Exercise Creation & Storage",
                        isAvailable = capabilities.exerciseCreation
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "Exercise Playback",
                        isAvailable = capabilities.exercisePlayback
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "Fretboard Visualization",
                        isAvailable = capabilities.fretboardVisualization
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "Notation Rendering",
                        isAvailable = capabilities.notationRendering
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "User Profiles",
                        isAvailable = capabilities.userProfiles
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "Exercise Customization",
                        isAvailable = capabilities.customization
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "AI Exercise Generation",
                        isAvailable = capabilities.aiGeneration,
                        isOnlineRequired = !capabilities.aiGeneration && !isOnline
                    )
                }
                
                item {
                    FeatureAvailabilityIndicator(
                        featureName = "Cloud Sync",
                        isAvailable = capabilities.cloudSync,
                        isOnlineRequired = !capabilities.cloudSync && !isOnline
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun OfflineModeIndicatorPreview() {
    MaterialTheme {
        Column {
            OfflineModeIndicator(
                isOnline = false,
                capabilities = OfflineCapabilities()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FeatureAvailabilityIndicator(
                featureName = "AI Exercise Generation",
                isAvailable = false,
                isOnlineRequired = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            FeatureAvailabilityIndicator(
                featureName = "Exercise Playback",
                isAvailable = true
            )
        }
    }
}