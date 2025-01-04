package com.anynetwork.app.ui.screens.contactspermissions

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.text.Header
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.button.PrimaryButton
import com.anynetwork.app.ui.components.button.SecondaryButton
import com.anynetwork.app.ui.components.dialog.AlertDialog
import com.anynetwork.app.ui.components.dialog.AlertDialogButtonState
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import java.util.Locale
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp

@Composable
fun ContactsPermissionsRoot(navController: NavHostController, contentResolver: ContentResolver) {
    ContactsPermissions(navController, contentResolver)
}

fun getRevolutAccount(contentResolver: ContentResolver, contactId: Long): String? {
    // MIME type for Revolut account data (this is hypothetical, replace with the actual MIME type if known)
    val REVOLUT_ACCOUNT_MIME_TYPE = "vnd.android.cursor.item/revolut_account"

    // Uri for the Data table
    val dataUri: Uri = ContactsContract.Data.CONTENT_URI

    // Define the projection (columns to retrieve)
    val projection = arrayOf(
        ContactsContract.Data._ID,
        ContactsContract.Data.MIMETYPE,
        ContactsContract.Data.DATA1,
        ContactsContract.Data.DATA2,
        ContactsContract.Data.DATA3,
        ContactsContract.Data.DATA4,
        ContactsContract.Data.DATA5,
        ContactsContract.Data.DATA6,
        ContactsContract.Data.DATA7,
        ContactsContract.Data.DATA8,
        ContactsContract.Data.DATA9,
        ContactsContract.Data.DATA10,
        ContactsContract.Data.DATA11,
        ContactsContract.Data.DATA12,
        ContactsContract.Data.DATA13,
        ContactsContract.Data.DATA14,
        ContactsContract.Data.DATA15
    )

    // Define the selection criteria
    val selection = "${ContactsContract.Data.CONTACT_ID} = ?"
    val selectionArgs = arrayOf(contactId.toString())

    // Query the Data table
    val cursor: Cursor? = contentResolver.query(dataUri, projection, selection, selectionArgs, null)

    cursor?.use {
        if (it.moveToFirst()) {
            // Assuming the data is stored in the DATA1 column
            val revolutAccount = it.getString(it.getColumnIndex(ContactsContract.Data.DATA1))
            return revolutAccount
        }
    }
    return null
}

fun getContactId(contentResolver: ContentResolver, displayName: String): Long? {
    val uri: Uri = ContactsContract.Contacts.CONTENT_URI
    val projection = arrayOf(
        ContactsContract.Data._ID,
        ContactsContract.Data.MIMETYPE,
        ContactsContract.Data.DATA1,
        ContactsContract.Data.DATA2,
        ContactsContract.Data.DATA3,
        ContactsContract.Data.DATA4,
        ContactsContract.Data.DATA5,
        ContactsContract.Data.DATA6,
        ContactsContract.Data.DATA7,
        ContactsContract.Data.DATA8,
        ContactsContract.Data.DATA9,
        ContactsContract.Data.DATA10,
        ContactsContract.Data.DATA11,
        ContactsContract.Data.DATA12,
        ContactsContract.Data.DATA13,
        ContactsContract.Data.DATA14,
        ContactsContract.Data.DATA15
    )
    val selection = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} = ?"
    val selectionArgs = arrayOf(displayName)

    // Query the Contacts Provider
    val cursor = contentResolver.query(
        ContactsContract.Data.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE))
            val data1 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1))
            val data2 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA2))
            val data3 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA3))
            val data4 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA4))
            val data5 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA5))
            val data6 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA6))
            val data7 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA7))
            val data8 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA8))
            val data9 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA9))
            val data10 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA10))
            val data11 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA11))

            // Process the data based on MIME type and data fields
            // Example: Print or process the retrieved data
            println("MIME Type: $mimeType")
            println("Data 1: $data1")
            println("Data 2: $data2")
            println("Data 3: $data3")
            println("Data 4: $data4")
            println("Data 5: $data5")
            println("Data 6: $data6")
            println("Data 7: $data7")
            println("Data 8: $data8")
            println("Data 9: $data9")
            println("Data 10: $data10")
            println("Data 11: $data11")
        }
    }
    return null
}


fun getContacts(contentResolver: ContentResolver): List<String> {
    val contacts = mutableListOf<String>()
    val cursor: Cursor? = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, null
    )
    cursor?.use {
        while (it.moveToNext()) {
            val name = it.getString(
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            )
            contacts.add(name)
        }
    }
    return contacts
}

@Composable
private fun ContactsPermissions(navController: NavHostController, contentResolver: ContentResolver) {
    var showDialog by remember { mutableStateOf(false) }

    val readContactsPermission = remember { mutableStateOf(false) }
    val writeContactsPermission = remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        readContactsPermission.value = permissions[Manifest.permission.READ_CONTACTS] ?: false
        writeContactsPermission.value = permissions[Manifest.permission.WRITE_CONTACTS] ?: false
    }

    Screen {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(236.xdpv))

                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    painter = painterResource(id = R.drawable.contact_permissions_shadow),
                    contentDescription = null
                )
            }

            Column(
                modifier = Modifier.padding(top = 40.7.xdpv, start = 16.xdph, end = 16.xdph),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .width(273.7.xdph)
                        .height(264.19.xdpv)
                        .fillMaxWidth(),
                    painter = painterResource(R.drawable.contact_permissions_image),
                    contentDescription = "contacts permission image",
                )

                Header(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 33.7.xdpv),
                    text = stringResource(R.string.contact_permissions_title),
                )

                SubtitleText(
                    modifier = Modifier
                        .padding(top = 24.7.xdpv, start = 16.xdph, end = 16.xdph),
                    text = stringResource(R.string.contact_permissions_subtitle_1).trimIndent()
                )

                SubtitleText(
                    modifier = Modifier
                        .padding(top = 24.7.xdpv, start = 16.xdph, end = 16.xdph),
                    text = stringResource(R.string.contact_permissions_subtitle_2).trimIndent()
                )

                SubtitleText(
                    modifier = Modifier
                        .padding(top = 30.7.xdpv, start = 16.xdph, end = 16.xdph),
                    text = stringResource(R.string.contact_permissions_subtitle_3).trimIndent()
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrimaryButton(title = stringResource(R.string.contact_permissions_button_allow)) {
                        showDialog = true
                    }

                    Spacer(modifier = Modifier.height(7.xdpv))

                    SecondaryButton(title = stringResource(R.string.title_skip).toUpperCase(Locale.ROOT)) {
                        navController.navigate(Route.Home)
                    }

                    Spacer(modifier = Modifier.height(2.xdpv))
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            title = stringResource(R.string.contact_permissions_dialog_title),
            message = stringResource(R.string.contact_permissions_dialog_subtitle),
            buttons = listOf(
                AlertDialogButtonState(stringResource(R.string.contact_permissions_dialog_button_dont_allow)) {},
                AlertDialogButtonState(stringResource(R.string.contact_permissions_dialog_button_allow)) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS
                        )
                    )
                }
            ),
            onDismiss = { showDialog = false })
    }
}

@Composable
private fun SubtitleText(modifier: Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Center,
        color = Color(0xFFFFFFFF),
        style = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.csp,
        ),
        lineHeight = 21.csp
    )
}
