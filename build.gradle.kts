plugins {
    kotlin("jvm") version "1.9.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    /*
    Зависимость от стандартной библиотеки (stdlib) автоматически добавляется в каждый исходный набор.
    Версия используемой стандартной библиотеки такая же, как и версия плагина Kotlin Gradle.
    Подробнее тут https://kotlinlang.org/docs/gradle-configure-project.html#dependency-on-the-standard-library
     */
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
    testImplementation("junit:junit:4.12")
}

group = "com.github.zharovvv"
version = "1.0-SNAPSHOT"
description = "com.github.zharovvv.kotlin-sandbox"

java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += listOf(
//        "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
        "-opt-in=kotlin.RequiresOptIn",
        "-opt-in=kotlinx.coroutines.InternalCoroutinesApi"
    )
}

tasks.withType<JavaCompile> {
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
