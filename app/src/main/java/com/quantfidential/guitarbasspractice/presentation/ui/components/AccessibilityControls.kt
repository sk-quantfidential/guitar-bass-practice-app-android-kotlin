package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quantfidential.guitarbasspractice.presentation.ui.theme.ColorSchemeType
import com.quantfidential.guitarbasspractice.presentation.ui.theme.ThemeMode

data class AccessibilitySettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorSchemeType: ColorSchemeType = ColorSchemeType.DEFAULT,
    val dynamicColor: Boolean = true,
    val fontSizeScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val screenReaderEnabled: Boolean = false,
    val hapticFeedback: Boolean = true,
    val audioFeedback: Boolean = true
)

@Composable
fun AccessibilitySettingsDialog(
    settings: AccessibilitySettings,
    onSettingsChanged: (AccessibilitySettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Accessibility,
                    contentDescription = "Accessibility",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Accessibility Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Mode Selection
                AccessibilitySection(
                    title = "Theme Mode",
                    description = "Choose your preferred theme"
                ) {
                    ThemeModeSelector(
                        selectedMode = settings.themeMode,
                        onModeSelected = { onSettingsChanged(settings.copy(themeMode = it)) }
                    )
                }
                
                // Color Scheme Selection
                AccessibilitySection(
                    title = "Color Scheme",
                    description = "Choose colors optimized for accessibility"
                ) {
                    ColorSchemeSelector(
                        selectedScheme = settings.colorSchemeType,
                        onSchemeSelected = { onSettingsChanged(settings.copy(colorSchemeType = it)) }
                    )
                }
                
                // Font Size Scale
                AccessibilitySection(
                    title = "Font Size",
                    description = "Adjust text size for better readability"
                ) {
                    FontSizeSelector(
                        scale = settings.fontSizeScale,
                        onScaleChanged = { onSettingsChanged(settings.copy(fontSizeScale = it)) }
                    )
                }
                
                // Accessibility Toggles
                AccessibilitySection(
                    title = "Features",
                    description = "Enable accessibility features"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccessibilityToggle(
                            label = "High Contrast",
                            description = "Increase contrast for better visibility",
                            checked = settings.highContrast,
                            onCheckedChange = { onSettingsChanged(settings.copy(highContrast = it)) }
                        )
                        
                        AccessibilityToggle(
                            label = "Reduce Motion",
                            description = "Minimize animations and transitions",
                            checked = settings.reduceMotion,
                            onCheckedChange = { onSettingsChanged(settings.copy(reduceMotion = it)) }
                        )
                        
                        AccessibilityToggle(
                            label = "Haptic Feedback",
                            description = "Vibration feedback for interactions",
                            checked = settings.hapticFeedback,
                            onCheckedChange = { onSettingsChanged(settings.copy(hapticFeedback = it)) }
                        )
                        
                        AccessibilityToggle(
                            label = "Audio Feedback",
                            description = "Sound cues for interactions",
                            checked = settings.audioFeedback,
                            onCheckedChange = { onSettingsChanged(settings.copy(audioFeedback = it)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun AccessibilitySection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            content()
        }
    }
}

@Composable
private fun ThemeModeSelector(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        ThemeMode.values().forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedMode == mode,
                        onClick = { onModeSelected(mode) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMode == mode,
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (mode) {
                        ThemeMode.SYSTEM -> "System Default"
                        ThemeMode.LIGHT -> "Light Theme"
                        ThemeMode.DARK -> "Dark Theme"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ColorSchemeSelector(
    selectedScheme: ColorSchemeType,
    onSchemeSelected: (ColorSchemeType) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        ColorSchemeType.values().forEach { scheme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedScheme == scheme,
                        onClick = { onSchemeSelected(scheme) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedScheme == scheme,
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (scheme) {
                        ColorSchemeType.DEFAULT -> "Default Colors"
                        ColorSchemeType.COLOR_BLIND_FRIENDLY -> "Color-blind Friendly"
                        ColorSchemeType.HIGH_CONTRAST -> "High Contrast"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FontSizeSelector(
    scale: Float,
    onScaleChanged: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Size: ${(scale * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Sample Text",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize * scale
                )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = scale,
            onValueChange = onScaleChanged,
            valueRange = 0.8f..1.5f,
            steps = 6
        )
    }
}

@Composable
private fun AccessibilityToggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccessibilitySettingsDialogPreview() {
    MaterialTheme {
        AccessibilitySettingsDialog(
            settings = AccessibilitySettings(),
            onSettingsChanged = {},
            onDismiss = {}
        )
    }
}