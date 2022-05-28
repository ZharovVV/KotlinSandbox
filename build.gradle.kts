plugins {
    kotlin("jvm") version "1.6.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.20")
    testImplementation("junit:junit:4.12")
}

group = "com.github.zharovvv"
version = "1.0-SNAPSHOT"
description = "com.github.zharovvv.kotlin-sandbox"

java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("com.github.zharovvv.high.order.functions.UseKotlinFunctionInJavaExample")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "com.github.zharovvv.high.order.functions.UseKotlinFunctionInJavaExample"
    }
    //добавление зависимостей в jar
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}
