package net.ntworld.foundation

class HandlerVersioningStrategy private constructor(
    private val strategy: Strategy,
    private val version: Int?
) {
    private enum class Strategy {
        Skip,
        UseLatestVersion,
        UseSpecificVersion
    }

    val specificVersion: Int
        get() {
            if (Strategy.UseSpecificVersion == strategy) {
                return version!!
            }
            throw Exception("You cannot get specificVersion when decision is not UseSpecificVersion")
        }

    fun skip(): Boolean {
        return strategy == Strategy.Skip
    }

    fun useLatestVersion(): Boolean {
        return strategy == Strategy.UseLatestVersion
    }

    companion object {
        val skip = HandlerVersioningStrategy(Strategy.Skip, null)
        val useLatestVersion = HandlerVersioningStrategy(Strategy.UseLatestVersion, null)

        fun useSpecificVersion(version: Int): HandlerVersioningStrategy {
            return HandlerVersioningStrategy(Strategy.UseSpecificVersion, version)
        }
    }
}
