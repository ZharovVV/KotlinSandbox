package com.github.zharovvv.lambda


fun List<Person>.personNamesStartsWithJ(): List<String> {
    /**
     * При вызове метода asSequence():
     * Создается объект (анонимный внутренний класс, реализующий интерфейс [Sequence],
     * у которого имеется единственная функция:
     * public operator fun iterator(): Iterator<T>).
     * Реализацией этого метода является отложенный вызов функции iterator() исходной коллекции (List<Person>).
     * (Под отложенным вызовом имеется в виду - вызов метода iterator() исходной коллекии в момент вызова метода
     * iterator() у [Sequence].
     * По итогу метод asSequence() возвращает Sequence<Person>.
     */
    return this.asSequence()
        /**
         * При вызове метода map:
         * Создается объект [TransformingSequence], в конструктор которого передается
         * изначальная последовательность (Sequence<Person>) и лямбда-выражение для трансформации (которое
         * передается в качестве аргумента функции map).
         *
         * internal class TransformingSequence<T, R>
         * constructor(private val sequence: Sequence<T>, private val transformer: (T) -> R) : Sequence<R> {
         *      override fun iterator(): Iterator<R> = object : Iterator<R> {
         *          val iterator = sequence.iterator()
         *          override fun next(): R {
         *              return transformer(iterator.next())
         *          }
         *
         *          override fun hasNext(): Boolean {
         *              return iterator.hasNext()
         *          }
         *      }
         *
         *      internal fun <E> flatten(iterator: (R) -> Iterator<E>): Sequence<E> {
         *          return FlatteningSequence<T, R, E>(sequence, transformer, iterator)
         *      }
         * }
         *
         * Таким образом, TransformingSequence является декоратором (оберткой) изначальной последовательности,
         * добавляя дополнительное поведение (путем transformer) к итератору,
         * полученному от изначальной последовательности.
         * По итогу метод возвращает Sequence<String>.
         */
        .map { person -> person.name }
        /**
         * Суть работы анологична предыдущему методу.
         * При вызове filter создается объект [FilteringSequence], который добавляет свое поведение в работу итератора.
         * По итогу метод возвращает Sequence<String>.
         */
        .filter { personName -> personName.startsWith("j", ignoreCase = true) }
        /**
         * При вызове данного метода, создается пустая коллекция,
         * осуществляется обход итератора итоговой последовательности.
         * В теле цикла при обходе в изначально пустую коллекцию добавляются элементы из итератора.
         * По итогу метод возвращает List<String>.
         * toList - является аналогом терминальной операции в Java Stream API .collect(Collectors.toList()).
         */
        .toList()
}