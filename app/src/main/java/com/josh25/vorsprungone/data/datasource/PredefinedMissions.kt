package com.josh25.vorsprungone.data.datasource

val predefinedMissions = listOf(
    // 0: Downstairs 8X8
    """
    {
        "topRightCorner": { "x": 8, "y": 8 },
        "roverPosition": { "x": 0, "y": 8 },
        "roverDirection": "E",
        "movements": "MRMLMRMLMRMLMRMLMRMLMRMLMRMRMMMMMM"
    }
    """.trimIndent(),

    // 1: Horizontal Spiral 6x3
    """
    {
        "topRightCorner": { "x": 6, "y": 3 },
        "roverPosition": { "x": 0, "y": 0 },
        "roverDirection": "E",
        "movements": "MMMMMMLMMMLMMMMMMLMMLMMMMMLMLMMMMRRRR"
    }
    """.trimIndent(),

    // 2: Letter "J"
    """
    {
        "topRightCorner": { "x": 6, "y": 6 },
        "roverPosition": { "x": 1, "y": 2 },
        "roverDirection": "S",
        "movements": "MLMMMLMMMMLMMRRMMM"
    }
    """.trimIndent(),

    // 3: Letter "T"
    """
    {
        "topRightCorner": { "x": 8, "y": 8 },
        "roverPosition": { "x": 1, "y": 7 },
        "roverDirection": "E",
        "movements": "MMMMMMRRMMMLMMMMMM"
    }
    """.trimIndent(),

    // 4: Spiral inward 7X7
    """
    {
        "topRightCorner": { "x": 7, "y": 7 },
        "roverPosition": { "x": 6, "y": 6 },
        "roverDirection": "S",
        "movements": "LLLMMMMMLMMMMMLMMMMMLMMMMLMMMMLMMMLMMMLMMLMMLMLMRR"
    }
    """.trimIndent(),

    // 5: Corridor In And Out
    """
    {
        "topRightCorner": { "x": 9, "y": 3 },
        "roverPosition": { "x": 1, "y": 1 },
        "roverDirection": "W",
        "movements": "RRMMMMMMMLMLMMMMMMMMM"
    }
    """.trimIndent(),

    // 6: Into the Sunset
    """
    {
        "topRightCorner": { "x": 2, "y": 16 },
        "roverPosition": { "x": 1, "y": 0 },
        "roverDirection": "E",
        "movements": "LMMMMMMMMMMMMMMMM"
    }
    """.trimIndent(),


    // 7: Snake path
    """
    {
        "topRightCorner": { "x": 12, "y": 4 },
        "roverPosition": { "x": 1, "y": 1 },
        "roverDirection": "E",
        "movements": "LMMRMRMMLMLMMRMRMMLMLMMRMRMMLMLMRMMMM"
    }
    """.trimIndent(),


    // 8: Tiny 4x4
    """
    {
        "topRightCorner": { "x": 3, "y": 3 },
        "roverPosition": { "x": 0, "y": 0 },
        "roverDirection": "E",
        "movements": "MMLMMLMMLMM"
    }
    """.trimIndent(),
/*
    // Grid cross
    """
    {
        "topRightCorner": { "x": 7, "y": 7 },
        "roverPosition": { "x": 3, "y": 0 },
        "roverDirection": "N",
        "movements": "MMMMMMMLLLLLMMMMMM"
    }
    """.trimIndent(),

    // Letter "A"
    """
    {
        "topRightCorner": { "x": 8, "y": 8 },
        "roverPosition": { "x": 4, "y": 0 },
        "roverDirection": "N",
        "movements": "MMMMMRMLMRMRMMMM"
    }
    """.trimIndent(),

    // Zigzag down
    """
    {
        "topRightCorner": { "x": 6, "y": 6 },
        "roverPosition": { "x": 0, "y": 6 },
        "roverDirection": "E",
        "movements": "MMMMMRMMMLMMMMMRMMMLMM"
    }
    """.trimIndent(),

    // Circle-ish spiral
    """
    {
        "topRightCorner": { "x": 10, "y": 10 },
        "roverPosition": { "x": 5, "y": 5 },
        "roverDirection": "N",
        "movements": "MMMMRMMMMLMMMMRMMMMLMMRMM"
    }
    """.trimIndent(),

    // Sweeping "V"
    """
    {
        "topRightCorner": { "x": 9, "y": 9 },
        "roverPosition": { "x": 0, "y": 9 },
        "roverDirection": "S",
        "movements": "MMRMMRMMRMM"
    }
    """.trimIndent(),

    // Letter "I"
    """
    {
        "topRightCorner": { "x": 5, "y": 8 },
        "roverPosition": { "x": 2, "y": 0 },
        "roverDirection": "N",
        "movements": "MMMMMMM"
    }
    """.trimIndent(),

    // Letter "H"
    """
    {
        "topRightCorner": { "x": 5, "y": 8 },
        "roverPosition": { "x": 0, "y": 0 },
        "roverDirection": "N",
        "movements": "MMMMMMMRMMMMMLLLLLMMMMMM"
    }
    """.trimIndent(),

    // Grid outline
    """
    {
        "topRightCorner": { "x": 4, "y": 4 },
        "roverPosition": { "x": 0, "y": 0 },
        "roverDirection": "E",
        "movements": "MMMMMRMMMMMRMMMMMRMMMMM"
    }
    """.trimIndent(),

    // Complex snake
    """
    {
        "topRightCorner": { "x": 10, "y": 5 },
        "roverPosition": { "x": 0, "y": 0 },
        "roverDirection": "E",
        "movements": "MMMMMMMMMRMMMMLMMMMMRMMMMLMMMMMR"
    }
    """.trimIndent(),

    // Maze pattern
    """
    {
        "topRightCorner": { "x": 6, "y": 6 },
        "roverPosition": { "x": 0, "y": 0 },
        "roverDirection": "E",
        "movements": "MMMRMMMLMMMRMMMLMMMRMMM"
    }
    """.trimIndent(),

    // Diamond shape
    """
    {
        "topRightCorner": { "x": 8, "y": 8 },
        "roverPosition": { "x": 4, "y": 0 },
        "roverDirection": "N",
        "movements": "MMMMRMMMLMMMRMMMLMMM"
    }
    """.trimIndent()

     */
)
