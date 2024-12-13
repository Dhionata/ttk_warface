package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode

data class Weapon(
    val name: String,
    var damage: Int,
    var fireRate: Int, // DPM (disparos por minuto)
    var headMultiplier: Double,
    var bodyMultiplier: Double,
    val ttk: MutableMap<Int, Double> = mutableMapOf(),
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
        bodyMultiplierAddPercentage: Double? = null,
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

        bodyMultiplier = BigDecimal(bodyMultiplier).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        headMultiplier = BigDecimal(headMultiplier).setScale(2, RoundingMode.HALF_EVEN).toDouble()

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

    companion object WeaponsLists {
        val fuzileiroWeapons: List<Weapon> = listOf(
            Weapon("AK Alpha", 100, 800, 6.5, 1.0).addMods(6.0, 4),
            Weapon("AK Alpha RAJADA", 100, 800, 6.5, 1.0).addMods(-35.0, 50),
            Weapon("AK Alpha RAJADA", 100, 800, 6.5, 1.0).addMods(-35.0, 50).addMods(6.0, 4),
            Weapon("AK-12 (Mod Cadência)", 105, 735, 7.0, 1.25).addMods(10.0),
            Weapon("Beretta", 111, 810, 4.0, 1.4).addMods(10.0),
            Weapon("Carmel Modificada", 96, 720, 7.0, 1.07).addMods(-27.5, 70, null, 12.0),
            Weapon("Cobalt (Mod Cadência [normal], +2 Corporal)", 95, 735, 7.0, 1.05).addMods(5.2)
                .addMods(bodyMultiplierAddPercentage = 10.0)
                .addMods(bodyMultiplierAddPercentage = 40.0),
            Weapon("Kord", 175, 640, 6.0, 1.15).addMods(10.0),
            Weapon("Kord (Mod Recuo)", 175, 640, 6.0, 1.15).addMods(10.0).addMods(-26.0),
            Weapon("PKM Zenit", 105, 793, 5.5, 1.06),
            Weapon("QBZ", 106, 720, 7.0, 1.12).addMods(8.0),
            Weapon("STK (Mod Cadência e Corporal)", 110, 840, 4.0, 1.25).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
            Weapon("STK (Mod Cadência, Corporal e Dano)", 110, 840, 4.0, 1.25).addMods(8.0 - 42.0, 60, headMultiplierAddPercentage = 13.0 - 10.0),
            Weapon("FN SCAR-H", 175, 600, 7.0, 1.24),
            Weapon("FN SCAR-H (Mod Cadência)", 175, 600, 7.0, 1.24).addMods(10.0)
        ).sortedBy { it.ttk.values.last() }

        val engenheiroWeapons: List<Weapon> = listOf(
            Weapon("Tavor CTAR-21", 102, 970, 4.0, 1.6).addMods(10.0),
            Weapon("Honey Badger", 128, 785, 6.0, 1.24).addMods(7.0),
            Weapon("Kriss Super V Custom (Mod)", 100, 740, 4.5, 1.1).addMods(5.0, 9).addMods(40.0),
            Weapon("Kriss Super V Custom (Mod)", 100, 740, 4.5, 1.1).addMods(5.0, 16, 20.0).addMods(40.0),
            Weapon("Magpul", 100, 1010, 4.0, 1.42),
            Weapon("Magpul (Mod Cadência)", 100, 1010, 4.0, 1.42).addMods(8.0),
            Weapon("Magpul (Mod Dano Corporal)", 100, 1010, 4.0, 1.42).addMods(bodyMultiplierAddPercentage = 13.0),
            Weapon("Magpul (Ambas Modificações)", 100, 1010, 4.0, 1.42).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
            Weapon("PP-2011", 120, 790, 5.8, 1.12),
            Weapon("PP-2011 (Mod Cadência)", 120, 790, 5.8, 1.12).addMods(6.8),
            Weapon("PP-2011 (Mod Dano Corporal)", 120, 790, 5.8, 1.21).addMods(bodyMultiplierAddPercentage = 8.0),
            Weapon("PP-2011 (Ambas Modificações)", 120, 790, 5.8, 1.21).addMods(6.8, bodyMultiplierAddPercentage = 8.0),
            Weapon("CSV-9 Comodo", 92, 980, 4.8, 1.2).addMods(-13.0, 29, 25.0).addMods(bodyMultiplierAddPercentage = 8.0),
            Weapon("Famae SAF-200 (Mod Cadência)", 125, 790, 6.0, 1.3).addMods(fireRateAddPercentage = 10.0)
        ).sortedBy { it.ttk.values.last() }
    }
}
