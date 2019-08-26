package com.example

import kotlin.Exception

interface Processor {
    fun canProcess(instance: Cmd): Boolean

    fun process(instance: Cmd): String

    fun resolveHandler(instance: Cmd): String?

    fun execHandler(handler: String): String
}

interface Cmd
interface A : Cmd
interface B : Cmd
interface C : Cmd
interface D : Cmd
interface E : Cmd
interface F : Cmd
interface G : Cmd
interface H : Cmd
interface I : Cmd
interface J : Cmd
interface K : Cmd
interface L : Cmd
interface M : Cmd
interface N : Cmd

class AImpl : A
class BImpl : B
class CImpl : C
class DImpl : D
class EImpl : E
class FImpl : F
class GImpl : G
class HImpl : H
class IImpl : I
class JImpl : J
class KImpl : K
class LImpl : L
class MImpl : M
class NImpl : N
class NotFound : Cmd

class NotFoundException(instance: Any) : Exception("Cannot process $instance")

fun main_local_processor_strategy() {
    val time = 1000000
    val smallList = listOf(
        AImpl(),
        BImpl(),
        CImpl(),
        DImpl(),
        EImpl(),
        NotFound()
    )
    val smallListProcessor = SmallListProcessor()
    val a1 = useCanProcess(smallListProcessor, smallList, time)
    val a2 = useTryCatch(smallListProcessor, smallList, time)
    val a3 = useResolveExec(smallListProcessor, smallList, time)

    println("useCanProcess() took ${a1} ms to process $time on smallList")
    println("useTryCatch() took ${a2} ms to process $time on smallList")
    println("useResolveExec() took ${a3} ms to process $time on smallList")

    val normalList = listOf(
        AImpl(),
        BImpl(),
        CImpl(),
        DImpl(),
        EImpl(),
        FImpl(),
        GImpl(),
        HImpl(),
        IImpl(),
        JImpl(),
        KImpl(),
        LImpl(),
        MImpl(),
        NImpl(),
        NotFound()
    )
    val mediumListProcessor = MediumListProcessor()
    val b1 = useCanProcess(mediumListProcessor, normalList, time)
    val b2 = useTryCatch(mediumListProcessor, normalList, time)
    val b3 = useResolveExec(mediumListProcessor, normalList, time)
    println("useTryCatch() took ${b1} ms to process $time on smallList")
    println("useCanProcess() took ${b2} ms to process $time on smallList")
    println("useResolveExec() took ${b3} ms to process $time on smallList")

// Result:
// useCanProcess() took 661 ms to process 1000000 on smallList
// useTryCatch() took 1699 ms to process 1000000 on smallList
// useResolveExec() took 29 ms to process 1000000 on smallList
// useTryCatch() took 3745 ms to process 1000000 on smallList
// useCanProcess() took 3233 ms to process 1000000 on smallList
// useResolveExec() took 2119 ms to process 1000000 on smallList
// ----> use resolve/exec model
}

fun useTryCatch(processor: Processor, list: List<Cmd>, time: Int): Long {
    val start = System.currentTimeMillis()
    val wrapper = TryCatchWrapper(processor)
    for (i in 0..time) {
        list.forEach {
            wrapper.process(it)
        }
    }
    val end = System.currentTimeMillis()
    return end - start
}

fun useCanProcess(processor: Processor, list: List<Cmd>, time: Int): Long {
    val start = System.currentTimeMillis()
    val wrapper = CanProcessWrapper(processor)
    for (i in 0..time) {
        list.forEach {
            wrapper.process(it)
        }
    }
    val end = System.currentTimeMillis()
    return end - start
}

fun useResolveExec(processor: Processor, list: List<Cmd>, time: Int): Long {
    val start = System.currentTimeMillis()
    val wrapper = ResolveExecWrapper(processor)
    for (i in 0..time) {
        list.forEach {
            wrapper.process(it)
        }
    }
    val end = System.currentTimeMillis()
    return end - start
}

class TryCatchWrapper(private val wrappee: Processor) {
    fun process(instance: Cmd): String {
        return try {
            wrappee.process(instance)
        } catch (exception: Exception) {
            if (exception is NotFoundException) {
                // println("Call remote processor")
                "from remote processor"
            } else {
                throw (exception)
            }
        }
    }
}


class CanProcessWrapper(private val wrappee: Processor) {
    fun process(instance: Cmd): String {
        return if (wrappee.canProcess(instance)) {
            wrappee.process(instance)
        } else {
            // println("Call remote processor")
            "from remote processor"
        }
    }
}

class ResolveExecWrapper(private val wrappee: Processor) {
    fun process(instance: Cmd): String {
        val handler = wrappee.resolveHandler(instance)
        return if (null !== handler) {
            wrappee.execHandler(handler)
        } else {
            // println("Call remote processor")
            "from remote processor"
        }
    }
}

class SmallListProcessor : Processor {
    override fun canProcess(instance: Cmd): Boolean {
        return when (instance) {
            is A,
            is B,
            is C,
            is D,
            is E -> true
            else -> false
        }
    }

    override fun process(instance: Cmd): String {
        return when (instance) {
            is A -> "a"
            is B -> "b"
            is C -> "c"
            is D -> "d"
            is E -> "e"
            else -> throw NotFoundException(instance)
        }
    }

    override fun resolveHandler(instance: Cmd): String? {
        return when (instance) {
            is A -> "a"
            is B -> "b"
            is C -> "c"
            is D -> "d"
            is E -> "e"
            else -> null
        }
    }

    override fun execHandler(handler: String): String {
        return handler
    }
}

class MediumListProcessor : Processor {
    override fun canProcess(instance: Cmd): Boolean {
        return when (instance) {
            is A,
            is B,
            is C,
            is D,
            is E,
            is F,
            is G,
            is H,
            is I,
            is J,
            is K,
            is L,
            is M,
            is N -> true
            else -> false
        }
    }

    override fun process(instance: Cmd): String {
        return when (instance) {
            is A -> "a"
            is B -> "b"
            is C -> "c"
            is D -> "d"
            is E -> "e"
            is F -> "f"
            is G -> "g"
            is H -> "h"
            is I -> "i"
            is J -> "j"
            is K -> "k"
            is L -> "l"
            is M -> "m"
            is N -> "n"
            else -> throw NotFoundException(instance)
        }
    }

    override fun resolveHandler(instance: Cmd): String? {
        return when (instance) {
            is A -> "a"
            is B -> "b"
            is C -> "c"
            is D -> "d"
            is E -> "e"
            is F -> "f"
            is G -> "g"
            is H -> "h"
            is I -> "i"
            is J -> "j"
            is K -> "k"
            is L -> "l"
            is M -> "m"
            is N -> "n"
            else -> null
        }
    }

    override fun execHandler(handler: String): String {
        return handler
    }
}



