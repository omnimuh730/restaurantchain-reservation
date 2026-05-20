package com.mh.restaurantchainreservation.feature.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.DeterministicQrCode
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

object AuthRoutes {
    const val Root = "auth"
    const val Login = "auth/login"
    const val Register = "auth/register"
    const val Forgot = "auth/forgot"
}

private data class MockUser(
    val password: String,
    val name: String,
    val active: Boolean,
    val securityQA: List<Pair<String, String>>,
)

private val mockUsers = mapOf(
    "demo" to MockUser(
        password = "aaa",
        name = "Demo User",
        active = true,
        securityQA = listOf("pet" to "fluffy", "birthCity" to "seoul", "favoriteFood" to "pizza"),
    ),
    "admin" to MockUser(
        password = "Admin123",
        name = "Admin",
        active = false,
        securityQA = listOf("birthCity" to "seoul", "firstSchool" to "greenfield", "motherMaiden" to "kim"),
    ),
    "foodie99" to MockUser(
        password = "Yummy123",
        name = "Food Lover",
        active = true,
        securityQA = listOf("favoriteFood" to "pizza", "pet" to "buddy", "childhoodNick" to "foodster"),
    ),
)

private val securityQuestions = listOf(
    "pet" to "What is your pet's name?",
    "birthCity" to "What city were you born in?",
    "favoriteFood" to "What is your favorite food?",
    "firstSchool" to "What was the name of your first school?",
    "motherMaiden" to "What is your mother's maiden name?",
    "childhoodNick" to "What was your childhood nickname?",
    "favoriteMovie" to "What is your favorite movie?",
    "childhoodStreet" to "What street did you grow up on?",
    "sportsTeam" to "What is your favorite sports team?",
)

private enum class FeedbackType { Success, Error, Warning }

private data class Feedback(val type: FeedbackType, val message: String)

private enum class RegisterStep { Refer, Credentials, Profile, Security, Done }

/** Reserve scroll space before the bottom bar is measured (tallest step: invite + sign-in link). */
private val RegisterBottomBarScrollPaddingMin = 132.dp

private enum class ForgotStep { Username, Security, Reset, Done }

