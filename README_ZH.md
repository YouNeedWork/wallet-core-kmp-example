## 项目概览

本示例项目演示如何在 Kotlin Multiplatform (Android & iOS) + Compose Multiplatform 应用中集成以下核心能力：

1. Trust Wallet `wallet-core` 跨平台加密钱包能力（助记词生成、地址派生、消息签名与验证）。
2. Voyager 作为多端一致的屏幕路由 / 导航与 ScreenModel（业务状态模型）管理。
3. 内置跨平台 WebView（`compose-webview-multiplatform`）实现应用内浏览器能力。
4. Ktor `HttpClient` 跨平台网络访问（Android 使用 OkHttp，iOS 使用 Darwin 引擎）。
5. 统一的依赖版本管理（`libs.versions.toml`）。

> 代码主模块：`composeApp`，公共逻辑位于 `composeApp/src/commonMain`。

---

## 技术栈

| 能力       | 说明                                         | 关键依赖                                            |
| ---------- | -------------------------------------------- | --------------------------------------------------- |
| UI         | Compose Multiplatform (Material3, Resources) | `org.jetbrains.compose`                             |
| 路由导航   | Voyager Navigator + ScreenModel              | `cafe.adriel.voyager:*`                             |
| 内置浏览器 | Compose WebView Multiplatform                | `io.github.kevinnzou:compose-webview-multiplatform` |
| 钱包功能   | Trust Wallet Core Kotlin 绑定                | `com.trustwallet:wallet-core-kotlin*`               |
| 日志       | Kermit                                       | `co.touchlab:kermit`                                |
| 协程       | Kotlinx Coroutines                           | `org.jetbrains.kotlinx:kotlinx-coroutines-*`        |
| 网络       | Ktor Client (core + OkHttp + Darwin)         | `io.ktor:ktor-client-*`                             |

---

## wallet-core 集成

`wallet-core` 提供跨链助记词、密钥、地址、签名等能力。项目使用官方 Kotlin 多平台绑定：

`gradle/libs.versions.toml` 中声明：

```
wallet-core-kotlin = com.trustwallet:wallet-core-kotlin:{version}
wallet-core-kotlin-android = com.trustwallet:wallet-core-kotlin-android:{version}
wallet-core-kotlin-iosarm64 = ...
wallet-core-kotlin-iossimulatorarm64 = ...
wallet-core-kotlin-iosx64 = ...
```

`composeApp/build.gradle.kts` 中：

```
commonMain.dependencies { implementation(libs.wallet.core.kotlin) }
androidMain.dependencies { implementation(libs.wallet.core.kotlin.android) }
iosMain.dependencies { implementation(libs.wallet.core.kotlin.iosarm64 ... 等) }
```

Android 入口（`MainActivity.kt`）通过：

```kotlin
System.loadLibrary("TrustWalletCore")
```

加载 native 库。

示例使用（摘自 `HomeScreen.rt.kt`）：

```kotlin
val wallet = HDWallet(256, "")               // 256 位熵创建助记词（默认英文）
val mnemonic = wallet.mnemonic
val btcAddress = wallet.getAddressForCoin(CoinType.Bitcoin)
val ethAddress = wallet.getAddressForCoin(CoinType.Ethereum)
val key = wallet.getKeyForCoin(CoinType.Ethereum)
val sig = EthereumMessageSigner.signMessage(key, "123")
val verified = EthereumMessageSigner.verifyMessage(key.getPublicKey(CoinType.Ethereum), "123", sig)
```

要点：

- 生产环境不要使用空口令（第二个参数推荐设置 passphrase 或硬件安全方案）。
- 注意私钥/助记词的内存生命周期与日志泄露风险（示例中直接打印仅作演示）。
- iOS 端无需手动 `loadLibrary`，因为静态 Framework 已封装。

---

## 路由与 ScreenModel（Voyager）

项目采用 Voyager：

