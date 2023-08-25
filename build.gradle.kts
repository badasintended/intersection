plugins {
    id("java")
    `maven-publish`
}

group = "lol.bai"
version = "0.1"

repositories {
    mavenCentral()
}

sourceSets {
    val generator by creating
}

dependencies {
    "generatorImplementation"("org.ow2.asm:asm:9.5")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/badasintended/intersection")
            name = "GitHub"
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }

        maven {
            name = "B2"
            url = rootProject.projectDir.resolve(".b2").toURI()
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        dependsOn("runGenerator")
        from(layout.buildDirectory.file("/aaaa"))
    }

    create<JavaExec>("runGenerator") {
        group = "run"
        classpath = sourceSets["generator"].runtimeClasspath
        mainClass = "lol.bai.intersection.generator.Generator"

        args(layout.buildDirectory.file("/aaaa").get().asFile.absolutePath)
    }
}