@Composable
fun LoginScreen(
    onNavigateRegister: () -> Unit,
    onNavigateForgot: () -> Unit,
    modifier: Modifier = Modifier,
    onAuthenticated: () -> Unit = {},
    onClose: () -> Unit = {},
    allowDismiss: Boolean = true,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var loading by rememberSaveable { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<Feedback?>(null) }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    fun clearErrors() {
        usernameError = null
        passwordError = null
        feedback = null
    }

    fun submit() {
        clearErrors()
        when {
            username.trim().isEmpty() -> {
                usernameError = "Username is required"
                return
            }
            password.isEmpty() -> {
                passwordError = "Password is required"
                return
            }
        }
        loading = true
        val user = mockUsers[username.trim().lowercase()]
        when {
            user == null -> {
                loading = false
                usernameError = "Username not found"
                feedback = Feedback(FeedbackType.Error, "Account not found. Check your username or sign up.")
            }
            !user.active -> {
                loading = false
                feedback = Feedback(FeedbackType.Warning, "This account has been deactivated. Please contact support.")
            }
            user.password != password -> {
                loading = false
                passwordError = "Incorrect password"
                feedback = Feedback(FeedbackType.Error, "Incorrect password. Please try again or reset it.")
            }
            else -> {
                loading = false
                feedback = Feedback(FeedbackType.Success, "Welcome back, ${user.name}.")
                onAuthenticated()
            }
        }
    }

    AuthSurface(modifier = modifier) {
        if (allowDismiss) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                CircleIconButton(icon = Icons.Filled.Close, label = "Close", onClick = onClose)
            }
        }

        Spacer(Modifier.weight(1f))
        AuthHero(
            icon = { LogoBadge() },
            title = "Welcome back",
            subtitle = "Sign in to keep your reservations, rewards, and favorite dining spots close.",
        )
        AnimatedVisibility(
            visible = feedback != null,
            enter = fadeIn(tween(180)) + slideInVertically { -it / 4 },
            exit = fadeOut(tween(150)) + slideOutVertically { -it / 4 },
        ) {
            feedback?.let {
                FeedbackBanner(feedback = it, onDismiss = { feedback = null })
                Spacer(Modifier.height(12.dp))
            }
        }
        AuthInputField(
            value = username,
            onChange = {
                username = it
                clearErrors()
            },
            placeholder = "Username",
            icon = Icons.Outlined.Person,
            error = usernameError,
            enabled = !loading,
        )
        Spacer(Modifier.height(12.dp))
        AuthInputField(
            value = password,
            onChange = {
                password = it
                clearErrors()
            },
            placeholder = "Password",
            icon = Icons.Outlined.Lock,
            password = true,
            error = passwordError,
            enabled = !loading,
            onDone = ::submit,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextAction(text = "Forgot password?", onClick = onNavigateForgot)
        }
        PrimaryButton(
            text = "Sign in",
            onClick = ::submit,
            modifier = Modifier.padding(top = 20.dp),
            loading = loading,
        )
        DemoHint()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val palette = LocalRestaurantPalette.current
            Text("New to Tonight?", color = palette.mutedForeground, fontSize = 13.sp)
            Spacer(Modifier.width(4.dp))
            TextAction(text = "Create account", onClick = onNavigateRegister)
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onGoLogin: () -> Unit = {},
    onComplete: () -> Unit = {},
    onClose: () -> Unit = {},
    allowDismiss: Boolean = true,
) {
    var step by rememberSaveable { mutableStateOf(RegisterStep.Refer) }
    var referCode by rememberSaveable { mutableStateOf("") }
    var showScanner by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var feedback by remember { mutableStateOf<Feedback?>(null) }
    var fieldErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var questionIndexes by rememberSaveable { mutableStateOf(listOf(0, 1, 2)) }
    var securityAnswers by rememberSaveable { mutableStateOf(listOf("", "", "")) }
    var expandedQuestionSlot by rememberSaveable { mutableStateOf<Int?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(step) {
        if (step != RegisterStep.Security) {
            expandedQuestionSlot = null
        }
    }

    fun goBack() {
        when (step) {
            RegisterStep.Refer, RegisterStep.Done -> onGoLogin()
            RegisterStep.Credentials -> step = RegisterStep.Refer
            RegisterStep.Profile -> step = RegisterStep.Credentials
            RegisterStep.Security -> step = RegisterStep.Profile
        }
    }

    fun clear() {
        feedback = null
        fieldErrors = emptyMap()
    }

    fun validateCredentials() {
        clear()
        val nextErrors = mutableMapOf<String, String>()
        val cleanUsername = username.trim()
        when {
            cleanUsername.isEmpty() -> nextErrors["username"] = "Username is required"
            cleanUsername.length < 3 -> nextErrors["username"] = "Username must be at least 3 characters"
            cleanUsername.any { it.isWhitespace() } -> nextErrors["username"] = "Username cannot contain spaces"
            mockUsers.containsKey(cleanUsername.lowercase()) -> {
                nextErrors["username"] = "Username already exists"
                feedback = Feedback(FeedbackType.Error, "This username is already taken.")
            }
        }
        when {
            password.length < 6 -> nextErrors["password"] = "Password must be at least 6 characters"
            !password.any { it.isUpperCase() } || !password.any { it.isDigit() } ->
                nextErrors["password"] = "Use at least 1 uppercase letter and 1 number"
            password != confirmPassword -> nextErrors["confirm"] = "Passwords do not match"
        }
        if (nextErrors.isEmpty()) step = RegisterStep.Profile else fieldErrors = nextErrors
    }

    fun validateProfile() {
        clear()
        val clean = displayName.trim()
        when {
            clean.isEmpty() -> fieldErrors = mapOf("displayName" to "Display name is required")
            clean.length < 2 -> fieldErrors = mapOf("displayName" to "Name must be at least 2 characters")
            else -> step = RegisterStep.Security
        }
    }

    fun validateSecurity() {
        clear()
        val nextErrors = securityAnswers.mapIndexedNotNull { index, answer ->
            if (answer.trim().isEmpty()) "security$index" to "Please provide an answer" else null
        }.toMap().toMutableMap()
        if (questionIndexes.distinct().size != questionIndexes.size) {
            feedback = Feedback(FeedbackType.Error, "Each security question must be different.")
            return
        }
        if (nextErrors.isNotEmpty()) {
            fieldErrors = nextErrors
            return
        }
        loading = true
        loading = false
        step = RegisterStep.Done
    }

    val labels = listOf("Invite", "Account", "Profile", "Secure", "Done")
    val density = LocalDensity.current
    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    val bottomBarHeight = with(density) { bottomBarHeightPx.toDp() }
        .coerceAtLeast(RegisterBottomBarScrollPaddingMin)
    val securitySetupComplete by remember(securityAnswers, questionIndexes) {
        derivedStateOf {
            securityAnswers.size == 3 &&
                securityAnswers.all { it.trim().isNotEmpty() } &&
                questionIndexes.distinct().size == questionIndexes.size
        }
    }

    AuthSurface(modifier = modifier, scrollable = false, pinContentToBottom = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                AuthHeader(
                    title = "Create account",
                    subtitle = "Join Tonight dining",
                    onBack = ::goBack,
                    onClose = onClose,
                    showClose = allowDismiss,
                )
                AuthProgress(labels = labels, activeIndex = RegisterStep.entries.indexOf(step))

                AnimatedVisibility(feedback != null) {
                    feedback?.let {
                        FeedbackBanner(feedback = it, onDismiss = { feedback = null })
                        Spacer(Modifier.height(12.dp))
                    }
                }

                AnimatedContent(
                    targetState = step,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    transitionSpec = {
                            val forward = targetState.ordinal > initialState.ordinal
                            if (forward) {
                                (
                                    slideInHorizontally(
                                        animationSpec = tween(320, easing = FastOutSlowInEasing),
                                        initialOffsetX = { width -> width },
                                    ) + fadeIn(tween(220, easing = FastOutSlowInEasing))
                                    ).togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                                        targetOffsetX = { width -> -width / 3 },
                                    ) + fadeOut(tween(200)),
                                )
                            } else {
                                (
                                    slideInHorizontally(
                                        animationSpec = tween(320, easing = FastOutSlowInEasing),
                                        initialOffsetX = { width -> -width / 3 },
                                    ) + fadeIn(tween(220, easing = FastOutSlowInEasing))
                                    ).togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                                        targetOffsetX = { width -> width },
                                    ) + fadeOut(tween(200)),
                                )
                            }
                        },
                    label = "register_step",
                ) { current ->
                    when (current) {
                        RegisterStep.Security -> {
                            RecoverySetupStepContent(
                                questionIndexes = questionIndexes,
                                securityAnswers = securityAnswers,
                                fieldErrors = fieldErrors,
                                expandedQuestionSlot = expandedQuestionSlot,
                                bottomPadding = bottomBarHeight,
                                onToggleExpand = { index ->
                                    expandedQuestionSlot =
                                        if (expandedQuestionSlot == index) null else index
                                },
                                onSelectQuestion = { index, selected ->
                                    questionIndexes = questionIndexes.toMutableList().also { list ->
                                        list[index] = selected
                                    }
                                    expandedQuestionSlot = null
                                    feedback = null
                                },
                                onAnswer = { index, value ->
                                    securityAnswers = securityAnswers.toMutableList().also { list ->
                                        list[index] = value
                                    }
                                    clear()
                                },
                            )
                        }
                        else -> {
                            val scrollState = rememberScrollState()
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                                    .imePadding()
                                    .padding(bottom = bottomBarHeight),
                            ) {
                                when (current) {
                                RegisterStep.Refer -> {
                                    AuthHero(
                                        icon = { IconBadge(icon = Icons.Outlined.CardGiftcard) },
                                        title = "Have an invite?",
                                        subtitle = "Enter a referral code or scan a friend's QR. You can also skip this.",
                                    )
                                    if (!showScanner) {
                                        AuthInputField(
                                            value = referCode,
                                            onChange = { referCode = it.uppercase().filterNot { ch -> ch.isWhitespace() } },
                                            placeholder = "Referral code (optional)",
                                            icon = Icons.Outlined.CardGiftcard,
                                        )
                                        SecondaryButton(
                                            text = "Scan QR code",
                                            icon = Icons.Outlined.QrCodeScanner,
                                            onClick = { showScanner = true },
                                            modifier = Modifier.padding(top = 12.dp),
                                        )
                                    } else {
                                        ReferralScanner(
                                            onSimulate = {
                                                referCode = "FRIEND-" + username.ifBlank { "DEMO" }.take(5).uppercase()
                                                showScanner = false
                                            },
                                            onCancel = { showScanner = false },
                                        )
                                    }
                                }
                                RegisterStep.Credentials -> {
                                    AuthHero(
                                        icon = { LogoBadge() },
                                        title = "Choose credentials",
                                        subtitle = "Create a username and a password strong enough for reservation security.",
                                    )
                                    AuthInputField(
                                        value = username,
                                        onChange = {
                                            username = it.filterNot { ch -> ch.isWhitespace() }
                                            clear()
                                        },
                                        placeholder = "Username",
                                        icon = Icons.Outlined.Person,
                                        error = fieldErrors["username"],
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    AuthInputField(
                                        value = password,
                                        onChange = {
                                            password = it
                                            clear()
                                        },
                                        placeholder = "Password",
                                        icon = Icons.Outlined.Lock,
                                        password = true,
                                        error = fieldErrors["password"],
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    AuthInputField(
                                        value = confirmPassword,
                                        onChange = {
                                            confirmPassword = it
                                            clear()
                                        },
                                        placeholder = "Confirm password",
                                        icon = Icons.Outlined.Lock,
                                        password = true,
                                        error = fieldErrors["confirm"],
                                        onDone = ::validateCredentials,
                                    )
                                    PasswordStrength(password = password)
                                }
                                RegisterStep.Profile -> {
                                    AuthHero(
                                        icon = { IconBadge(icon = Icons.Outlined.Person) },
                                        title = "Set up profile",
                                        subtitle = "Tell us what to call you on reservations and invites.",
                                    )
                                    AuthInputField(
                                        value = displayName,
                                        onChange = {
                                            displayName = it
                                            clear()
                                        },
                                        placeholder = "Display name",
                                        icon = Icons.Outlined.Person,
                                        error = fieldErrors["displayName"],
                                        onDone = ::validateProfile,
                                    )
                                }
                                RegisterStep.Done -> {
                                    SuccessPanel(
                                        title = "Welcome, ${displayName.ifBlank { "friend" }}",
                                        body = "Your account is ready. Start discovering memorable restaurants tonight.",
                                        action = "Get started",
                                        onAction = onComplete,
                                        showActionInPanel = false,
                                    )
                                }
                                else -> Unit
                                }
                            }
                        }
                    }
                }
            }

            RegisterStepBottomBar(
                step = step,
                referCode = referCode,
                loading = loading,
                securitySetupComplete = securitySetupComplete,
                onReferContinue = { step = RegisterStep.Credentials },
                onReferSkip = {
                    referCode = ""
                    step = RegisterStep.Credentials
                },
                onCredentialsNext = ::validateCredentials,
                onProfileNext = ::validateProfile,
                onSecuritySubmit = ::validateSecurity,
                onComplete = onComplete,
                onGoLogin = onGoLogin,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .onSizeChanged { bottomBarHeightPx = it.height },
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onClose: () -> Unit = {},
    allowDismiss: Boolean = true,
) {
    var step by rememberSaveable { mutableStateOf(ForgotStep.Username) }
    var username by rememberSaveable { mutableStateOf("") }
    var securityAnswer by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var feedback by remember { mutableStateOf<Feedback?>(null) }
    var fieldErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var foundUserKey by rememberSaveable { mutableStateOf<String?>(null) }
    var questionIndex by rememberSaveable { mutableStateOf(0) }
    var loading by rememberSaveable { mutableStateOf(false) }

    fun clear() {
        feedback = null
        fieldErrors = emptyMap()
    }

    fun goBack() {
        when (step) {
            ForgotStep.Username, ForgotStep.Done -> onBack()
            ForgotStep.Security -> step = ForgotStep.Username
            ForgotStep.Reset -> step = ForgotStep.Security
        }
    }

    fun findAccount() {
        clear()
        if (username.trim().isEmpty()) {
            fieldErrors = mapOf("username" to "Please enter your username")
            return
        }
        loading = true
        val key = username.trim().lowercase()
        val user = mockUsers[key]
        loading = false
        when {
            user == null -> feedback = Feedback(FeedbackType.Error, "No account found with this username.")
            !user.active -> feedback = Feedback(FeedbackType.Warning, "This account is deactivated. Contact support.")
            else -> {
                foundUserKey = key
                questionIndex = 0
                step = ForgotStep.Security
            }
        }
    }

    fun verify() {
        clear()
        val user = foundUserKey?.let { mockUsers[it] } ?: return
        if (securityAnswer.trim().isEmpty()) {
            fieldErrors = mapOf("security" to "Please answer the security question")
            return
        }
        loading = true
        loading = false
        val expected = user.securityQA[questionIndex].second
        if (securityAnswer.trim().lowercase() == expected) {
            step = ForgotStep.Reset
        } else {
            feedback = Feedback(FeedbackType.Error, "Incorrect answer. Please try again.")
        }
    }

    fun resetPassword() {
        clear()
        when {
            newPassword.length < 6 -> fieldErrors = mapOf("newPw" to "Password must be at least 6 characters")
            newPassword != confirmPassword -> fieldErrors = mapOf("confirmPw" to "Passwords do not match")
            else -> {
                loading = true
                loading = false
                step = ForgotStep.Done
            }
        }
    }

    val labels = listOf("Account", "Verify", "Reset", "Done")
    AuthSurface(modifier = modifier, scrollable = step != ForgotStep.Done) {
        AuthHeader(
            title = "Password recovery",
            subtitle = "Secure account reset",
            onBack = ::goBack,
            onClose = onClose,
            showClose = allowDismiss,
        )
        AuthProgress(labels = labels, activeIndex = ForgotStep.entries.indexOf(step))
        AnimatedVisibility(feedback != null) {
            feedback?.let {
                FeedbackBanner(feedback = it, onDismiss = { feedback = null })
                Spacer(Modifier.height(12.dp))
            }
        }
        AnimatedContent(targetState = step, label = "forgot_step") { current ->
            Column {
                when (current) {
                    ForgotStep.Username -> {
                        AuthHero(
                            icon = { IconBadge(icon = Icons.Outlined.Person) },
                            title = "Find your account",
                            subtitle = "Enter your username and we will confirm your recovery questions.",
                        )
                        AuthInputField(
                            value = username,
                            onChange = {
                                username = it
                                clear()
                            },
                            placeholder = "Username",
                            icon = Icons.Outlined.Person,
                            error = fieldErrors["username"],
                            enabled = !loading,
                            onDone = ::findAccount,
                        )
                        PrimaryButton(
                            text = "Find account",
                            onClick = ::findAccount,
                            loading = loading,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                        InfoCard(text = "Test accounts: demo / aaa. Recovery answers include fluffy, seoul, or pizza.")
                    }
                    ForgotStep.Security -> {
                        val user = foundUserKey?.let { mockUsers[it] }
                        val questionId = user?.securityQA?.getOrNull(questionIndex)?.first
                        val question = securityQuestions.firstOrNull { it.first == questionId }?.second ?: "What is your pet's name?"
                        AuthHero(
                            icon = { IconBadge(icon = Icons.Outlined.HelpOutline) },
                            title = "Verify identity",
                            subtitle = "Answer the security question attached to your account.",
                        )
                        QuestionPrompt(text = question)
                        AuthInputField(
                            value = securityAnswer,
                            onChange = {
                                securityAnswer = it
                                clear()
                            },
                            placeholder = "Your answer",
                            icon = Icons.Outlined.HelpOutline,
                            error = fieldErrors["security"],
                            enabled = !loading,
                            onDone = ::verify,
                        )
                        PrimaryButton(
                            text = "Verify",
                            onClick = ::verify,
                            loading = loading,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                    }
                    ForgotStep.Reset -> {
                        AuthHero(
                            icon = { IconBadge(icon = Icons.Outlined.Key) },
                            title = "Set new password",
                            subtitle = "Choose a new password for your Tonight account.",
                        )
                        AuthInputField(
                            value = newPassword,
                            onChange = {
                                newPassword = it
                                clear()
                            },
                            placeholder = "New password",
                            icon = Icons.Outlined.Lock,
                            password = true,
                            error = fieldErrors["newPw"],
                        )
                        Spacer(Modifier.height(12.dp))
                        AuthInputField(
                            value = confirmPassword,
                            onChange = {
                                confirmPassword = it
                                clear()
                            },
                            placeholder = "Confirm new password",
                            icon = Icons.Outlined.Lock,
                            password = true,
                            error = fieldErrors["confirmPw"],
                            onDone = ::resetPassword,
                        )
                        PrimaryButton(
                            text = "Reset password",
                            onClick = ::resetPassword,
                            loading = loading,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                    }
                    ForgotStep.Done -> {
                        SuccessPanel(
                            title = "Password reset",
                            body = "You can now sign in with your updated password.",
                            action = "Back to sign in",
                            onAction = onBack,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthSurface(
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    pinContentToBottom: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val base = Modifier
        .fillMaxSize()
        .background(palette.cardSurface)
        .statusBarsPadding()
    Box(modifier = modifier.then(base), contentAlignment = Alignment.TopCenter) {
        val columnModifier = Modifier
            .fillMaxSize()
            .widthIn(max = 460.dp)
            .padding(horizontal = 20.dp)
            .padding(
                top = 18.dp,
                bottom = if (pinContentToBottom) 0.dp else 18.dp,
            )
        Column(
            modifier = if (scrollable) columnModifier.verticalScroll(rememberScrollState()) else columnModifier,
            content = content,
        )
    }
}

@Composable
private fun AuthHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onClose: () -> Unit,
    showClose: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, label = "Back", onClick = onBack)
        Column(modifier = Modifier.weight(1f)) {
            val palette = LocalRestaurantPalette.current
            Text(text = title, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(text = subtitle, color = palette.mutedForeground, fontSize = 12.sp, maxLines = 1)
        }
        if (showClose) {
            CircleIconButton(icon = Icons.Filled.Close, label = "Close", onClick = onClose)
        }
    }
}

@Composable
private fun AuthHero(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.padding(bottom = 14.dp)) { icon() }
        Text(
            text = title,
            color = palette.foreground,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            color = palette.mutedForeground,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .widthIn(max = 320.dp),
        )
    }
}

@Composable
private fun LogoBadge() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(palette.brand),
        contentAlignment = Alignment.Center,
    ) {
        TonightLogoMark(modifier = Modifier.size(36.dp), color = Color.White)
    }
}

@Composable
private fun IconBadge(icon: ImageVector, tone: FeedbackType = FeedbackType.Success) {
    val palette = LocalRestaurantPalette.current
    val color = when (tone) {
        FeedbackType.Success -> palette.brand
        FeedbackType.Error -> palette.destructive
        FeedbackType.Warning -> palette.warning
    }
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(30.dp))
    }
}

@Composable
private fun AuthInputField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    password: Boolean = false,
    error: String? = null,
    enabled: Boolean = true,
    onDone: (() -> Unit)? = null,
) {
    val palette = LocalRestaurantPalette.current
    var showPassword by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.mutedSurface.copy(alpha = 0.70f))
                .border(
                    width = 1.dp,
                    color = if (error != null) palette.destructive else Color.Transparent,
                    shape = RoundedCornerShape(percent = 50),
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = value,
                onValueChange = { if (enabled) onChange(it) },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                visualTransformation = if (password && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (password) KeyboardType.Password else KeyboardType.Text,
                    imeAction = if (onDone != null) ImeAction.Done else ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
                textStyle = TextStyle(color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(placeholder, color = palette.mutedForeground.copy(alpha = 0.75f), fontSize = 15.sp)
                        }
                        inner()
                    }
                },
            )
            if (password) {
                Icon(
                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (showPassword) "Hide password" else "Show password",
                    tint = palette.mutedForeground,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { showPassword = !showPassword },
                )
            }
        }
        if (error != null) {
            Row(
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = palette.destructive, modifier = Modifier.size(14.dp))
                Text(text = error, color = palette.destructive, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    trailingIcon: ImageVector? = null,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (enabled && !loading) palette.brand else palette.mutedSurface)
            .clickable(enabled = enabled && !loading, role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
        } else {
            Text(
                text = text,
                color = if (enabled) Color.White else palette.mutedForeground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(trailingIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
            }
        }
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = text, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = if (muted) palette.mutedForeground else palette.brand,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
    )
}

