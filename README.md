# KotlinSandbox

Конспект по книге "Kotlin in Action"

`+` заметки из Notion:
[Алгоритмы](notion/algorithms.md)
[Структуры данных](notion/data_structures.md)
[Patterns & App Architecture](notion/patterns_and_app_architecture.md)
[Многопоточность](notion/multithreading.md)
[Java](notion/java.md)
[Kotlin](notion/kotlin.md)
[Coroutines](notion/coroutines.md)
[Android](notion/android.md)
[Gradle](notion/gradle.md)

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
