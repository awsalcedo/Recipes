package com.asalcedo.recipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asalcedo.recipes.data.Recipe
import com.asalcedo.recipes.data.strawberryCake
import com.asalcedo.recipes.ui.AppBarCollapsedHeight
import com.asalcedo.recipes.ui.AppBarExpendedHeight
import com.asalcedo.recipes.ui.theme.*
import com.google.accompanist.insets.LocalWindowInsets
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainFragment(strawberryCake)
                }
            }
        }
    }
}

@Composable
fun MainFragment(recipe: Recipe) {
    val scrollState = rememberLazyListState()
    Box {
        Content(recipe, scrollState)
        ParallaxToolbar(recipe, scrollState)
    }
}

@Composable
fun ParallaxToolbar(recipe: Recipe, scrollState: LazyListState) {
    val imageHeight = AppBarExpendedHeight - AppBarCollapsedHeight
    val maxOffset =
        with(LocalDensity.current) { imageHeight.roundToPx() } - LocalWindowInsets.current.systemBars.layoutInsets.top

    val offset = min(scrollState.firstVisibleItemScrollOffset, maxOffset)

    val offsetProgress = max(0f, offset * 3f - 2f * maxOffset) / maxOffset

    TopAppBar(
        contentPadding = PaddingValues(),
        backgroundColor = White,
        modifier = Modifier
            .height(
                AppBarExpendedHeight
            )
            .offset { IntOffset(x = 0, y = -offset) },
        elevation = if (offset == maxOffset) 4.dp else 0.dp
    ) {
        Column {
            Box(
                Modifier
                    .height(imageHeight)
                    .graphicsLayer {
                        alpha = 1f - offsetProgress
                    }) {
                Image(
                    painter = painterResource(id = R.drawable.strawberry_pie_1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    Pair(0.4f, Transparent),
                                    Pair(1f, White)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        recipe.category,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(Shapes.medium)
                            .background(com.asalcedo.recipes.ui.theme.LightGray)
                            .padding(vertical = 6.dp, horizontal = 16.dp)
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(AppBarCollapsedHeight),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = recipe.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = (16 + 28 * offsetProgress).dp)
                        .scale(1f - 0.25f * offsetProgress)
                )
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(AppBarCollapsedHeight)
            .padding(horizontal = 16.dp)
    ) {
        CircularButton(R.drawable.ic_arrow_back)
        CircularButton(R.drawable.ic_favorite)
    }
}

@Composable
fun CircularButton(
    @DrawableRes iconResource: Int,
    color: Color = Gray,
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        shape = Shapes.small,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = White,
            contentColor = color
        ),
        elevation = elevation,
        modifier = Modifier
            .width(38.dp)
            .height(38.dp)
    ) {
        Icon(painter = painterResource(id = iconResource), contentDescription = null)
    }
}

@Composable
fun Content(recipe: Recipe, scrollState: LazyListState) {
    LazyColumn(contentPadding = PaddingValues(top = AppBarExpendedHeight), state = scrollState) {
        item {
            BasicInfo(recipe)
            Description(recipe)
            ServingCalculator()
            IngredientsHeader()
            IngredientsList(recipe)
            ShoppingListButton()
            Reviews(recipe)
            Images()
        }
    }
}

@Composable
fun Images() {
    Row(
        modifier = Modifier
            .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.strawberry_pie_2),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .clip(Shapes.small)
        )
        Spacer(modifier = Modifier.weight(0.1f))
        Image(
            painter = painterResource(id = R.drawable.strawberry_pie_3),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .clip(Shapes.small)
        )
    }
}


@Composable
fun Reviews(recipe: Recipe) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(text = "Reviews", fontWeight = FontWeight.Bold)
            Text(text = recipe.reviews, color = com.asalcedo.recipes.ui.theme.DarkGray)
        }
        Button(
            onClick = { /*TODO*/ },
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = com.asalcedo.recipes.ui.theme.Transparent,
                contentColor = Pink
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "See all")
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }

        }
    }
}

@Composable
fun ShoppingListButton() {
    Button(
        onClick = { /*TODO*/ },
        elevation = null,
        shape = Shapes.small,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = com.asalcedo.recipes.ui.theme.LightGray,
            contentColor = Color.Black
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Add to shopping list", modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun IngredientsList(recipe: Recipe) {
    EasyGrid(nColumns = 3, items = recipe.ingredients) {
        IngredientCard(it.image, it.title, it.subtitle, Modifier)
    }
}

@Composable
fun <T> EasyGrid(nColumns: Int, items: List<T>, content: @Composable (T) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        for (i in items.indices step nColumns) {
            Row {
                for (j in 0 until nColumns) {
                    if (i + j < items.size) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            content(items[i + j])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f, fill = true))
                    }
                }

            }
        }
    }
}

@Composable
fun IngredientCard(
    @DrawableRes iconResource: Int,
    title: String,
    subtitle: String,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Card(
            shape = Shapes.large,
            backgroundColor = com.asalcedo.recipes.ui.theme.LightGray,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconResource),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(
            text = title,
            modifier = Modifier.width(100.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(text = subtitle, color = DarkGray, modifier = Modifier.width(100.dp), fontSize = 14.sp)
    }
}

@Composable
fun IngredientsHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(Shapes.medium)
            .background(com.asalcedo.recipes.ui.theme.LightGray)
            .fillMaxWidth()
            .height(44.dp)
    ) {
        TabButton("Ingredients", true, Modifier.weight(1f))
        TabButton("Tools", false, Modifier.weight(1f))
        TabButton("Steps", false, Modifier.weight(1f))
    }
}

@Composable
fun TabButton(text: String, active: Boolean, modifier: Modifier) {
    Button(
        onClick = { /*TODO*/ },
        shape = Shapes.medium,
        modifier = modifier.fillMaxHeight(),
        elevation = null,
        colors = if (active) ButtonDefaults.buttonColors(
            backgroundColor = Pink,
            contentColor = White
        ) else ButtonDefaults.buttonColors(
            backgroundColor = com.asalcedo.recipes.ui.theme.LightGray,
            contentColor = DarkGray
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun ServingCalculator() {
    var value by remember { mutableStateOf(6) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(Shapes.medium)
            .background(
                com.asalcedo.recipes.ui.theme.LightGray
            )
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Servings", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        CircularButton(
            iconResource = R.drawable.ic_minus,
            elevation = null,
            color = Pink
        ) { value-- }
        Text(text = "$value", fontWeight = FontWeight.Medium, modifier = Modifier.padding(16.dp))
        CircularButton(
            iconResource = R.drawable.ic_plus,
            elevation = null,
            color = Pink
        ) { value++ }
    }
}

@Composable
fun Description(recipe: Recipe) {
    Text(
        recipe.description,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun BasicInfo(recipe: Recipe) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        InfoColumn(R.drawable.ic_clock, recipe.cookingTime)
        InfoColumn(R.drawable.ic_flame, recipe.energy)
        InfoColumn(R.drawable.ic_star, recipe.rating)
    }
}

@Composable
fun InfoColumn(@DrawableRes iconResource: Int, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = iconResource),
            contentDescription = null,
            tint = Pink,
            modifier = Modifier.height(24.dp)
        )
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 1400)
@Composable
fun DefaultPreview() {
    RecipesTheme {
        MainFragment(strawberryCake)
    }
}