- `Screen` 表示一个页面，可组合 UI。
- `ScreenModel` 类似 ViewModel，承载状态与业务逻辑，可在 `rememberScreenModel { ... }` 中创建。

`HomeScreen` 示例：

```kotlin
class HomeScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable override fun Content() {
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val state by screenModel.state.collectAsState()
        // ... 根据状态渲染 UI
    }
}
```

优势：

1. 与 Compose 协同，API 简洁。
2. 支持多平台一致导航模型。
3. ScreenModel 生命周期与屏幕绑定，利于资源释放。

扩展建议：

- 封装通用 `NavigatorHost`，集中管理栈与 Deep Link。
- 为路由定义统一枚举或常量，避免硬编码。

---

## 内置浏览器（WebView）

依赖：`api(libs.compose.webview.multiplatform)`（放在 `commonMain`，实现多端统一 API）。

使用步骤（示例伪代码）：

```kotlin
@Composable
fun InAppBrowser(url: String) {
    // WebView(state = rememberWebViewState(url)) { /* 自定义配置 */ }
}
```

可用场景：

- 展示 H5 活动页
- 第三方授权 / 支付回调
- 调试外部区块浏览器链接

可进一步封装：

- 拦截 URL Scheme 实现 DApp / WalletConnect 交互
- 注入 Javascript Bridge 实现原生 <-> H5 通信

---

## Ktor HttpClient 跨平台网络

定义位置：`Greeting.kt`

```kotlin
private val client = HttpClient()
suspend fun greeting(): String = client.get("https://www.baidu.com/").bodyAsText()
```

在多平台中：

- Android 自动挑选 OkHttp（本项目显式依赖 `ktor-client-okhttp`）
- iOS 使用 Darwin 引擎（`ktor-client-darwin`）

推荐增强：

```kotlin
HttpClient {
    expectSuccess = true
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.INFO }
    defaultRequest { header("User-Agent", "KMP-App") }
}
```

错误处理策略建议：

- 定义 `Repository` 层返回 `Result<T>` 或自定义 sealed class。
- 根据平台（例如 iOS）在 Swift 层更好地展示错误。

---

## 目录结构简述

```
composeApp/
  src/
    commonMain/
      kotlin/  <-- 共享业务逻辑、UI（Compose）、路由、钱包、网络
    androidMain/ <-- Android 特定（Activity、System.loadLibrary）
    iosMain/      <-- iOS 特定（Framework 导出）
gradle/libs.versions.toml <-- 版本与依赖集中管理
```

---

## 构建与运行

Android：

```
./gradlew :composeApp:assembleDebug
```

iOS：

```
./gradlew :composeApp:syncFramework
# 然后在 Xcode 中打开 iosApp/ 目录下工程或使用 Xcode 运行
```

桌面（如已开启 desktopTarget，可扩展）：

```
./gradlew :composeApp:run
```

---

## 安全注意事项（与 wallet-core 相关）

1. 演示代码中不要在生产环境打印助记词、私钥。
2. 建议使用系统安全存储（Android Keystore / iOS Keychain）。
3. 进行消息签名时确认待签名内容来源可信，防止钓鱼签名。
4. 升级 wallet-core 时跟踪 release note，注意潜在 breaking changes。
5. 若引入多线程并发访问同一 `HDWallet`，需确保线程安全或使用派生后的只读公钥对象。

---

## 可拓展的下一步

- 抽象 WalletRepository：支持多种链 & 缓存地址 / 余额。
- 封装统一 `Route` sealed class + Navigator 扩展函数。
- 引入 DI（如 Koin / Kodein）管理 HttpClient 与 ScreenModel 依赖。
- 为网络层加入超时 / 重试 / 断网缓存策略。
- 集成 WalletConnect / EIP-4361(SIWE) 登录流程。
- 为 WebView 注入与账户交互的 JS Bridge。

---

## License

示例仓库仅作学习参考，请依据实际业务与合规要求调整（如需对外发布请补充 LICENSE）。

---

返回英文版: [English README](./README.md)
