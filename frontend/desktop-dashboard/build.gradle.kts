import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "desktop-dashboard"
            packageVersion = "1.0.0"
        }
    }

    tasks.register<Jar>("buildJar") {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        from(sourceSets["main"].output)
        archiveFileName.set("desktop-dashboard.jar")
        destinationDirectory.set(file("$buildDir/libs"))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}



