import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.graalvm.buildtools.native") version "0.10.6"
}

group = "com.imer1c.alegliceu"
version = "1.0-SNAPSHOT"

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
    mainModule.set("com.imer1c.alegliceu")
    mainClass.set("com.imer1c.alegliceu.AlegLiceu")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
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
        imageName = "AlegLiceu"
        installerName = "AlegLiceu"
        installerType = "dmg"

        appVersion = "1.0.0"

        outputDir = "$buildDir/installer"
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.imer1c.alegliceu.AlegLiceu"
    }
}

graalvmNative {
    toolchainDetection.set(true)
    binaries {
        named("main") {
            imageName.set("AlegLiceu")
            mainClass.set("com.imer1c.alegliceu.AlegLiceu")
            buildArgs.addAll(listOf(
                "--no-fallback",
                "--enable-url-protocols=http,https",
                "-H:+UnlockExperimentalVMOptions",
                //"-H:IncludeResources=.*\\.dylib|.*\\.so|.*\\.dll|.*\\.ttf|.*\\.css|.*\\.bss",
                "-H:+ReportUnsupportedElementsAtRuntime",
                "-H:+AllowVMInspection",
               // "--add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
               // "--add-exports=javafx.graphics/com.sun.glass.utils=ALL-UNNAMED",
               // "--add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
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

    group = "verification"

    val jarFile = tasks.shadowJar.get().archiveFile.get().asFile

    commandLine = listOf(
        "java",
        "-agentlib:native-image-agent=config-merge-dir=graal-config",
        "--module-path",
        configurations.runtimeClasspath.get().asPath,
        "--add-modules",
        "javafx.controls,javafx.fxml,javafx.swing,javafx.graphics",
        "-jar",
        jarFile.absolutePath
    )

    doFirst {
        delete(file("graal-config"))
    }
}

tasks.register<Copy>("agentlib") {
    dependsOn(agentLib)

    group = "verification"

    val dest = file("src/main/resources/META-INF/native-image")

    from(file("graal-config"))
    into(dest)

    doFirst {
        delete(dest)

        println("Update graal VM config")
    }
}

tasks.register<Copy>("copyGraalConfig") {
    group = "verification"

    val dest = file("src/main/resources/META-INF/native-image")

    from(file("graal-config"))
    into(dest)

    doFirst {
        delete(dest)

        println("Update graal VM config")
    }
}

tasks.register<Copy>("copyJFXNatives") {
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.contains("javafx") && it.name.endsWith(".jar") && it.isFile
        }.map { zipTree(it) }
    })

    val dest = file("$buildDir/javafx-natives")

    include("**/*.dylib", "**/*.so", "**/*.dll")
    into(dest)

    doFirst {
        delete(dest)
    }
}

tasks.register<Copy>("packageForMacOS") {

    dependsOn(tasks.nativeCompile)

    val nativeBinary = file("build/native/nativeCompile")
    val output = file("build/native-app/${project.name}.app")
    val jfxNatives = file("build/javafx-natives")

    val macOS = file("$output/Contents/MacOS")
    val res = file("$output/Contents/Resources")

    from (jfxNatives) {
        include { !it.isDirectory }
    }
    from(nativeBinary)

    into(macOS)

    doFirst {
        delete(output)
        output.mkdirs()

        macOS.mkdirs()
        res.mkdirs()

        file("$output/Contents/Info.plist").writeText("""
        <?xml version="1.0" ?>
        <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "https://www.apple.com/DTDs/PropertyList-1.0.dtd">
        <plist version="1.0">
         <dict>
                <key>CFBundleName</key>
                <string>${project.name}</string>
                <key>CFBundleDisplayName</key>
                <string>${project.name}</string>
                <key>CFBundleExecutable</key>
                <string>${project.name}</string>
                <key>CFBundleIdentifier</key>
                <string>${group}</string>
                <key>CFBundleVersion</key>
                <string>1.0</string>
                <key>CFBundlePackageType</key>
                <string>APPL</string>
                <key>CFBundleIconFile</key>
                <string>icon.icns</string>
         </dict>
        </plist>

    """.trimIndent())
    }

}