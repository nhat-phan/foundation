package net.ntworld.foundation

interface Environment {
    val name: String

    val allowAnonymization: Boolean

    object Development: Environment {
        override val name: String = "development"

        override val allowAnonymization: Boolean = true
    }

    object Test: Environment {
        override val name: String = "test"

        override val allowAnonymization: Boolean = true
    }

    object Stage: Environment {
        override val name: String = "stage"

        override val allowAnonymization: Boolean = false
    }

    object Production: Environment {
        override val name: String = "production"

        override val allowAnonymization: Boolean = false
    }
}
