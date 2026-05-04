import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

root = r"c:\CHANGERS\Crypseal\crypseal-android"

# 1. gradle.properties
create_file(os.path.join(root, "gradle.properties"), """
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
""")

# 2. settings.gradle.kts
create_file(os.path.join(root, "settings.gradle.kts"), """
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Crypseal"
include(":app")
include(":crypseal-runtime")
include(":crypseal-shell-bridge")
include(":crypseal-guard")
include(":ui")
""")

# 3. Root build.gradle.kts
create_file(os.path.join(root, "build.gradle.kts"), """
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
""")

# Common library build.gradle.kts
lib_build_gradle = """
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.harrowhaus.crypseal.[MODULE]"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
"""

modules = {
    "crypseal-runtime": "runtime",
    "crypseal-shell-bridge": "shellbridge",
    "crypseal-guard": "guard",
    "ui": "ui"
}

for mod, ns in modules.items():
    mod_path = os.path.join(root, mod)
    create_file(os.path.join(mod_path, "build.gradle.kts"), lib_build_gradle.replace("[MODULE]", ns.replace("-", "")))
    os.makedirs(os.path.join(mod_path, f"src/main/java/com/harrowhaus/crypseal/{ns}"), exist_ok=True)

# 4. app module
app_build = """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.harrowhaus.crypseal.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.harrowhaus.crypseal"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":crypseal-runtime"))
    implementation(project(":crypseal-shell-bridge"))
    implementation(project(":crypseal-guard"))
    implementation(project(":ui"))
    
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
}
"""
app_path = os.path.join(root, "app")
create_file(os.path.join(app_path, "build.gradle.kts"), app_build)

manifest = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:name=".CrypsealApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Crypseal"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Crypseal">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Crypseal">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
"""
create_file(os.path.join(app_path, "src/main/AndroidManifest.xml"), manifest)

app_class = """package com.harrowhaus.crypseal.app

import android.app.Application

class CrypsealApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
"""
create_file(os.path.join(app_path, "src/main/java/com/harrowhaus/crypseal/app/CrypsealApplication.kt"), app_class)

main_activity = """package com.harrowhaus.crypseal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CrypsealShell()
                }
            }
        }
    }
}

@Composable
fun CrypsealShell() {
    Text(text = "Crypseal Android Shell Initialized")
}
"""
create_file(os.path.join(app_path, "src/main/java/com/harrowhaus/crypseal/app/MainActivity.kt"), main_activity)

# dummy res
create_file(os.path.join(app_path, "src/main/res/values/styles.xml"), """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.Crypseal" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
""")

print("Scaffolded Crypseal Android project.")
