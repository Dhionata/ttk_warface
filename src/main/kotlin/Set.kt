package br.com.dhionata

data class Set(
    val name: String,
    val armor: Int,
    val bodyProtection: Double, // Body Protection%
    val headProtection: Double, // Head Protection%
    val armProtection: Double, // Arm Protection%
    val legProtection: Double,  // Leg Protection%
    val absorption: Double,    // Absorption (flat value)
    val resistance: Double,    // Resistance (percentage)
    val weaponTypeResistance: Double, // Weapon Type Resistance (percentage)
    val cyborgDmgBuff: Double = 0.0,  // Bonus contra Cyborgs
    val entityDmgMult: Double = 1.0,  // Multiplicador contra entidades específicas
    val hp: Int = 125,             // HP inicial
) {

    companion object Fuzileiro {

        val sirocco: Set = Set(
            "Tempestade de Areia",
            500,
            0.15,
            0.25,
            0.25,
            0.25,
            15.0,
            0.0,
            0.0
        )

        val nord: Set = Set(
            "Nórdico",
            340,
            0.0,
            0.6,
            0.25,
            0.25,
            22.0,
            0.0,
            0.0
        )

        val `Assault (CO-OP), CQB (CO-OP) & Sniper (CO-OP)`: Set = Set(
            "Assault (CO-OP) | CQB (CO-OP) | Sniper (CO-OP)",
            280,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 500
        )

        val `Demoman (CO-OP)`: Set = Set(
            "Demoman (CO-OP)",
            350,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 625
        )

        val `Spec-Ops (CO-OP)`: Set = Set(
            "Spec-Ops (CO-OP)",
            420,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 750
        )

        val `Heavy Gunner (CO-OP)`: Set = Set(
            "Heavy Gunner (CO-OP)",
            0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 20000
        )

        val `G15 Pteranodon`: Set = Set(
            "G15 Pteranodon",
            0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 8400
        )

        val `SWAT Heavy Gunner`: Set = Set(
            "SWAT Heavy Gunner",
            400,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            hp = 12000,
        )
    }
}
