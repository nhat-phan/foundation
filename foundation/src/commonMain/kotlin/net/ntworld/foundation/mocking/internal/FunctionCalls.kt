package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.ParameterList

internal class FunctionCalls {
    private val calledParams: MutableList<ParameterList> = mutableListOf()

    fun reset() {
        calledParams.clear()
    }

    fun count(): Int {
        return calledParams.size
    }

    private fun add(params: List<Any?>): ParameterList {
        val list = ParameterList(params)
        calledParams += list

        return list
    }

    fun <R> callFake(params: List<Any?>, fake: (ParameterList) -> R): R {
        return fake.invoke(this.add(params))
    }

    fun <R> callFake(params: List<Any?>, fake: (ParameterList, InvokeData) -> R): R {
        val size = calledParams.size
        val list = this.add(params)
        return fake.invoke(list, InvokeData(size + 1))
    }

    fun <R> returnResult(params: List<Any?>, result: R): R {
        this.add(params)
        return result
    }

    fun verify(block: (ParameterList) -> Boolean): Boolean {
        for (list in calledParams) {
            if (!block.invoke(list)) {
                return false
            }
        }
        return true
    }

    fun verify(block: (ParameterList, InvokeData) -> Boolean): Boolean {
        for (index in 0 until count()) {
            if (!block.invoke(calledParams[index], InvokeData(index + 1))) {
                return false
            }
        }
        return true
    }

}