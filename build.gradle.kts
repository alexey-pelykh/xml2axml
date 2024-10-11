import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("net.sf.kxml:kxml2:2.3.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:1.4")
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = "com.codyi.xml2axml.test.Main"
        }
        isZip64 = true
        // Ignores the "-all" classifier shadow uses by default
        archiveClassifier.set("")
    }
    register("executable") {
        logger.info("Packaging ${project.name} into an executable binary...")
        dependsOn("shadowJar")

        doLast {
            val jarFile = File(
                project.buildDir,
                "libs/${project.name}-${project.version}.jar"
            )
            require(jarFile.exists()) { "shadowJar output file at ${jarFile.canonicalPath} does not exist!" }
            val executableFile = File(project.buildDir, "libs/${project.name}")

            executableFile.apply {
                writeText("#!/usr/bin/env bash\nexec java -jar \$0 \"\$@\"\n")
                appendBytes(jarFile.readBytes())
                setExecutable(true)
            }
        }
    }
}
