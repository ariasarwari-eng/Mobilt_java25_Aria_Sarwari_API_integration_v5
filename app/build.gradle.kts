// Java-klass som används för att hantera information i form av: namn = värde, vår nyckel är skriven detta format.
import java.util.Properties
// läsa innehåll från en fil som i vår fall, local.properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

// localProperties objekt -> Properties localProperties = new Properties();
val localProperties = Properties()

// Hitta filen local.properties i projektets rot. Filobjektet sparas i localPropertiesFile
val localPropertiesFile = rootProject.file("local.properties")

// Kontrollera att filen finns, vill ej att gradel ska läsa en fil som ej finns
if (localPropertiesFile.exists()) {

    /*Läs local.properties och lägg informationen i localProperties.*/

    // FileInputStream(localPropertiesFile) -> Öppna filen och skapa en ström för att läsa innehållet.
    // localProperties.load(...) -> Läs in informationen från filen till vårt localProperties-objekt.
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.mobilutveckling.myweatherapp"
    compileSdk {
        version = release(37)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.mobilutveckling.myweatherapp"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /*
        * värdet som ska skapas är en String.
        * namnet som vår app kommer använda.
        * hämtar värdet från local.properties, dvs vår nyckel. extra " för at få med " runt värdet*/
        buildConfigField(
            "String",
            "OPENWEATHER_API_KEY",
            "\"${localProperties.getProperty("OPENWEATHER_API_KEY")}\""
        )
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}