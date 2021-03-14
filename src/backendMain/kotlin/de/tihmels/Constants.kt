package de.tihmels

object Constants {

    class CSP private constructor() {
        companion object {
            const val SPEED_MIN = 1
            const val SPEED_MAX = 10
            const val DELAY_MIN = 80
            const val DELAY_MAX = 1000
        }
    }

    class Resource private constructor() {
        companion object {
            const val FOLDER = "testPuzzles/"
        }
    }

}
