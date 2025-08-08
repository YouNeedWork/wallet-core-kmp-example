rootProject.name = "FinanceTrchingApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {

        maven {
            url = uri("https://maven.pkg.github.com/trustwallet/wallet-core")
            credentials {
                username = "YouNeedWork"
                password = "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"
            }
        }

        maven { url = uri("https://maven.aliyun.com/repository/public")}
        maven { url = uri("https://maven.aliyun.com/repository/central")}
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin")}
        maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots")}



        mavenLocal()

        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/trustwallet/wallet-core")
            credentials {
                username = "YouNeedWork"
                password = "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"
            }
        }

        maven { url = uri("https://maven.aliyun.com/repository/public")}
        maven { url = uri("https://maven.aliyun.com/repository/central")}
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin")}
        maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots")}

        mavenLocal()

        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        google()
    }
}


include(":composeApp")