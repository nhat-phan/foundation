package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.MockingException
import net.ntworld.foundation.mocking.ParameterList

internal class CalledWithBuilderImpl :
    CalledWithBuilder.Action,
    CalledWithBuilder.Build,
    CalledWithBuilder.Calls,
    CalledWithBuilder.Chain,
    CalledWithBuilder.Start {
    private data class Match(
        val ordinal: Int,
        var matcher: ((ParameterList, InvokeData) -> Boolean)?
    ) {
        fun verify(list: ParameterList, invokeData: InvokeData): Boolean {
            if (null !== matcher) {
                return matcher!!.invoke(list, invokeData)
            }
            throw MockingException("Please provide verify lambda via .match()")
        }
    }

    private var calledAtLeast: Int = -1
    private var calledExact: Int = -1
    private val data = mutableMapOf<Int, Match>()
    private var currentOrdinal = 0

    override fun atLeast(count: Int): CalledWithBuilder.Calls {
        this.calledAtLeast = if (count < 0) -1 else count

        return this
    }

    override fun exact(count: Int): CalledWithBuilder.Calls {
        this.calledExact = if (count < 0) -1 else count

        return this
    }

    override fun alwaysMatch(verify: (ParameterList, InvokeData) -> Boolean) {
        val match = findMatch(GLOBAL_ORDINAL)
        match.matcher = verify
    }

    override fun otherwiseMatch(verify: (ParameterList, InvokeData) -> Boolean) = alwaysMatch(verify)

    override fun onCall(n: Int): CalledWithBuilder.Action {
        currentOrdinal = n

        return this
    }

    override fun match(verify: (ParameterList) -> Boolean): CalledWithBuilder.Chain {
        val match = findMatch(currentOrdinal)
        match.matcher = { list, _ -> verify(list) }

        return this
    }

    override fun getCalledAtLeast(): Int = this.calledAtLeast

    override fun getCalledCount(): Int = this.calledExact

    override fun toCalledWith(): ((ParameterList, InvokeData) -> Boolean)? {
        if (data.isEmpty()) {
            return null
        }

        return { list, invokedData ->
            val match = data[invokedData.callIndex]
            if (null !== match) {
                match.verify(list, invokedData)
            } else {
                val global = data[GLOBAL_ORDINAL]
                if (null !== global) {
                    global.verify(list, invokedData)
                } else {
                    true
                }
            }
        }
    }

    private fun findMatch(ordinal: Int): Match {
        val item = data[ordinal]
        if (item === null) {
            val new = Match(ordinal, null)
            data[ordinal] = new
            return new
        }
        return item
    }

    companion object {
        const val GLOBAL_ORDINAL = -1
    }
}