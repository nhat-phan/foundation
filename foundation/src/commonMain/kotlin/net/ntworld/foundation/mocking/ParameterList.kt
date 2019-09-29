package net.ntworld.foundation.mocking

class ParameterList(private val params: List<Any?>) {
    val size = params.size
    operator fun get(index: Int) = params[index]

    operator fun component1() = params[0]
    operator fun component2() = params[1]
    operator fun component3() = params[2]
    operator fun component4() = params[3]
    operator fun component5() = params[4]
    operator fun component6() = params[5]
    operator fun component7() = params[6]
    operator fun component8() = params[7]
    operator fun component9() = params[8]
    operator fun component10() = params[9]
    operator fun component11() = params[10]
    operator fun component12() = params[11]
    operator fun component13() = params[12]
    operator fun component14() = params[13]
    operator fun component15() = params[14]
    operator fun component16() = params[15]
    operator fun component17() = params[16]
    operator fun component18() = params[17]
    operator fun component19() = params[18]
    operator fun component20() = params[19]
}