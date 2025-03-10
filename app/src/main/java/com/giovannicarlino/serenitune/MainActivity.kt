@file:OptIn(ExperimentalMaterial3Api::class)

package com.giovannicarlino.serenitune

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalLifecycleOwner
import android.content.res.Configuration
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.res.stringResource
import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.unit.IntOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SereniTuneApp()
        }
    }

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }
}

@Composable
fun SereniTuneApp() {
    val cozyColors = lightColorScheme(
        primary = Color(0xFF9B6B9E), // Soft lavender
        secondary = Color(0xFFD4A5A5), // Dusty rose
        tertiary = Color(0xFFB7C4CF), // Soft blue-gray
        background = Color(0xFFFDF6F6), // Warm white
        surface = Color(0xFFFFFFFF), // Pure white
        onPrimary = Color.White,
        onSecondary = Color(0xFF2C2C2C),
        onTertiary = Color(0xFF2C2C2C),
        onBackground = Color(0xFF2C2C2C),
        onSurface = Color(0xFF2C2C2C)
    )

    MaterialTheme(
        colorScheme = cozyColors,
        typography = Typography(
            headlineLarge = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.sp,
                lineHeight = 44.sp
            ),
            headlineMedium = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.sp,
                lineHeight = 40.sp
            ),
            titleLarge = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.sp,
                lineHeight = 32.sp
            ),
            titleMedium = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.sp,
                lineHeight = 28.sp
            ),
            bodyLarge = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                lineHeight = 24.sp
            ),
            bodyMedium = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.25.sp,
                lineHeight = 20.sp
            )
        ),
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp)
        )
    ) {
        // Start with the splash screen
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
        
        // Add language state
        var showLanguageDialog by remember { mutableStateOf(false) }
        var currentLanguage by rememberSaveable { mutableStateOf(Locale.getDefault().language) }
        
        // Handle back navigation
        val activity = LocalContext.current as ComponentActivity
        val mainActivity = LocalContext.current as MainActivity
        
        DisposableEffect(Unit) {
            val onBackPressedCallback = object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (currentScreen != Screen.Menu) {
                        currentScreen = Screen.Menu
                    }
                }
            }
            activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)
            onDispose {
                onBackPressedCallback.remove()
            }
        }
        
        // Handle language change
        fun changeLanguage(languageCode: String) {
            currentLanguage = languageCode
            mainActivity.setLocale(languageCode)
        }
        
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (currentScreen) {
                is Screen.Splash -> SplashScreen(onTimeout = { currentScreen = Screen.Menu })
                is Screen.Menu -> MenuScreen(
                    onNavigation = { currentScreen = it },
                    onLanguageClick = { showLanguageDialog = true }
                )
                is Screen.Journal -> JournalScreen(onBack = { currentScreen = Screen.Menu })
                is Screen.Breathing -> BreathingScreen(onBack = { currentScreen = Screen.Menu })
                is Screen.Panic -> PanicScreen(onBack = { currentScreen = Screen.Menu })
                is Screen.Quotes -> QuotesScreen(onBack = { currentScreen = Screen.Menu })
                is Screen.Minigames -> MinigamesScreen(onBack = { currentScreen = Screen.Menu })
            }

            if (showLanguageDialog) {
                LanguageSelectionDialog(
                    currentLanguage = currentLanguage,
                    onLanguageSelected = { languageCode ->
                        changeLanguage(languageCode)
                        showLanguageDialog = false
                    },
                    onDismiss = { showLanguageDialog = false }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val splashDuration = 2000L
    LaunchedEffect(Unit) {
        delay(splashDuration)
        onTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.app_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onNavigation: (Screen) -> Unit,
    onLanguageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onLanguageClick) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.language_settings),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                MenuCard(
                    title = stringResource(R.string.menu_journal),
                    icon = Icons.Default.Edit,
                    onClick = { onNavigation(Screen.Journal) }
                )
            }
            item {
                MenuCard(
                    title = stringResource(R.string.menu_breathing),
                    icon = Icons.Default.Info,
                    onClick = { onNavigation(Screen.Breathing) }
                )
            }
            item {
                MenuCard(
                    title = stringResource(R.string.menu_panic),
                    icon = Icons.Default.Warning,
                    onClick = { onNavigation(Screen.Panic) }
                )
            }
            item {
                MenuCard(
                    title = stringResource(R.string.menu_quotes),
                    icon = Icons.Default.Info,
                    onClick = { onNavigation(Screen.Quotes) }
                )
            }
            item {
                MenuCard(
                    title = stringResource(R.string.menu_minigames),
                    icon = Icons.Default.PlayArrow,
                    onClick = { onNavigation(Screen.Minigames) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val view = LocalView.current
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    CozyCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(scale)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isPressed = true
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isPressed = false
                        onClick()
                        true
                    }
                    else -> false
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(onBack: () -> Unit) {
    var journalEntry by remember { mutableStateOf(TextFieldValue("")) }
    var mood by remember { mutableStateOf(3) }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Get string resources
    val journalSaved = stringResource(R.string.journal_saved)
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.journal_back),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    stringResource(R.string.journal_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(journalSaved)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.journal_save),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CozyCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.journal_mood),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { mood = index + 1 },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (index < mood) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Mood ${index + 1}",
                                    tint = if (index < mood) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CozyCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.journal_thoughts),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = journalEntry,
                        onValueChange = { journalEntry = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        textStyle = TextStyle(fontSize = 16.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { /* handle done if needed */ })
                    )
                }
            }
        }
    }
}

