import org.gradle.api.tasks.bundling.Jar
import java.net.URL
import java.io.File

plugins {
    kotlin("jvm") version "2.0.10"
    id("application")
}

group = "dev.flounder"
version = "1.0"

application {
    mainClass.set("finder.DuplicateFinderKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.commonmark:commonmark:0.22.0")
    implementation("it.unimi.dsi:fastutil:8.2.2")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("com.google.guava:guava:32.0.0-jre")
}

tasks {
    test {
        useJUnitPlatform()
    }

    task("fatJar", type = Jar::class) {
        group = "build"
        description = "Assembles a fat JAR containing the main classes and all dependencies"
        archiveFileName.set("duplicate-finder.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest { attributes["Main-Class"] = "finder.DuplicateFinderKt" }

        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }

    jar {
        manifest {
            attributes["Main-Class"] = "finder.DuplicateFinderKt"
        }
    }

    register("downloadWordsList") {
        val source = "https://www.mit.edu/~ecprice/wordlist.10000"
        group = "verification"
        description = "Downloads words for generating test data from $source and places it under ./src/test/resources"

        doLast {
            val file = File("./src/test/resources/words")

            URL(source).openStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            println("File downloaded to: ${file.absolutePath}")
        }
    }
}

kotlin {
    jvmToolchain(16)
}


