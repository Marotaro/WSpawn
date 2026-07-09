plugins {
    id("java")
}

group = "ch.wondercraft.loanm"
version = "0.0.0-SNAPSHOT"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}