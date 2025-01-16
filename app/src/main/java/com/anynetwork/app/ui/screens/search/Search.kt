@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.anynetwork.app.ui.screens.search

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anynetwork.app.R
import com.anynetwork.app.ui.components.NavigationIconState
import com.anynetwork.app.ui.components.Screen
import com.anynetwork.app.ui.components.SearchTextField
import com.anynetwork.app.ui.components.ToolbarState
import com.anynetwork.app.ui.components.ToolbarStateTitle
import com.anynetwork.app.ui.components.hexagon.EmptyHexagonContentStyle
import com.anynetwork.app.ui.components.hexagon.HexGridCellPosition
import com.anynetwork.app.ui.components.hexagon.HexagonalGrid
import com.anynetwork.app.ui.components.hexagon.NontransparentHexagonContentStyle.Background
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.theme.DarkBlue
import com.anynetwork.app.ui.theme.GreenColor
import com.anynetwork.app.ui.theme.montserratFontFamily
import com.anynetwork.app.ui.utils.csp
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.fsp
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import kotlin.math.roundToInt

@Composable
fun SharedTransitionScope.SearchRoot(navController: NavController) {
    Search(
        onBackButtonClick = {
            navController.popBackStack()
        },
        onContactClick = { name ->
//            navController.navigate(Route.ExternalProfile(name = nameIndex))
        },
        onCreateNewContactClick = {
            navController.navigate(Route.NewContact)
        }
    )
}

sealed class SearchScreenMode {
    data object List: SearchScreenMode()
    data object Grid: SearchScreenMode()
}

@Composable
private fun SharedTransitionScope.Search(
    onBackButtonClick: () -> Unit,
    onContactClick: (name: String) -> Unit,
    onCreateNewContactClick: () -> Unit,
) {
    var mode: SearchScreenMode by remember { mutableStateOf(SearchScreenMode.List) }

    Screen(
        modifier = Modifier
            .fillMaxSize(),
        topBar = ToolbarState.Shown(
            navigationIconState = NavigationIconState.Custom {
                IconButton(onClick = onBackButtonClick) {
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
                        text = "Search",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.csp,
                        )
                    )
                }
            ),
            actions = {
                if (mode == SearchScreenMode.List) {
                    IconButton(onClick = {
                        mode = SearchScreenMode.Grid
                    }) {
                        Image(
                            painter = painterResource(R.drawable.ic_hex_grid),
                            contentDescription = "open hex grid",
                        )
                    }
                } else {
                    IconButton(onClick = {
                        mode = SearchScreenMode.List
                    }) {
                        Image(
                            painter = painterResource(R.drawable.ic_list),
                            contentDescription = "open list",
                        )
                    }
                }
            }
        ),
        hexagonGrid = {
            if (mode == SearchScreenMode.Grid) {
                val scale = 12 / 9f
                val gridColumns = 12
                val gridRows = 19

                fun createOnboardingCellPosition(row: Int, column: Int) = HexGridCellPosition(
                    column = column,
                    row = row,
                    gridRows = gridRows,
                    gridColumns = gridColumns
                )

                // Find the central element
                val items = List(gridRows) { row -> // Create a list with `gridRows` elements
                    List(gridColumns) { column ->  // Each element is a list with `gridColumns` elements
                        remember {
                            EmptyHexagonContentStyle(
                                id = 0,
                                background = Background.SingleColor(Color(0xFF252130))
                            )
                        }
                    }
                }

                HexagonalGrid(
                    modifier = Modifier.padding(top = 109.fdpv),
                    items = items,
                    rowSize = gridColumns,
                    minScale = scale,
                    isScrollEnabled = false,
                    offsetY = with(LocalDensity.current) { 109.fdpv.toPx() }.roundToInt()
                )
            }
        },
        content = {
            if (mode == SearchScreenMode.List) {

//                LazyColumn(modifier = Modifier.padding(top = 109.fdpv)) {
//                    items(demoContacts.size) {
//                        ContactsRow(
//                            modifier = Modifier.padding(vertical = 8.9.fdpv),
//                            name = demoContacts[it].name,
//                            phone = demoContacts[it].phone,
//                            avatarResource = demoContacts[it].avatarResource,
//                            onClick = {
//                                onContactClick.invoke(demoContacts[it].name)
//                            },
//                            backgroundColor = Color(0xFF120E1E)
//                        )
//                    }
//                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 182.fdpv),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier,
                        text = "\"mark.hamlin@gmail.com\"",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.fsp,
                        )
                    )

                    Text(
                        modifier = Modifier.padding(top = 16.fdpv),
                        text = "There is nothing to show on your contact list",
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.7f),
                        style = TextStyle(
                            fontFamily = montserratFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.fsp,
                        )
                    )

                    Button(
                        modifier = Modifier
                            .padding(top = 17.fdpv),
                        onClick = { onCreateNewContactClick.invoke() },
                        colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent)
                    ) {
                        Row(
                            modifier = Modifier
                                .height(24.fdpv),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(15.61.fdpv),
                                painter = painterResource(R.drawable.ic_rounded_plus),
                                contentDescription = "add new contact",
                                colorFilter = ColorFilter.tint(GreenColor)
                            )

                            Text(
                                modifier = Modifier.padding(start = 8.fdph),
                                text = "Create New Contact",
                                textAlign = TextAlign.Center,
                                color = GreenColor,
                                style = TextStyle(
                                    fontFamily = montserratFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.fsp,
                                )
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)
                ) {
                    var value by remember {
                        mutableStateOf("")
                    }
                    SearchTextField(
                        modifier = Modifier
                            .padding(horizontal = 16.fdpv, vertical = 16.fdph)
                            .height(56.xdpv),
                        onValueChange = {
                            value = it
                        },
                        trailingIcon = {
                            if (value.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Spacer(modifier = Modifier.width(16.xdph))

                                    Box(
                                        modifier = Modifier
                                            .size(24.fdpv)
                                            .clip(CircleShape)
                                            .background(GreenColor)
                                            .clickable {
                                                onCreateNewContactClick.invoke()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            modifier = Modifier.size(10.29.fdpv),
                                            painter = painterResource(R.drawable.ic_rounded_plus),
                                            contentDescription = "add new contact",
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.xdph))
                                }
                            }
                        },
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.fdpv)
                        .background(color = DarkBlue)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(89.fdpv)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    DarkBlue.copy(alpha = 0.99f),
                                    DarkBlue.copy(alpha = 0f)
                                )
                            )
                        )
                )
            }
        }
    )
}