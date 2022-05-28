# KotlinSandbox

Конспект по книге "Kotlin in Action"

### Запуск проекта (gradle)

* Прописать main-класс в _build.gradle.kts_

```kotlin
application {
    mainClass.set("com.github.zharovvv.delegating.DelegatingKt")
}
```

* Собрать jar:

```
gradle jar
```

* Запустить jar:

```
java -jar <Project Directory>/build/libs/kotlin-sandbox-1.0-SNAPSHOT.jar
```
