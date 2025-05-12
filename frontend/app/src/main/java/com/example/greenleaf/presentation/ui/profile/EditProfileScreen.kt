package com.example.greenleaf.presentation.ui.profile
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil3.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.greenleaf.presentation.components.MainBottomBar
import com.example.greenleaf.presentation.viewmodels.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()

    LaunchedEffect(isSaved) {
        if (isSaved) navController.navigateUp()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveProfile(context, selectedImageUri) },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        },
        bottomBar = { MainBottomBar(navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUserProfile() }) {
                            Text("Retry")
                        }
                    }
                }

                user != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Profile photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else if (!user!!.profileImage.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = user!!.profileImage,
                                        contentDescription = "Profile photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Tap to select photo",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Set New Photo",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            "First Name",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        TextField(
                            value = user!!.firstName.orEmpty(),
                            onValueChange = { viewModel.updateFirstName(it) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Gray,
                                disabledIndicatorColor = Color.LightGray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Last Name",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        TextField(
                            value = user!!.lastName.orEmpty(),
                            onValueChange = { viewModel.updateLastName(it) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Gray,
                                disabledIndicatorColor = Color.LightGray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Birth Date",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                TextField(
                                    value = user!!.birthdate.orEmpty(),
                                    onValueChange = { viewModel.updateBirthdate(it) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color.Black,
                                        unfocusedIndicatorColor = Color.Gray,
                                        disabledIndicatorColor = Color.LightGray,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent
                                    )
                                )
                            }

                            // >>> REPLACED GENDER DROPDOWN ONLY
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Gender",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                var expanded by remember { mutableStateOf(false) }
                                val options = listOf("Male", "Female")

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    TextField(
                                        readOnly = true,
                                        value = user!!.gender.orEmpty(),
                                        onValueChange = {},
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                        },
                                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                                            focusedIndicatorColor = Color.Black,
                                            unfocusedIndicatorColor = Color.Gray
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        options.forEach { selection ->
                                            DropdownMenuItem(
                                                text = { Text(selection) },
                                                onClick = {
                                                    viewModel.updateGender(selection)
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            "Email",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        TextField(
                            value = user!!.email,
                            onValueChange = {},
                            singleLine = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Gray,
                                disabledIndicatorColor = Color.LightGray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(Modifier.height(24.dp))

                        Text(
                            "Mobile",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        TextField(
                            value = user!!.phoneNumber.orEmpty(),
                            onValueChange = { viewModel.updatePhoneNumber(it) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Gray,
                                disabledIndicatorColor = Color.LightGray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

