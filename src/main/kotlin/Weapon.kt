package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode

data class Weapon(
    val name: String,
    var damage: Int,
    var fireRate: Int, // DPM (disparos por minuto)
    var headMultiplier: Double,
    var bodyMultiplier: Double,
    val ttk: MutableMap<String, Double> = mutableMapOf(),
) {

    var set: Set = Set.sirocco
        set(value) {
            field = value
            updateTTK()
        }

    init {
        updateTTK()
    }

    override fun toString(): String {
        return "Nome: $name | Dano: $damage | Cadência: $fireRate | Cabeça X $headMultiplier | Corpo X $bodyMultiplier | TTK - Tiros=Tempo(s): $ttk"
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

        updateTTK()

        return this
    }

    /**
     * Função privada para atualizar o mapa ttk com base nas propriedades atuais da arma.
     */
    private fun updateTTK(set: Set = this@Weapon.set) {
        ttk.clear()

        val headMap = TTKCalculator.calculateTTKWithProtection(this, set, true)
        ttk["Cabeça: ${headMap.first}"] = headMap.second

        val bodyMap = TTKCalculator.calculateTTKWithProtection(this, set, false)
        ttk["Corpo: ${bodyMap.first}"] = bodyMap.second

        val ttkAverage = (headMap.second + bodyMap.second) / 2
        ttk["Média: ${((headMap.first + bodyMap.first) / 2)}"] = ttkAverage

    }

    companion object WeaponsLists {
        val fuzileiroWeapons: List<Weapon> = listOf(
            Weapon("AK Alpha (Mod Cadência)", 100, 800, 6.5, 1.0),
            Weapon("AK Alpha (Mod Cadência e Dano)", 100, 800, 6.5, 1.0).addMods(6.0, 4),
            Weapon("AK Alpha (Mod RAJADA)", 100, 800, 6.5, 1.0).addMods(-35.0, 50),
            Weapon("AK Alpha (Mod RAJADA, Cadência e Dano)", 100, 800, 6.5, 1.0).addMods(-35.0, 50).addMods(6.0).addMods(damageAdd = 4),
            Weapon("AK-12 (Mod Cadência)", 105, 735, 7.0, 1.25).addMods(10.0),
            Weapon("Beretta (Mod Cadência)", 111, 810, 4.0, 1.4).addMods(10.0),
            Weapon("Carmel (Mod Dano)", 96, 720, 7.0, 1.07).addMods(-27.5, 70, bodyMultiplierAddPercentage = 12.0),
            Weapon("Cobalt (Mod Cadência [normal], +2 Corporal)", 95, 735, 7.0, 1.05).addMods(5.2).addMods(bodyMultiplierAddPercentage = 10.0)
                .addMods(bodyMultiplierAddPercentage = 40.0),
            Weapon("Cobalt (Mod Cadência [especial] e Corporal", 95, 735, 7.0, 1.05).addMods(40.0, headMultiplierAddPercentage = -32.0).addMods(5.2).addMods
                (bodyMultiplierAddPercentage = 10.0),
            Weapon("Kord", 175, 640, 6.0, 1.15),
            Weapon("Kord (Mod Cadência)", 175, 640, 6.0, 1.15).addMods(10.0),
            Weapon("Kord (Mod Cadência e Recuo)", 175, 640, 6.0, 1.15).addMods(10.0).addMods(-26.0),
            Weapon("PKM Zenit (Mod Cadência e Cadência [especial]", 145, 440, 5.5, 1.0).addMods(6.0).addMods(70.0, -40),
            Weapon("PKM Zenit (Mod Cadência)", 145, 440, 5.5, 1.0).addMods(6.0),
            Weapon("QBZ (Mod Cadência)", 106, 720, 7.0, 1.12).addMods(8.0),
            Weapon("STK (Mod Cadência e Corporal)", 110, 840, 4.0, 1.25).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
            Weapon("STK (Mod Cadência, Corporal e Dano)", 110, 840, 4.0, 1.25).addMods(8.0).addMods(bodyMultiplierAddPercentage = 13.0)
                .addMods(-42.0, 60, bodyMultiplierAddPercentage = -10.0),
            Weapon("FN SCAR-H (Mod Cadência)", 175, 600, 7.0, 1.24).addMods(10.0),
            Weapon("AN-94 (Mod Cadência)", 125, 620, 7.0, 1.8).addMods(10.0),
            Weapon("MPAR-556 (Mod Cadência)", 110, 825, 4.0, 1.45).addMods(10.0)
        ).sortedBy { it.ttk.values.elementAt(1) }

        val engenheiroWeapons: List<Weapon> = listOf(
            Weapon("Tavor CTAR-21 (Mod Cadência)", 102, 970, 4.0, 1.6).addMods(10.0),
            Weapon("Honey Badger (Mod Cadência)", 128, 785, 6.0, 1.24).addMods(7.0),
            Weapon("Kriss Super V Custom (Mod Cadência, Dano e Dano na Cabeça)", 100, 800, 4.5, 1.1).addMods(5.0, 9).addMods(damageAdd = 16, headMultiplierAddPercentage = 20.0),
            Weapon("Kriss Super V Custom (Mod Cadência, Dano e Cadência [especial])", 100, 800, 4.5, 1.1).addMods(5.0).addMods(40.0).addMods(damageAdd = 9),
            Weapon("Magpul (Mod Cadência)", 100, 1010, 4.0, 1.42).addMods(8.0),
            Weapon("Magpul (Mod Dano Corporal)", 100, 1010, 4.0, 1.42).addMods(bodyMultiplierAddPercentage = 13.0),
            Weapon("Magpul (Ambas Modificações)", 100, 1010, 4.0, 1.42).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
            Weapon("PP-2011 (Mod Cadência)", 120, 790, 5.8, 1.12).addMods(6.8),
            Weapon("PP-2011 (Mod Dano Corporal)", 120, 790, 5.8, 1.21).addMods(bodyMultiplierAddPercentage = 8.0),
            Weapon("PP-2011 (Mod Cadência e Dano Corporal)", 120, 790, 5.8, 1.21).addMods(6.8, bodyMultiplierAddPercentage = 8.0),
            Weapon("CSV-9 Comodo (Mod Dano e Dano Corporal)", 92, 980, 4.8, 1.2).addMods(-13.0, 29, 25.0).addMods(bodyMultiplierAddPercentage = 8.0),
            Weapon("Famae SAF-200 (Mod Cadência)", 125, 790, 6.0, 1.3).addMods(fireRateAddPercentage = 10.0),
            Weapon("CZ Scorpion (Mod Cadência)", 128, 775, 6.0, 1.28).addMods(10.0)
        ).sortedBy { it.ttk.values.elementAt(1) }

        val pistolas: List<Weapon> = listOf(
            Weapon("Taurus Raging Hunter (Mod Cadência)", 350, 160, 6.0, 1.10).addMods(5.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal, Precisão e Cadência)", 350, 160, 6.0, 1.10).addMods(5.0).addMods(bodyMultiplierAddPercentage = 10.0)
                .addMods(-10.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal e Cadência)", 350, 160, 6.0, 1.10).addMods(5.0).addMods(bodyMultiplierAddPercentage = 10.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal, Cadência e Cadência [especial])", 350, 160, 6.0, 1.10).addMods(5.0)
                .addMods(bodyMultiplierAddPercentage = 10.0)
                .addMods(55.0, -90),
            Weapon("SIG Sauer P226 (Mod Cadência e Dano)", 155, 275, 4.0, 1.05).addMods(8.0).addMods(damageAdd = 26),
            Weapon("SIG Sauer P226 (Mod Cadência, Dano e Precisão)", 155, 275, 4.0, 1.05).addMods(8.0).addMods(damageAdd = 26).addMods(-30.0, 80),
            Weapon("Maxim 9", 130, 290, 3.5, 1.15),
            Weapon("ST Kinetics (Mod Cadência e Corporal", 108, 880, 5.0, 1.1).addMods(12.0).addMods(bodyMultiplierAddPercentage = 16.0),
            Weapon("ST Kinetics (Mod Cadência, Corporal e Dupla", 108, 880, 5.0, 1.1).addMods(12.0).addMods(bodyMultiplierAddPercentage = 16.0).addMods(7.0, -15, -20.0).addMods
                (100.0),
            Weapon("Taurus Judge (Mod Cadência)", 680, 100, 3.0, 1.4).addMods(10.0)
        ).sortedBy { it.ttk.values.elementAt(1) }
    }
}
