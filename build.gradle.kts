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
        compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
        compileOnly("net.luckperms:api:5.5")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}