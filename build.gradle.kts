plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "ch.wondercraft.loanm"
version = "1.1.3"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    implementation("org.bstats:bstats-bukkit:3.2.1")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { setOf(it) }

    dependencies {
        // Only merge bStats into the final jar, no other dependencies
        exclude { it.moduleGroup != "org.bstats" }
    }

    // Relocate bStats into the plugin's package to avoid conflicts with other plugins
    relocate("org.bstats", "${project.group}.libs.bstats")
}