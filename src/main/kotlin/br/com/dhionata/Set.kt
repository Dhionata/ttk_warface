package br.com.dhionata

data class Set(
    val name: String,
    val armor: Double,
    val bodyProtection: Double, // Body Protection%
    val headProtection: Double, // Head Protection%
    val armProtection: Double, // Arm Protection%
    val legProtection: Double,  // Leg Protection%
    val absorption: Double,    // Absorption (flat value)
    val resistance: Double,    // Resistance (percentage)
    val weaponTypeResistance: Double, // Type Resistance (percentage)
    val cyborgDmgBuff: Double = 0.0,  // Bonus contra Cyborgs
    val entityDmgMult: Double = 1.0,  // Multiplicador contra entidades específicas
    val hp: Double = 125.0,             // HP inicial
) {

    companion object SetsAndEnemy {

        val Sirocco: Set = Set(
            "Tempestade de Areia",
            500.0,
            0.15,
            0.30,
            0.25,
            0.25,
            27.0,
            0.0,
            0.0
        )

        val Nord: Set = Set(
            "Nórdico",
            340.0,
            0.0,
            0.6,
            0.25,
            0.25,
            22.0,
            0.0,
            0.0
        )

        val `Assault, CQB & Sniper`: Set = Set(
            "Assault (CO-OP) | CQB (CO-OP) | Sniper (CO-OP)",
            280.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 500.0
        )

        val Demoman: Set = Set(
            "Demoman (CO-OP)",
            350.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 625.0
        )

        val `Spec-Ops`: Set = Set(
            "Spec-Ops (CO-OP)",
            420.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 750.0
        )

        val `Heavy Gunner`: Set = Set(
            "Heavy Gunner (CO-OP)",
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 20000.0
        )

        val `G15 Pteranodon`: Set = Set(
            "G15 Pteranodon",
            .0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 8400.0
        )

        val `SWAT Heavy Gunner`: Set = Set(
            "SWAT Heavy Gunner",
            400.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 12000.0,
        )

        val `Alpha, Beta e Omega`: Set = Set(
            "Alpha, Beta e Omega",
            300.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 300.0
        )

        val Screamer: Set = Set(
            "Screamer",
            1680.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 3000.0
        )

        val `Fast Hybrid`: Set = Set(
            "Fast Hybrid",
            0.0,
            -70.0,
            0.0,
            0.0,
            0.0,
            0.0,
            55.5,
            0.0,
            hp = 123750.0
        )
    }
}
