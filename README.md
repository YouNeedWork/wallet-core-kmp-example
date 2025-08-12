## Kotlin Multiplatform Wallet-Core + Compose Example

[中文版本 (Chinese)](./README_ZH.md)

This repository showcases how to integrate the following core capabilities in a Kotlin Multiplatform (Android & iOS) application using Compose Multiplatform:

1. Trust Wallet `wallet-core` (mnemonic generation, key & address derivation, message signing & verification)
2. Voyager for cross-platform screen navigation + ScreenModel (state/business logic)
3. In-app browser via `compose-webview-multiplatform`
4. Cross-platform networking with Ktor `HttpClient` (OkHttp on Android, Darwin engine on iOS)
5. Centralized dependency version management with `libs.versions.toml`

Primary shared module: `composeApp` (shared code in `composeApp/src/commonMain`).

---

## Tech Stack

| Capability     | Description                                  | Key Dependencies                                    |
| -------------- | -------------------------------------------- | --------------------------------------------------- |
| UI             | Compose Multiplatform (Material3, Resources) | `org.jetbrains.compose`                             |
| Navigation     | Voyager Navigator + ScreenModel              | `cafe.adriel.voyager:*`                             |
| In-app Browser | Compose WebView Multiplatform                | `io.github.kevinnzou:compose-webview-multiplatform` |
| Wallet         | Trust Wallet Core Kotlin bindings            | `com.trustwallet:wallet-core-kotlin*`               |
| Logging        | Kermit                                       | `co.touchlab:kermit`                                |
| Concurrency    | Kotlinx Coroutines                           | `org.jetbrains.kotlinx:kotlinx-coroutines-*`        |
| Networking     | Ktor Client (core + OkHttp + Darwin)         | `io.ktor:ktor-client-*`                             |

---

## Wallet-Core Integration

The project uses Trust Wallet Core Kotlin bindings for multi-chain wallet operations (mnemonic, keys, addresses, signing):

In `gradle/libs.versions.toml`:

```
wallet-core-kotlin = com.trustwallet:wallet-core-kotlin:{version}
wallet-core-kotlin-android = com.trustwallet:wallet-core-kotlin-android:{version}
wallet-core-kotlin-iosarm64 = ...
wallet-core-kotlin-iossimulatorarm64 = ...
wallet-core-kotlin-iosx64 = ...
```

In `composeApp/build.gradle.kts`:

```
commonMain.dependencies { implementation(libs.wallet.core.kotlin) }
androidMain.dependencies { implementation(libs.wallet.core.kotlin.android) }
iosMain.dependencies { implementation(libs.wallet.core.kotlin.iosarm64 /* etc */) }
```

Android entry point (`MainActivity.kt`) loads the native library:

```kotlin
System.loadLibrary("TrustWalletCore")
```

Usage sample (`HomeScreen.rt.kt`):

```kotlin
val wallet = HDWallet(256, "")          // 256-bit entropy -> mnemonic (English default)
val mnemonic = wallet.mnemonic
val btcAddress = wallet.getAddressForCoin(CoinType.Bitcoin)
val ethAddress = wallet.getAddressForCoin(CoinType.Ethereum)
val key = wallet.getKeyForCoin(CoinType.Ethereum)
val sig = EthereumMessageSigner.signMessage(key, "123")
val verified = EthereumMessageSigner.verifyMessage(key.getPublicKey(CoinType.Ethereum), "123", sig)
```

Notes:

- Never use an empty passphrase in production.
- Avoid logging mnemonics / private keys (shown here only for demonstration).
- iOS does not need a manual `loadLibrary` (static framework bundle).

---

## Navigation & ScreenModel (Voyager)

Voyager provides a lightweight, multiplatform navigation and state model pattern.

Key concepts:

- `Screen` = a navigation unit with a `Content()` composable.
- `ScreenModel` = lifecycle-aware state holder (analogous to ViewModel).

Example:

```kotlin
class HomeScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable override fun Content() {
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val state by screenModel.state.collectAsState()
        // Render based on state
    }
}
```

Benefits:

1. Works naturally with Compose
2. Consistent navigation abstraction across platforms
3. Lifecycle scoping simplifies resource cleanup

Suggested enhancements:

- A centralized `NavigatorHost` to manage stack + deep links
- Define a sealed `Route` hierarchy to avoid stringly-typed routes

---

## In-App Browser (WebView)

Dependency: `compose-webview-multiplatform` (added to `commonMain`).

Example (pseudo):

```kotlin
@Composable
fun InAppBrowser(url: String) {
    // WebView(state = rememberWebViewState(url)) { /* custom config */ }
}
```

Use cases:

- Campaign / marketing pages
- Third-party auth / payment callbacks
- Viewing block explorer links

Potential extensions:

- URL scheme interception for DApps / WalletConnect
- JS bridge for native <-> web interactions

---

## Ktor HttpClient (Multiplatform Networking)

Located in `Greeting.kt`:

```kotlin
private val client = HttpClient()
suspend fun greeting(): String = client.get("https://www.baidu.com/").bodyAsText()
```

Engines:

- Android -> OkHttp (`ktor-client-okhttp`)
- iOS -> Darwin (`ktor-client-darwin`)

Recommended configuration:

```kotlin
HttpClient {
    expectSuccess = true
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.INFO }
    defaultRequest { header("User-Agent", "KMP-App") }
}
```

Error handling:

- Repository layer returning `Result<T>` or custom sealed classes
- Platform-specific mapping for UI error messages

---

## Project Structure

```
composeApp/
  src/
    commonMain/   <-- shared business logic, UI (Compose), navigation, wallet, networking
    androidMain/  <-- Android-specific (Activity, loadLibrary)
    iosMain/      <-- iOS-specific (framework export)
gradle/libs.versions.toml <-- centralized dependency versions
```

---

## Build & Run

Android:

```
./gradlew :composeApp:assembleDebug
```

iOS:

```
./gradlew :composeApp:syncFramework
# Open the Xcode project under iosApp/ and run
```

Desktop (if a desktop target is later added):

```
./gradlew :composeApp:run
```

---

## Security Considerations (wallet-core)

1. Do not log mnemonics / private keys in production.
2. Use secure storage (Android Keystore / iOS Keychain) for sensitive data.
3. Verify user intent before signing arbitrary messages (phishing protection).
4. Track wallet-core release notes for breaking changes during upgrades.
5. Ensure thread safety if sharing an `HDWallet` instance; prefer derived public keys for read-only operations.

---

## Possible Next Steps

- Abstract a `WalletRepository` supporting multiple chains + caching (addresses, balances)
- Sealed `Route` + navigation extensions
- Dependency Injection (Koin / Kodein) for HttpClient & ScreenModels
- Network: timeouts, retry, offline cache policies
- Integrate WalletConnect / EIP-4361 (SIWE) auth flow
- JS bridge in WebView for account & signing interactions

---

## License

Educational example only. Add an appropriate LICENSE before distribution.

---

Feedback / contributions welcome. See the Chinese version here: [中文说明](./README_ZH.md)
