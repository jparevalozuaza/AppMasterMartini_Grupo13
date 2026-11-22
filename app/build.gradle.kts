plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

android {
    namespace = "cl.duoc.mastermartini"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.mastermartini"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Cobertura de las pruebas unitarias (JaCoCo integrado en Android Gradle Plugin).
        // Se genera con: gradlew createDebugUnitTestCoverageReport
        debug {
            enableUnitTestCoverage = true
        }
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")
    // Navegación entre pantallas (NavHost, NavController, composable)
    implementation("androidx.navigation:navigation-compose:2.9.8")
    // Permite usar ViewModels dentro de Compose (función viewModel())
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    // Íconos básicos de Material (menú ☰, casa, persona, estrella)
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")
    // DataStore: guarda preferencias pequeñas (sesión y accesibilidad)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // Retrofit: cliente HTTP para consumir APIs REST (microservicio propio y API externa)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Convertidor Gson: transforma JSON en objetos Kotlin y viceversa
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // Corrutinas: ejecutan las llamadas de red en segundo plano sin congelar la pantalla
    // (versión sugerida en la Guía 14; Retrofit y Gson ya usan la 2.11.0 de la guía)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // ───────────── PRUEBAS UNITARIAS (Guía 15) ─────────────
    // Kotest: estilo de pruebas StringSpec y comparaciones legibles (shouldBe…)
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    // JUnit 5: el motor que ejecuta las pruebas
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    // Gradle 9 exige declarar el lanzador de JUnit 5 de forma explícita
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
    // MockK: crea objetos simulados (mocks) del repositorio y de la API
    testImplementation("io.mockk:mockk:1.13.10")
    // Corrutinas en pruebas: reemplaza el hilo principal y ejecuta sin esperas reales
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    // Las pruebas de interfaz (Compose UI Test) ya estaban configuradas arriba con el BOM:
    // androidTestImplementation(ui-test-junit4) y debugImplementation(ui-test-manifest)
}

// Obligatorio para usar JUnit 5 (Guía 15)
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}