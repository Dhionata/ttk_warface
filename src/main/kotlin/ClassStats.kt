package br.com.dhionata

// Classe para armazenar estatísticas de cada classe
data class ClassStats(
    val name: String,
    val armor: Double,
    val bodyProtection: Double, // Body Protection%
    val headProtection: Double, // Head Protection%
    val legProtection: Double,  // Leg Protection%
    val absorption: Double,    // Absorption (flat value)
    val resistance: Double,    // Resistance (percentage)
    val weaponTypeResistance: Double, // Weapon Type Resistance (percentage)
    val cyborgDmgBuff: Double = 0.0,  // Bonus contra Cyborgs
    val entityDmgMult: Double = 1.0,  // Multiplicador contra entidades específicas
    val hp: Double = 125.0             // HP inicial
) {

    companion object Fuzileiro {
        val FuzileiroStats = ClassStats(
            "Fuzileiro",
            500.0,
            0.15,
            0.25,
            0.25,
            15.0,
            0.0,
            0.0
        )
    }
}