@Composable
private fun CircleIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(palette.mutedSurface)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = label, tint = palette.foreground, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun FeedbackBanner(feedback: Feedback, onDismiss: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val color = when (feedback.type) {
        FeedbackType.Success -> palette.success
        FeedbackType.Error -> palette.destructive
        FeedbackType.Warning -> palette.warning
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = if (feedback.type == FeedbackType.Success) Icons.Filled.CheckCircle else Icons.Outlined.WarningAmber,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = feedback.message,
            color = color,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Dismiss",
            tint = color.copy(alpha = 0.75f),
            modifier = Modifier
                .size(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )
    }
}

@Composable
private fun AuthProgress(labels: List<String>, activeIndex: Int) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(bottom = 22.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Progress", color = palette.mutedForeground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(labels[activeIndex.coerceIn(labels.indices)], color = palette.brand, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { index, label ->
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(
                                when {
                                    index < activeIndex -> palette.success
                                    index == activeIndex -> palette.brand
                                    else -> palette.border
                                },
                            ),
                    )
                    Text(
                        text = label,
                        color = if (index <= activeIndex) palette.foreground else palette.mutedForeground.copy(alpha = 0.55f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoHint() {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .border(1.dp, palette.borderSoft, RoundedCornerShape(22.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = palette.brand, modifier = Modifier.size(16.dp))
            Text("Demo access", color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Text(
            text = "Username demo / Password aaa",
            color = palette.mutedForeground,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ReferralScanner(onSimulate: () -> Unit, onCancel: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .border(1.dp, palette.border, RoundedCornerShape(28.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(194.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.brand.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                .padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            DeterministicQrCode(code = "TONIGHT-REFERRAL", modifier = Modifier.fillMaxSize(), color = palette.mutedForeground.copy(alpha = 0.55f))
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(2.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .background(palette.brand),
            )
        }
        Text(
            text = "Point your camera at a referral QR.",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        TextAction(text = "Simulate scan", onClick = onSimulate, modifier = Modifier.padding(top = 8.dp))
        TextAction(text = "Cancel", onClick = onCancel, muted = true)
    }
}

@Composable
private fun PasswordStrength(password: String) {
    if (password.isEmpty()) return
    val palette = LocalRestaurantPalette.current
    val strength = listOf(
        password.length >= 6,
        password.any { it.isUpperCase() },
        password.any { it.isDigit() },
        password.length >= 10,
    ).count { it }
    val color = when {
        strength <= 1 -> palette.destructive
        strength == 2 -> palette.warning
        else -> palette.success
    }
    val label = when {
        strength <= 1 -> "Weak"
        strength == 2 -> "Fair"
        strength == 3 -> "Good"
        else -> "Strong"
    }
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (strength > index) color else palette.border),
                )
            }
        }
        Text(label, color = palette.mutedForeground, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun RegisterStepBottomBar(
    step: RegisterStep,
    referCode: String,
    loading: Boolean,
    securitySetupComplete: Boolean,
    onReferContinue: () -> Unit,
    onReferSkip: () -> Unit,
    onCredentialsNext: () -> Unit,
    onProfileNext: () -> Unit,
    onSecuritySubmit: () -> Unit,
    onComplete: () -> Unit,
    onGoLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .background(palette.cardSurface)
            .navigationBarsPadding()
            .imePadding(),
    ) {
        HorizontalDivider(color = palette.border.copy(alpha = 0.65f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp),
        ) {
        when (step) {
            RegisterStep.Refer -> {
                PrimaryButton(
                    text = if (referCode.isBlank()) "Continue" else "Apply and continue",
                    onClick = onReferContinue,
                    trailingIcon = Icons.Outlined.ChevronRight,
                )
                TextAction(
                    text = "Skip for now",
                    onClick = onReferSkip,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp),
                    muted = true,
                )
            }
            RegisterStep.Credentials -> {
                PrimaryButton(
                    text = "Next",
                    onClick = onCredentialsNext,
                    trailingIcon = Icons.Outlined.ChevronRight,
                )
            }
            RegisterStep.Profile -> {
                PrimaryButton(
                    text = "Next",
                    onClick = onProfileNext,
                    trailingIcon = Icons.Outlined.ChevronRight,
                )
            }
            RegisterStep.Security -> {
                PrimaryButton(
                    text = "Create account",
                    onClick = onSecuritySubmit,
                    trailingIcon = Icons.Outlined.ChevronRight,
                    enabled = securitySetupComplete,
                    loading = loading,
                )
            }
            RegisterStep.Done -> {
                PrimaryButton(
                    text = "Get started",
                    onClick = onComplete,
                    trailingIcon = Icons.Outlined.ChevronRight,
                )
            }
        }
        if (step == RegisterStep.Refer || step == RegisterStep.Credentials) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Already have an account?", color = palette.mutedForeground, fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                TextAction(text = "Sign in", onClick = onGoLogin)
            }
        }
        }
    }
}

@Composable
private fun RecoverySetupStepContent(
    questionIndexes: List<Int>,
    securityAnswers: List<String>,
    fieldErrors: Map<String, String>,
    expandedQuestionSlot: Int?,
    bottomPadding: Dp,
    onToggleExpand: (Int) -> Unit,
    onSelectQuestion: (Int, Int) -> Unit,
    onAnswer: (Int, String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val scrollState = rememberScrollState()
    val showScrollTopBorder by remember {
        derivedStateOf { scrollState.value > 0 }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AuthHero(
            icon = { IconBadge(icon = Icons.Outlined.HelpOutline) },
            title = "Recovery setup",
            subtitle = "Pick three questions so you can safely recover your account.",
        )
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AnimatedVisibility(
                visible = showScrollTopBorder,
                enter = fadeIn(tween(160, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(140)),
            ) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = palette.border,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding()
                    .padding(bottom = bottomPadding),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    repeat(3) { index ->
                        val takenElsewhere = questionIndexes.mapIndexedNotNull { slot, question ->
                            if (slot != index) question else null
                        }.toSet()
                        SecurityQuestionDropdownCard(
                            index = index,
                            questionIndex = questionIndexes[index],
                            answer = securityAnswers[index],
                            error = fieldErrors["security$index"],
                            expanded = expandedQuestionSlot == index,
                            takenElsewhere = takenElsewhere,
                            onToggleExpand = { onToggleExpand(index) },
                            onSelectQuestion = { selected -> onSelectQuestion(index, selected) },
                            onAnswer = { value -> onAnswer(index, value) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityQuestionDropdownCard(
    index: Int,
    questionIndex: Int,
    answer: String,
    error: String?,
    expanded: Boolean,
    takenElsewhere: Set<Int>,
    onToggleExpand: () -> Unit,
    onSelectQuestion: (Int) -> Unit,
    onAnswer: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "security_dropdown_chevron",
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(300, easing = FastOutSlowInEasing))
            .clip(RoundedCornerShape(22.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(22.dp))
            .padding(12.dp),
    ) {
        Text(
            text = "Question ${index + 1}",
            color = palette.mutedForeground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(palette.mutedSurface)
                .clickable(onClick = onToggleExpand)
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = securityQuestions[questionIndex].second,
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = if (expanded) "Collapse questions" else "Expand questions",
                tint = palette.mutedForeground,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(chevronRotation),
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                expandFrom = Alignment.Top,
            ) + fadeIn(tween(220, easing = FastOutSlowInEasing)),
            exit = shrinkVertically(
                animationSpec = tween(260, easing = FastOutSlowInEasing),
                shrinkTowards = Alignment.Top,
            ) + fadeOut(tween(180)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.mutedSurface.copy(alpha = 0.65f))
                    .padding(vertical = 4.dp),
            ) {
                securityQuestions.forEachIndexed { optionIndex, (_, label) ->
                    val selected = optionIndex == questionIndex
                    val disabled = takenElsewhere.contains(optionIndex)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    selected -> palette.brand.copy(alpha = 0.10f)
                                    disabled -> Color.Transparent
                                    else -> Color.Transparent
                                },
                            )
                            .clickable(enabled = !disabled, onClick = { onSelectQuestion(optionIndex) })
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = label,
                            color = when {
                                disabled -> palette.mutedForeground.copy(alpha = 0.45f)
                                selected -> palette.foreground
                                else -> palette.foreground
                            },
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f),
                        )
                        if (selected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = palette.brand,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
        AuthInputField(
            value = answer,
            onChange = onAnswer,
            placeholder = "Your answer",
            icon = Icons.Outlined.HelpOutline,
            error = error,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
private fun InfoCard(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.mutedForeground,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(palette.mutedSurface.copy(alpha = 0.70f))
            .padding(14.dp),
    )
}

@Composable
private fun QuestionPrompt(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.foreground,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(palette.brandSoftSurface)
            .border(1.dp, palette.brand.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
            .padding(16.dp),
    )
}

@Composable
private fun SuccessPanel(
    title: String,
    body: String,
    action: String,
    onAction: () -> Unit,
    showActionInPanel: Boolean = true,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconBadge(icon = Icons.Filled.Check, tone = FeedbackType.Success)
        Text(
            title,
            color = palette.foreground,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp),
        )
        Text(
            body,
            color = palette.mutedForeground,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .widthIn(max = 320.dp),
        )
        if (showActionInPanel) {
            PrimaryButton(text = action, onClick = onAction, modifier = Modifier.padding(top = 28.dp))
        }
    }
}