@Composable
fun BreathingScreen(onBack: () -> Unit) {
    val progress = remember { Animatable(0.3f) }
    var isPlaying by remember { mutableStateOf(true) }
    var cycleCount by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current

    // Get string resources
    val phaseIn = stringResource(R.string.breathing_phase_in)
    val phaseHold = stringResource(R.string.breathing_phase_hold)
    val phaseOut = stringResource(R.string.breathing_phase_out)
    var phase by remember { mutableStateOf(phaseIn) }

    // Create a coroutine scope for the animation
    LaunchedEffect(isPlaying, phaseIn, phaseHold, phaseOut) {
        while (isPlaying) {
            phase = phaseIn
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
            )
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            phase = phaseHold
            delay(7000)

            phase = phaseOut
            progress.animateTo(
                targetValue = 0.3f,
                animationSpec = tween(durationMillis = 8000, easing = FastOutSlowInEasing)
            )
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            phase = phaseHold
            delay(7000)
            cycleCount++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.breathing_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.breathing_cycle, cycleCount),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                drawCircle(
                    color = Color(0xFF9B6B9E),
                    radius = 100f * progress.value,
                    center = center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            phase,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { isPlaying = !isPlaying },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) stringResource(R.string.breathing_pause) else stringResource(R.string.breathing_play),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            CozyButton(
                onClick = { onBack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.breathing_back))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanicScreen(onBack: () -> Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    var showEmergencyContacts by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Get string resources
    val breathingPause = stringResource(R.string.breathing_pause)
    val breathingPlay = stringResource(R.string.breathing_play)
    val panicBack = stringResource(R.string.panic_back)
    val panicEmergency = stringResource(R.string.panic_emergency)
    
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            elapsedTime++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = panicBack,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                stringResource(R.string.panic_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { showEmergencyContacts = !showEmergencyContacts }
            ) {
                Icon(
                    imageVector = if (showEmergencyContacts) Icons.Default.Close else Icons.Default.Warning,
                    contentDescription = if (showEmergencyContacts) panicBack else panicEmergency,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (showEmergencyContacts) {
            CozyCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.panic_emergency),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    EmergencyContactButton(
                        name = stringResource(R.string.panic_services),
                        number = "911",
                        icon = Icons.Default.Info
                    )
                    EmergencyContactButton(
                        name = stringResource(R.string.panic_helpline),
                        number = "988",
                        icon = Icons.Default.Call
                    )
                }
            }
        } else {
            CozyCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.panic_safe),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.panic_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        stringResource(R.string.panic_time, elapsedTime / 60, elapsedTime % 60),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { 
                                isPlaying = !isPlaying
                                if (!isPlaying) elapsedTime = 0
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) breathingPause else breathingPlay,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactButton(
    name: String,
    number: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        IconButton(
            onClick = { /* Handle call */ }
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = stringResource(R.string.panic_call),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(onBack: () -> Unit) {
    var currentQuoteIndex by remember { mutableStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Get string resources
    val quotesBack = stringResource(R.string.quotes_back)
    val quotesUnfavorite = stringResource(R.string.quotes_unfavorite)
    val quotesFavorite = stringResource(R.string.quotes_favorite)
    val quotesShare = stringResource(R.string.quotes_share)
    val quotesPrevious = stringResource(R.string.quotes_previous)
    val quotesNext = stringResource(R.string.quotes_next)
    
    // Get all quotes as string resources
    val quote1 = stringResource(R.string.quote_1)
    val quote2 = stringResource(R.string.quote_2)
    val quote3 = stringResource(R.string.quote_3)
    val quote4 = stringResource(R.string.quote_4)
    val quote5 = stringResource(R.string.quote_5)
    val quote6 = stringResource(R.string.quote_6)
    val quote7 = stringResource(R.string.quote_7)
    val quote8 = stringResource(R.string.quote_8)
    val quote9 = stringResource(R.string.quote_9)
    val quote10 = stringResource(R.string.quote_10)
    val quote11 = stringResource(R.string.quote_11)
    val quote12 = stringResource(R.string.quote_12)
    
    val quotes = remember(quote1, quote2, quote3, quote4, quote5, quote6, quote7, quote8, quote9, quote10, quote11, quote12) {
        listOf(
            quote1, quote2, quote3, quote4, quote5, quote6,
            quote7, quote8, quote9, quote10, quote11, quote12
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = quotesBack,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                stringResource(R.string.quotes_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { isFavorite = !isFavorite }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) quotesUnfavorite else quotesFavorite,
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        CozyCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = quotes[currentQuoteIndex],
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, quotes[currentQuoteIndex])
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, quotesShare))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = quotesShare,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentQuoteIndex = (currentQuoteIndex - 1 + quotes.size) % quotes.size
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = quotesPrevious,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            CozyButton(
                onClick = {
                    currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.quotes_next))
            }
            
            IconButton(
                onClick = {
                    currentQuoteIndex = (currentQuoteIndex + 1 + quotes.size) % quotes.size
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = quotesNext,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CozyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CozyCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            Column {
                RadioButton(
                    selected = currentLanguage == "en",
                    onClick = { onLanguageSelected("en") }
                )
                Text(stringResource(R.string.english))
                RadioButton(
                    selected = currentLanguage == "it",
                    onClick = { onLanguageSelected("it") }
                )
                Text(stringResource(R.string.italian))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinigamesScreen(onBack: () -> Unit) {
    var selectedGame by remember { mutableStateOf<Int?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.minigames_back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                stringResource(R.string.minigames_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        when (selectedGame) {
            null -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        GameCard(
                            title = stringResource(R.string.minigame_1),
                            description = stringResource(R.string.minigame_1_desc),
                            icon = Icons.Default.PlayArrow,
                            onClick = { selectedGame = 1 }
                        )
                    }
                }
            }
            1 -> ColorMatchGame(onBack = { selectedGame = null })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    CozyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorMatchGame(onBack: () -> Unit) {
    var score by remember { mutableStateOf(0) }
    var pattern by remember { mutableStateOf(listOf<Color>()) }
    var userPattern by remember { mutableStateOf(listOf<Color>()) }
    var isComplete by remember { mutableStateOf(false) }
    
    val colors = remember {
        listOf(
            Color(0xFFE91E63), // Pink
            Color(0xFF2196F3), // Blue
            Color(0xFF4CAF50), // Green
            Color(0xFFFFC107), // Amber
            Color(0xFF9C27B0)  // Purple
        )
    }
    
    LaunchedEffect(Unit) {
        pattern = colors.shuffled().take(4)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.minigames_back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                stringResource(R.string.color_match_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            stringResource(R.string.color_match_score, score),
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Pattern display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            pattern.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // User pattern
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            userPattern.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, CircleShape)
                )
            }
            repeat(4 - userPattern.size) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            CircleShape
                        )
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Color selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, CircleShape)
                        .clickable {
                            if (userPattern.size < 4) {
                                val newPattern = userPattern + color
                                userPattern = newPattern
                                if (newPattern == pattern) {
                                    isComplete = true
                                    score += 10
                                }
                            }
                        }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CozyButton(
                onClick = {
                    pattern = colors.shuffled().take(4)
                    userPattern = emptyList()
                    isComplete = false
                }
            ) {
                Text(stringResource(R.string.color_match_reset))
            }
        }
        
        if (isComplete) {
            LaunchedEffect(Unit) {
                delay(1000)
                isComplete = false
                pattern = colors.shuffled().take(4)
                userPattern = emptyList()
            }
            Text(
                stringResource(R.string.color_match_complete),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

sealed class Screen {
    object Splash : Screen()
    object Menu : Screen()
    object Journal : Screen()
    object Breathing : Screen()
    object Panic : Screen()
    object Quotes : Screen()
    object Minigames : Screen()
}
