package com.anynetwork.app.ui.screens.newcontact

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.anynetwork.app.R
import com.anynetwork.app.ui.base.NavigateBack
import com.anynetwork.app.ui.components.HexagonTextField
import com.anynetwork.app.ui.components.NavigationIconState
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.components.hexagon.CustomHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.ImageHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.RoundedHexagon
import com.anynetwork.app.ui.components.hexagon.RoundedPolygonShape
import com.anynetwork.app.ui.components.hexagon.createPolygon
import com.anynetwork.app.ui.components.textfield.ProfileTextFieldLeading
import com.anynetwork.app.ui.screens.newcontact.NewContactViewAction.*
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.PrimaryColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.absoluteValue

@Composable
fun NewContactRoot(navController: NavController, input: String?) {
    val viewModel: NewContactViewModel = hiltViewModel<NewContactViewModel>().apply {
        val navigateEvent by navigationEvents.collectAsState()
        if (navigateEvent == NavigateBack) {
            navController.popBackStack()
            resetNavigationState()
        }
    }
    NewContact(
        input = input,
        viewModel = viewModel,
        onContactCreated = { navController.popBackStack() },
        onBackButtonClick = { navController.popBackStack() }
    )
}

@Composable
private fun NewContact(
    input: String?,
    onContactCreated: () -> Unit,
    onBackButtonClick: () -> Unit,
    viewModel: NewContactViewModel
) {
    val context = LocalContext.current

    val viewState by viewModel.viewState.collectAsState()
    val firstName by remember {
        derivedStateOf {
            viewState.firstName
        }
    }
    val lastName by remember {
        derivedStateOf {
            viewState.lastName
        }
    }
    val phone by remember {
        derivedStateOf {
            viewState.phone
        }
    }
    val email by remember {
        derivedStateOf {
            viewState.email
        }
    }
    val photoUri by remember {
        derivedStateOf {
            viewState.photoUri
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Persist the permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.onViewAction(UpdatePhotoUri(it.toString()))
        }
    }

    if (input?.isValidEmail() == true) {
        viewModel.onViewAction(UpdateEmail(input))
    } else if (input?.isValidPhone() == true) {
        viewModel.onViewAction(UpdatePhone(input))
    } else if (input?.isNotEmpty() == true) {
        val words = input.trim().split("\\s+".toRegex())

        // Set the first word as firstName, and join the rest as lastName
        val firstName = words.firstOrNull() ?: ""
        val lastName = if (words.size > 1) words.drop(1).joinToString(" ") else ""

        viewModel.onViewAction(UpdateFirstName(firstName))
        viewModel.onViewAction(UpdateLastName(lastName))
    }

    Screen(
        topBar = ToolbarState.Shown(
            navigationIconState = NavigationIconState.Custom {
                IconButton(onClick = {
                    onBackButtonClick.invoke()
                }) {
                    Image(
                        painter = painterResource(R.drawable.ic_back_arrow),
                        contentDescription = "hamburger menu icon",
                    )
                }
            },
            titleState = ToolbarStateTitle.Custom(
                content = {
                    Text(
                        modifier = Modifier,
                        text = "New Contact",
                        textAlign = TextAlign.Center,
                        color = Color(0xFFFFFFFF),
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.fsp,
                        )
                    )
                }
            ),
            actions = {
                Button(
                    onClick = {
                        viewModel.onViewAction(SaveButtonClick)
                    },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(all = 0.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Save",
                        textAlign = TextAlign.Center,
                        color = GreenColor,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.csp,
                        )
                    )
                }
            },
        ),
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val polygon = remember { createPolygon() }
                val roundedPolygonShape = remember { RoundedPolygonShape(polygon) }
                RoundedHexagon(
                    modifier = Modifier
                        .padding(top = 115.fdpv)
                        .width(105.31.fdph)
                        .aspectRatio(79.93f / 89.99f)
                        .then(Modifier.graphicsLayer {
                            this.shadowElevation = shadowElevation
                            clip = true
                            shape = roundedPolygonShape
                        }),
                    contentStyle = if (photoUri == null) CustomHexagonContentStyle(
                        id = 0,
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF6E4CD4))
                            ) {
                                Image(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 29.fdpv)
                                        .width(40.37.fdph)
                                        .height(47.fdpv),
                                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.4f)),
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile),
                                    contentDescription = null,
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 11.fdpv)
                                        .size(24.fdpv)
                                        .clip(CircleShape)
                                        .background(PrimaryColor)
                                        .border(
                                            width = 1.fdpv,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .clickable { },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = 10.18f / 24),
                                        painter = painterResource(R.drawable.ic_edit_only_pen),
                                        contentDescription = "edit profile",
                                    )
                                }
                            }
                        },
                        onClick = { _ ->
                            imagePickerLauncher.launch("image/*")
                        }
                    ) else {
                        ImageHexagonContentStyle(
                            id = 0,
                            image = ImageHexagonContentStyle.Image.FromUri(
                                uri = photoUri!!
                            ),
                        )
                    }
                )

                Column(modifier = Modifier
                    .padding(top = 19.54.fdpv)
                    .padding(horizontal = 16.fdph)) {
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        HexagonTextField(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.fdpv),
                            value = firstName,
                            onValueChange = { newValue ->
                                viewModel.onViewAction(UpdateFirstName(newValue ?: ""))
                            },
                            placeholder = "First Name"
                        )

                        Spacer(modifier = Modifier.width(15.fdph))

                        HexagonTextField(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.fdpv),
                            value = lastName,
                            onValueChange = { newValue ->
                                viewModel.onViewAction(UpdateLastName(newValue ?: ""))
                            },
                            placeholder = "Last Name"
                        )
                    }

                    var company by remember { mutableStateOf("") }
                    HexagonTextField(
                        modifier = Modifier
                            .padding(top = 16.fdpv)
                            .height(54.fdpv),
                        value = company,
                        onValueChange = { newValue -> company = newValue ?: ""},
                        placeholder = "Company"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 21.fdpv),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier,
                            text = "Contact Info",
                            textAlign = TextAlign.Center,
                            color = Color(0xFFFFFFFF),
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.fsp,
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Add",
                            textAlign = TextAlign.Center,
                            color = GreenColor,
                            style = TextStyle(
                                fontFamily = montserratFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.fsp,
                            )
                        )
                    }

                    email?.let { email ->
                        HexagonTextField(
                            modifier = Modifier
                                .padding(top = 23.fdpv)
                                .height(54.fdpv),
                            value = email,
                            onValueChange = { newValue ->
                                viewModel.onViewAction(UpdateEmail(newValue))
                            },
                            leadingIcon = {
                                ProfileTextFieldLeading("Email")
                            }
                        )
                    }

                    phone?.let { phone ->
                        HexagonTextField(
                            modifier = Modifier
                                .padding(top = 16.fdpv)
                                .height(54.fdpv),
                            value = phone,
                            onValueChange = { newValue ->
                                viewModel.onViewAction(UpdatePhone(newValue))
                            },
                            leadingIcon = {
                                ProfileTextFieldLeading("Phone")
                            }
                        )
                    }
                }
            }
        }
    )
}

private fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    return this.matches(emailRegex)
}

private fun String.isValidPhone(): Boolean {
    val phoneRegex = "^\\+?[0-9]{1,3}?[-.\\s]?\\(?[0-9]{1,4}?\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}\$".toRegex()
    return this.matches(phoneRegex)
}

suspend fun loadBitmapFromUri(uri: String, context: android.content.Context): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val imageUri = Uri.parse(uri)
            return@withContext if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun addContactDirectly(
    context: Context,
    name: String,
    phone: String,
    email: String?,
    photoUri: Uri? = null,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val contentResolver: ContentResolver = context.contentResolver

            // List to hold multiple operations for bulk insert
            val ops = ArrayList<ContentProviderOperation>()

            // Insert a new raw contact into the ContactsContract.RawContacts table
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            // Insert the contact's name
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build()
            )

            // Insert the contact's phone number
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build()
            )

            // Insert the contact's email, if provided
            if (email != null) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build()
                )
            }

            // Insert the contact's photo URI, if provided
            photoUri?.let { uri ->
                val photoBytes = getBytesFromImageUri(contentResolver, uri)
                if (photoBytes != null) {
                    ops.add(
                        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photoBytes)
                            .build()
                    )
                }
            }

            // Apply the batch of insert operations to the content provider
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)

            // Show success message on the main thread
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Show error message on the main thread
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Failed to add contact", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun getBytesFromImageUri(contentResolver: ContentResolver, photoUri: Uri?): ByteArray? {
    return try {
        val inputStream = photoUri?.let { contentResolver.openInputStream(it) }
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Resize the bitmap to reduce its size (e.g., 512x512)
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 512, 512, true)

        val stream = ByteArrayOutputStream()
        // Compress the bitmap to JPEG format with a quality of 70%
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        stream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}