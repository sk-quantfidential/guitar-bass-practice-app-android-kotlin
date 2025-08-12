package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quantfidential.guitarbasspractice.domain.model.UserProfile
import com.quantfidential.guitarbasspractice.domain.model.InstrumentType
import com.quantfidential.guitarbasspractice.domain.model.DifficultyLevel

@Composable
fun UserProfileSelector(
    profiles: List<UserProfile>,
    activeProfile: UserProfile?,
    onProfileSelected: (UserProfile) -> Unit,
    onAddProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onAddProfile,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        isActive = profile.id == activeProfile?.id,
                        onClick = { onProfileSelected(profile) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    profile: UserProfile,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.take(2).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isActive) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Profile name
            Text(
                text = profile.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Instrument and skill level
            Text(
                text = "${profile.primaryInstrument.name.take(3)} • ${profile.skillLevel.name.take(3)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun CreateProfileDialog(
    onDismiss: () -> Unit,
    onCreateProfile: (String, InstrumentType, DifficultyLevel, List<String>) -> Unit,
    @Suppress("UNUSED_PARAMETER") modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedInstrument by remember { mutableStateOf(InstrumentType.GUITAR) }
    var selectedSkillLevel by remember { mutableStateOf(DifficultyLevel.BEGINNER) }
    var expandedInstrument by remember { mutableStateOf(false) }
    var expandedSkillLevel by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Profile Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Instrument selector
                ExposedDropdownMenuBox(
                    expanded = expandedInstrument,
                    onExpandedChange = { expandedInstrument = it }
                ) {
                    OutlinedTextField(
                        value = selectedInstrument.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Instrument") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedInstrument) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedInstrument,
                        onDismissRequest = { expandedInstrument = false }
                    ) {
                        InstrumentType.values().forEach { instrument ->
                            DropdownMenuItem(
                                text = { Text(instrument.name) },
                                onClick = {
                                    selectedInstrument = instrument
                                    expandedInstrument = false
                                }
                            )
                        }
                    }
                }
                
                // Skill level selector
                ExposedDropdownMenuBox(
                    expanded = expandedSkillLevel,
                    onExpandedChange = { expandedSkillLevel = it }
                ) {
                    OutlinedTextField(
                        value = selectedSkillLevel.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Skill Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSkillLevel) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSkillLevel,
                        onDismissRequest = { expandedSkillLevel = false }
                    ) {
                        DifficultyLevel.values().forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.name) },
                                onClick = {
                                    selectedSkillLevel = level
                                    expandedSkillLevel = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreateProfile(name, selectedInstrument, selectedSkillLevel, emptyList())
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UserProfileSelectorPreview() {
    MaterialTheme {
        UserProfileSelector(
            profiles = listOf(
                UserProfile(
                    id = "1",
                    name = "John Guitar",
                    primaryInstrument = InstrumentType.GUITAR,
                    skillLevel = DifficultyLevel.INTERMEDIATE,
                    preferredKeys = listOf("C", "G", "D"),
                    favoriteGenres = listOf("Rock", "Blues"),
                    createdTimestamp = System.currentTimeMillis(),
                    isActive = true
                ),
                UserProfile(
                    id = "2",
                    name = "Jane Bass",
                    primaryInstrument = InstrumentType.BASS,
                    skillLevel = DifficultyLevel.ADVANCED,
                    preferredKeys = listOf("E", "A"),
                    favoriteGenres = listOf("Jazz", "Funk"),
                    createdTimestamp = System.currentTimeMillis(),
                    isActive = false
                )
            ),
            activeProfile = UserProfile(
                id = "1",
                name = "John Guitar",
                primaryInstrument = InstrumentType.GUITAR,
                skillLevel = DifficultyLevel.INTERMEDIATE,
                preferredKeys = listOf("C", "G", "D"),
                favoriteGenres = listOf("Rock", "Blues"),
                createdTimestamp = System.currentTimeMillis(),
                isActive = true
            ),
            onProfileSelected = {},
            onAddProfile = {}
        )
    }
}