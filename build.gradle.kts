plugins {
    java
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.antonio32a"
version = "1.0.0"
description = "Ant Core"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // Ant
    implementation("com.antonio32a:ant-private-api-data:1.0.0")

    // MC and related utils
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    implementation("cloud.commandframework:cloud-core:1.8.3")
    implementation("cloud.commandframework:cloud-annotations:1.8.3")
    implementation("cloud.commandframework:cloud-paper:1.8.3")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.8.3")
    implementation("org.inventivetalent:reflectionhelper:1.18.13-SNAPSHOT")

    // Lombok and misc utils
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks {
    test {
        useJUnitPlatform()
    }

    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )

        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks["shadowJar"]) {
                classifier = ""
            }
            artifact(tasks["sourcesJar"])
        }
    }
}
