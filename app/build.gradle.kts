plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose.compiler)
}

android {
    namespace = "com.example.o2assignment"
    compileSdk {
        version = release( libs.versions.compileSdk.get().toInt())
    }

    defaultConfig {
        applicationId = "com.example.o2assignment"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
    composeCompiler {
        includeSourceInformation = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":core:theme"))
    implementation(project(":core:network"))
    implementation(project(":core:navigation"))
    implementation(project(":core:data:card_repository"))
    implementation(project(":navigation"))
    implementation(project(":feature:scratch"))
    implementation(project(":feature:activate"))
    implementation(project(":feature:home"))

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.compose.adaptive)

    testImplementation(libs.bundles.test)
}