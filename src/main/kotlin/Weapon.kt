package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode

data class Weapon(
    val name: String,
    var damage: Int,
    var fireRate: Int, // DPM (disparos por minuto)
    var headMultiplier: Double,
    var bodyMultiplier: Double,
    val ttk: MutableMap<Int, Double> = mutableMapOf()
) {

    init {
        updateTTK()
    }

    /**
     * Função para adicionar modificações à arma.
     * Após as modificações, o mapa ttk é atualizado.
     */
    fun addMods(
        fireRateAddPercentage: Double? = null,
        damageAdd: Int? = null,
        headMultiplierAddPercentage: Double? = null,
        bodyMultiplierAddPercentage: Double? = null
    ): Weapon {
        if (fireRateAddPercentage != null) {
            fireRate += BigDecimal(fireRate * fireRateAddPercentage / 100.0).setScale(0, RoundingMode.UP).toInt()
        }
        if (damageAdd != null) {
            damage += damageAdd
        }
        if (bodyMultiplierAddPercentage != null) {
            bodyMultiplier += (bodyMultiplier * bodyMultiplierAddPercentage / 100.0)
        }
        if (headMultiplierAddPercentage != null) {
            headMultiplier += (headMultiplier * headMultiplierAddPercentage / 100.0)
        }

        updateTTK()

        return this
    }

    /**
     * Função privada para atualizar o mapa ttk com base nas propriedades atuais da arma.
     */
    private fun updateTTK() {
        ttk.clear()

        val weaponCalculator = WeaponCalculator()

        val headMap = weaponCalculator.calculateTTKWithProtection(this, ClassStats.FuzileiroStats, true)
        ttk[headMap.first] = headMap.second

        val bodyMap = weaponCalculator.calculateTTKWithProtection(this, ClassStats.FuzileiroStats, false)
        ttk[bodyMap.first] = bodyMap.second

    }
}
