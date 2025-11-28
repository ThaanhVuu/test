plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.1"
}

group = "dhkthn"
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
//    mainModule.set("dhkthn.p2p")
    mainClass.set("dhkthn.p2p.Main")
}

javafx {
    version = "21.0.9"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")

    implementation("io.github.palexdev:materialfx:11.17.0")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
}