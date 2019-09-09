import kotlin.test.Test

class ContractTest {
    @Test
    fun testFakedAnnotations() {
        // readSettings()
    }

    private fun readSettings() {
        val tmp = this::class.java.getResource("/foundation-settings.json").readText()
        println(tmp)
    }
}