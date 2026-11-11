plugins {
    id("java")
}

group = "ch.wondercraft.loanm"
version = "1.1.1"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    dependencies {
        compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}