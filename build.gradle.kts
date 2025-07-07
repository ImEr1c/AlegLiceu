import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.graalvm.buildtools.native") version "0.10.6"
}

val appName: String by project
val javafx_modules: String by project
val version: String by project
val group: String by project
val appMainClass: String by project

val osName = System.getProperty("os.name")
val platform: String = when {
    osName.contains("windows") -> "windows"
    osName.contains("mac") -> "mac"
    else -> "linux"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set(group)
    mainClass.set(appMainClass)
}

javafx {
    version = "21"
    modules = javafx_modules.split(",")
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("org.apache.pdfbox:pdfbox:3.0.5")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    launcher {
        name = "app"
    }

    jpackage {
        imageName = appName
        installerName = appName
        installerType = when {
            platform.contains("mac") -> "pkg"
            platform.contains("windows") -> "msi"
            else -> null
        }

        appVersion = version

        outputDir = "build/installer"
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = appMainClass
    }
}

graalvmNative {
    toolchainDetection.set(true)
    binaries {
        named("main") {
            imageName.set(appName)
            mainClass.set(appMainClass)
            buildArgs.addAll(listOf(
                "--no-fallback",
                "--enable-url-protocols=http,https",
                "-H:+UnlockExperimentalVMOptions",
                "-H:+ReportUnsupportedElementsAtRuntime",
                "-H:+AllowVMInspection",
                "-H:+ReportExceptionStackTraces",
                "--module-path", configurations.runtimeClasspath.get().asPath,
                "--add-modules", "javafx.controls,javafx.fxml,javafx.swing,javafx.graphics"
            ))
            resources.autodetect()
        }
    }
}

val agentLib = tasks.register<Exec>("runAgentLibScript") {
    dependsOn(tasks.shadowJar)

    val jarFile = tasks.shadowJar.get().archiveFile.get().asFile

    commandLine = listOf(
        "java",
        "-agentlib:native-image-agent=config-merge-dir=graal-config/${platform}",
        "--module-path",
        configurations.runtimeClasspath.get().asPath,
        "--add-modules",
        javafx_modules,
        "-jar",
        jarFile.absolutePath
    )

    doFirst {
        delete(file("graal-config/${platform}"))
    }
}

tasks.register<Copy>("agentlib") {
    dependsOn(agentLib)

    group = "verification"

    val dest = file("src/main/resources/META-INF/native-image")

    from(file("graal-config/${platform}"))
    into(dest)

    doFirst {
        delete(dest)

        println("Updated graal VM config")
    }
}

tasks.register<Copy>("copyGraalConfig") {
    group = "verification"

    val dest = file("src/main/resources/META-INF/native-image")

    from(file("graal-config/${platform}"))
    into(dest)

    doFirst {
        delete(dest)

        println("Updated graal VM config")
    }
}

tasks.register("packageForMac") {
    group = "installers"

    dependsOn(tasks.jpackage)
    onlyIf { platform.equals("mac") }

    doLast {
        val dest = file("build/installer")

        println("Installer available at ${dest.toURI()}")
    }
}