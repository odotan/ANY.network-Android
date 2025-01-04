package com.anynetwork.app.ui.screens.testing

import android.provider.ContactsContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.components.dialog.Message
import com.anynetwork.app.model.Contact
import com.anynetwork.app.ui.screens.home.ContactsRow
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.log

@Composable
fun TestingScreen(navController: NavHostController) {
    Screen(
        topBar = ToolbarState.Shown(titleState = ToolbarStateTitle.Text("Testing"))
    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            HexButton(
//                modifier = Modifier
//                    .padding(top = 100.dp)
//                    .width(164.xdph)
//                    .height(54.xdpv),
//                text = "To home",
//                onClick = {
//                    navController.navigate(Route.Home)
//                },
//            )
//
//            HexButton(
//                modifier = Modifier
//                    .padding(top = 40.xdpv)
//                    .width(164.xdph)
//                    .height(54.xdpv),
//                text = "To my profile",
//                onClick = {
//                    navController.navigate(Route.MyProfile)
//                },
//            )
//
//            HexButton(
//                modifier = Modifier
//                    .padding(top = 40.xdpv)
//                    .width(164.xdph)
//                    .height(54.xdpv),
//                text = "To other user profile",
//                onClick = {
//                    navController.navigate(Route.ExternalProfile)
//                },
//            )
//
//            DetailText(
//                modifier = Modifier.padding(top = 40.xdpv, bottom = 20.xdpv),
//                text = "Hex Grid"
//            )
//
//            HexButton(
//                modifier = Modifier
//                    .width(164.xdph)
//                    .height(54.xdpv),
//                onClick = {
//                    navController.navigate(Route.GridPlayground(0))
//                },
//            )
//
//            DetailText(
//                modifier = Modifier.padding(top = 40.xdpv, bottom = 20.xdpv),
//                text = "Rounded Hexagon with Glow"
//            )
//
//            RoundedHexagon(
//                modifier = Modifier
//                    .width(defaultHexagonCellWidth * 3)
//                    .height(defaultHexagonCellHeight * 3),
//                shadowColor = Color(0xFF6E4CD4),
//                contentStyle = EmptyHexagonContentStyle(background = NontransparentHexagonContentStyle.Background.SingleColor(Color(0xFF6E4CD4))),
//            )
//
//            DetailText(
//                modifier = Modifier.padding(top = 40.xdpv, bottom = 20.xdpv),
//                text = "Wheel Horizontal Picker"
//            )
//
//            val items = listOf(
//                PickerItem(
//                    Color.Gray,
//                    resId = R.drawable.ic_logo_flat,
//                    iconContentDescription = ""
//                ),
//                PickerItem(
//                    YoutubeColor,
//                    resId = R.drawable.ic_youtube,
//                    iconContentDescription = ""
//                ),
//                PickerItem(TiktokColor),
//                PickerItem(FacebookColor),
//                PickerItem(WhatsappColor),
//                PickerItem(
//                    Color.Gray,
//                    resId = R.drawable.ic_logo_flat,
//                    iconContentDescription = ""
//                ),
//                PickerItem(
//                    YoutubeColor,
//                    resId = R.drawable.ic_youtube,
//                    iconContentDescription = ""
//                ),
//                PickerItem(TiktokColor),
//                PickerItem(FacebookColor),
//                PickerItem(WhatsappColor),
//                PickerItem(
//                    Color.Gray,
//                    resId = R.drawable.ic_logo_flat,
//                    iconContentDescription = ""
//                ),
//                PickerItem(
//                    YoutubeColor,
//                    resId = R.drawable.ic_youtube,
//                    iconContentDescription = ""
//                ),
//                PickerItem(TiktokColor),
//                PickerItem(FacebookColor),
//                PickerItem(WhatsappColor),
//            )
//
//            val pickerZoom = 1
//            CircularCarousel(
//                numItems = items.size,
//                modifier = Modifier
//                    .width(67.11.xdph * pickerZoom)
//                    .height(37.23.xdpv * pickerZoom)
//                    .clipToBounds()
//            ) { index ->
//                val item = items[index]
//                Card(
//                    modifier = Modifier
//                        .width(37.23.xdph * pickerZoom)
//                        .height(37.23.xdpv * pickerZoom),
//                    shape = CircleShape,
//                    colors = CardDefaults.cardColors(containerColor = item.color),
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .align(Alignment.CenterHorizontally),
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        if (item.resId != null) {
//                            Image(
//                                painter = painterResource(id = item.resId),
//                                contentDescription = item.iconContentDescription
//                            )
//                        } else {
//                            Text(text = "${index}")
//                        }
//                    }
//                }
//            }
//
//            DetailText(
//                modifier = Modifier.padding(top = 40.xdpv, bottom = 20.xdpv),
//                text = "Hex Button"
//            )
//
//            HexButton(
//                modifier = Modifier
//                    .width(164.xdph)
//                    .height(54.xdpv),
//                onClick = {},
//            )
//
//            DetailText(
//                modifier = Modifier.padding(top = 40.xdpv, bottom = 20.xdpv),
//                text = "Outline Hex Button"
//            )
//
//            OutlineHexButton(
//                modifier = Modifier
//                    .width(164.xdph)
//                    .height(54.xdpv),
//                color = Color.White.copy(alpha = .2f),
//                textColor = Color.White,
//                onClick = {},
//            )
//
//            DetailText(
//                modifier = Modifier.padding(top = 40.xdpv, bottom = 20.xdpv),
//                text = "Hex Grid 2"
//            )
//        }
        val context = LocalContext.current
        val contactList = remember { mutableStateListOf<Contact>() }

        LaunchedEffect(Unit) {
            log { "fetch contacts" }
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.PHOTO_URI
                ),
                null,
                null,
                null
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                val photoIndex = it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)

                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val number = it.getString(numberIndex)
                    val email = it.getString(emailIndex)
                    val photoUri = it.getString(photoIndex)
                    contactList.add(
                        Contact(
                            name = name,
                            avatarUri = photoUri,
                            id = 0
                        )
                    )
                }
            }
        }

        var isCreateYourContactCardShown by remember { mutableStateOf(true) }
        LazyColumn(
            modifier = Modifier
                .height(570.fdpv),
        ) {
            val contacts = contactList.distinct().sortedBy { it.name }
            if (isCreateYourContactCardShown) item {
                Message(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(132.fdpv),
                    icon = {
                        Image(
                            modifier = Modifier
                                .padding(start = 31.fdph, top = 35.fdpv)
                                .width(33.fdph)
                                .height(42.67.fdpv),
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = null
                        )
                    },
                    title = "Create your contact card?",
                    description = "Tap your hexagon in the grid to create or edit your card. Tap other hexagons to add or edit contacts.",
                    onCloseClick = {
                        isCreateYourContactCardShown = false
                    }
                )
            }
//            items(contacts.size) {
//                val contact = contacts[it]
//                ContactsRow(
//                    modifier = Modifier.padding(vertical = 8.9.fdpv),
//                    name = contact.name,
//                    phone = contact.phone,
//                    avatarUri = contact.avatarUri,
//                    onClick = {
////                        navController.navigate(Route.ExternalProfile(
////                            name = contact.name)
////                        )
//                    }
//                )
//            }
        }
    }
}

@Composable
fun NonScrollableGrid(
    modifier: Modifier = Modifier,
    columns: Int,
    itemCount: Int,
    itemContent: @Composable (Int) -> Unit
) {
    val rows = (itemCount + columns - 1) / columns // Calculate number of rows needed
    Column(modifier = modifier) {
        for (row in 0 until rows) {
            Row {
                for (column in 0 until columns) {
                    val index = row * columns + column
                    if (index < itemCount) {
                        Box(modifier = Modifier.weight(1f)) {
                            itemContent(index)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f)) // Fill empty space in the last row
                    }
                }
            }
        }
    }
}

@Composable
fun GridItem(index: Int) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Ensures the item is square
            .padding(8.dp)
            .background(Color.Yellow),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$index")
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun TestingScreenPreview() {
    TestingScreen(rememberNavController())
}