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

        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/public")}
        maven { url = uri("https://maven.aliyun.com/repository/central")}
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin")}
        maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots")}


        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://jogamp.org/deployment/maven")
        mavenLocal()
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

        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/public")}
        maven { url = uri("https://maven.aliyun.com/repository/central")}
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin")}
        maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots")}

        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://jogamp.org/deployment/maven")
        mavenLocal()
    }
}


include(":composeApp")