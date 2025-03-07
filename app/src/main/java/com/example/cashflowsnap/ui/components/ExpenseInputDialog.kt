package com.example.cashflowsnap.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat

@Composable
fun ExpenseInputDialog(
    onDismiss: () -> Unit,
    onAddExpense: (String, String, String, String, Uri?) -> Unit,
    initialAmount: String = "",
    initialCategory: String = "",
    initialDate: String = "",
    initialNote: String = ""
) {
    var amount by remember { mutableStateOf(initialAmount) }
    var date by remember { mutableStateOf(initialDate) }
    var note by remember { mutableStateOf(initialNote) }
    var selectedCategory by remember { mutableStateOf(initialCategory.ifEmpty { "Select Category" }) }
    var picUri by remember { mutableStateOf<Uri?>(null) }
    val categories = listOf("Electronics", "Clothing", "Books", "Home Appliances")
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "receipt_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CashFlowSnap")
                put(MediaStore.Images.Media.DESCRIPTION, "Receipt for CashFlowSnap")
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { imageUri ->
                context.contentResolver.openOutputStream(imageUri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                picUri = imageUri
                println("Saved Uri: $picUri")  // Debug log
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null) else onDismiss()
    }

    fun openImageInGallery(uri: Uri?) {
        uri?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(it, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Add Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                CustomDropdownMenu(
                    items = categories,
                    selectedItem = selectedCategory,
                    onItemSelected = { selectedCategory = it },
                    //modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g., March 2)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Snap Receipt (Optional)", fontSize = 16.sp)
                }
                if (picUri != null) {
                    Button(
                        onClick = { openImageInGallery(picUri) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("View Receipt", fontSize = 16.sp)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", fontSize = 16.sp)
                    }
                    Button(
                        onClick = {
                            if (amount.isNotEmpty() && selectedCategory != "Select Category" && date.isNotEmpty()) {
                                onAddExpense(amount, selectedCategory, date, note, picUri)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        enabled = amount.isNotEmpty() && selectedCategory != "Select Category" && date.isNotEmpty()
                    ) {
                        Text("Save", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}