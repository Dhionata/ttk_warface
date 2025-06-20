package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

data class Weapon(
    val name: String,
    var damage: Int,
    private var _fireRate: Double, // valor interno para cálculos precisos
    var headMultiplier: Double,
    var bodyMultiplier: Double,
    val ttk: MutableList<Pair<Int, Double>> = mutableListOf(),
) {

    var set: Set = Set.sirocco
        set(value) {
            field = value
            updateTTK()
        }

    val fireRate: Int
        get() = _fireRate.roundToInt()

    override fun toString(): String {
        return "Nome: $name | Dano: $damage | Cadência: $fireRate | Cabeça X $headMultiplier | Corpo X ${
            BigDecimal(bodyMultiplier).setScale(
                2, RoundingMode.HALF_UP
            )
        } | TTK[Tiro(s) em Tempo(s)]: Cabeça[${ttk.first().first} em ${
            BigDecimal(ttk.first().second).setScale(
                3, RoundingMode.HALF_UP
            )
        }], Corpo[${ttk.elementAt(1).first} em ${
            BigDecimal(ttk.elementAt(1).second).setScale(
                3, RoundingMode.HALF_UP
            )
        }], Média[${ttk.last().first} em ${BigDecimal(ttk.last().second).setScale(3, RoundingMode.HALF_UP)}]"
    }

    /**
     * Função para adicionar modificações à arma.
     * Após as modificações, o `map` ttk é atualizado.
     */
    fun addMods(
        fireRateAddPercentage: Double? = null,
        damageAdd: Int? = null,
        headMultiplierAddPercentage: Double? = null,
        bodyMultiplierAddPercentage: Double? = null,
    ): Weapon {
        if (fireRateAddPercentage != null) {
            _fireRate += _fireRate * fireRateAddPercentage / 100.0
        }
        if (damageAdd != null) {
            damage += damageAdd
        }
        if (bodyMultiplierAddPercentage != null) {
            bodyMultiplier += bodyMultiplier * bodyMultiplierAddPercentage / 100.0
        }
        if (headMultiplierAddPercentage != null) {
            headMultiplier += headMultiplier * headMultiplierAddPercentage / 100.0
        }

        updateTTK()

        return this
    }

    /**
     * Função privada para atualizar o mapa ttk com base nas propriedades atuais da arma.
     */
    private fun updateTTK() {
        ttk.clear()

        val headPair = TTKCalculator.calculateTTKWithProtectionInt(this, true)
        ttk.add(Pair(headPair.first, headPair.second))

        val bodyPair = TTKCalculator.calculateTTKWithProtectionInt(this, false)
        ttk.add(Pair(bodyPair.first, bodyPair.second))

        val ttkAverage = (headPair.second + bodyPair.second) / 2
        ttk.add(Pair(((headPair.first + bodyPair.first) / 2), ttkAverage))

    }

    companion object WeaponsLists {

        val fuzileiroWeapons: List<Weapon> = listOf(
            Weapon("AK Alpha", 100, 800.0, 6.5, 1.0),
            Weapon("AK Alpha (Mod Dano)", 100, 800.0, 6.5, 1.0).addMods(damageAdd = 6),
            Weapon("AK Alpha (Mod Cadência)", 100, 800.0, 6.5, 1.0).addMods(6.0),
            Weapon("AK Alpha (Mod Cadência e Dano)", 100, 800.0, 6.5, 1.0).addMods(6.0, 4),
            Weapon("AK Alpha (Mod RAJADA)", 100, 800.0, 6.5, 1.0).addMods(-35.0, 50),
            Weapon("AK Alpha (Mod RAJADA, Cadência e Dano)", 100, 800.0, 6.5, 1.0).addMods(-35.0, 50).addMods(6.0).addMods(damageAdd = 4),
            Weapon("AK-12 (Mod Cadência)", 105, 735.0, 7.0, 1.25).addMods(10.0),
            Weapon("Beretta (Mod Cadência)", 111, 810.0, 4.0, 1.4).addMods(10.0),
            Weapon("Carmel (Mod Dano, Cadência e Corporal)", 96, 720.0, 7.0, 1.07).addMods(-27.5, 70, bodyMultiplierAddPercentage = 12.0).addMods(6.0)
                .addMods(bodyMultiplierAddPercentage = 12.0),
            Weapon("Carmel (Mod Cadência [normal e especial] e Corporal)", 96, 720.0, 7.0, 1.07).addMods(45.0, headMultiplierAddPercentage = -45.0).addMods(6.0)
                .addMods(bodyMultiplierAddPercentage = 12.0),
            Weapon("Cobalt (Mod Cadência [normal], +2 Corporal)", 95, 735.0, 7.0, 1.05).addMods(5.2).addMods(bodyMultiplierAddPercentage = 10.0)
                .addMods(bodyMultiplierAddPercentage = 40.0),
            Weapon("Cobalt (Mod Cadência [especial] e Corporal)", 95, 735.0, 7.0, 1.05).addMods(40.0, headMultiplierAddPercentage = -32.0).addMods(5.2)
                .addMods(bodyMultiplierAddPercentage = 10.0),
            Weapon("Kord", 175, 640.0, 6.0, 1.15),
            Weapon("Kord (Mod Cadência)", 175, 640.0, 6.0, 1.15).addMods(10.0),
            Weapon("Kord (Mod Cadência e Recuo)", 175, 640.0, 6.0, 1.15).addMods(10.0).addMods(-26.0),
            Weapon("PKM Zenit (Mod Cadência [normal e especial])", 145, 440.0, 5.5, 1.0).addMods(6.0).addMods(70.0, -40),
            Weapon("PKM Zenit (Mod Cadência)", 145, 440.0, 5.5, 1.0).addMods(6.0),
            Weapon("PKM Zenit (Mod Cadência [especial e normal] e Corporal)", 145, 440.0, 5.5, 1.0).addMods(6.0).addMods(70.0, -40).addMods(bodyMultiplierAddPercentage = 6.0),
            Weapon("PKM Zenit (Mod Cadência e Corporal)", 145, 440.0, 5.5, 1.0).addMods(6.0).addMods(bodyMultiplierAddPercentage = 6.0),
            Weapon("QBZ (Mod Cadência e Corporal)", 106, 720.0, 7.0, 1.12).addMods(8.0).addMods(bodyMultiplierAddPercentage = 13.0),
            Weapon("STK (Mod Cadência e Corporal)", 110, 865.0, 4.0, 1.25).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
            Weapon("STK (Mod Cadência, Corporal e Dano)", 110, 865.0, 4.0, 1.25).addMods(8.0).addMods(bodyMultiplierAddPercentage = 13.0).addMods(
                -42.0, 60, bodyMultiplierAddPercentage = -10.0
            ),
            Weapon("FN SCAR-H (Mod Cadência)", 175, 540.0, 7.0, 1.24).addMods(10.0),
            Weapon("AN-94 (Mod Cadência)", 125, 700.0, 7.0, 1.8).addMods(10.0),
            Weapon("MPAR-556 (Mod Cadência)", 110, 850.0, 4.0, 1.45).addMods(10.0),
            Weapon("MPAR-556", 110, 850.0, 4.0, 1.45),
            Weapon("As-Val (Mod Cadência)", 105, 765.0, 7.0, 1.25).addMods(10.0),
            Weapon("A-545 (Mod Cadência)", 106, 735.0, 7.0, 1.18).addMods(10.0),
            Weapon("M16A3 Custom (Mod Cadência)", 108, 730.0, 7.0, 1.24).addMods(10.0),
        )

        val engenheiroWeapons: List<Weapon> = listOf(
            Weapon("Tavor CTAR-21 (Mod Cadência)", 102, 970.0, 4.0, 1.6).addMods(10.0),
            Weapon("Honey Badger (Mod Cadência)", 128, 785.0, 6.0, 1.24).addMods(7.0),
            Weapon("Kriss Super V Custom (Mod Cadência, Dano e Dano na Cabeça)", 100, 800.0, 4.5, 1.1).addMods(5.0, 9).addMods(damageAdd = 16, headMultiplierAddPercentage = 20.0),
            Weapon("Kriss Super V Custom (Mod Cadência, Dano e Cadência [especial])", 100, 800.0, 4.5, 1.1).addMods(5.0).addMods(40.0).addMods(damageAdd = 9),
            Weapon("Magpul (Mod Cadência)", 100, 1010.0, 4.0, 1.42).addMods(8.0),
            Weapon("Magpul (Mod Dano Corporal)", 100, 1010.0, 4.0, 1.42).addMods(bodyMultiplierAddPercentage = 13.0),
            Weapon("Magpul (Ambas Modificações)", 100, 1010.0, 4.0, 1.42).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
            Weapon("PP-2011 (Mod Cadência)", 125, 790.0, 6.0, 1.25).addMods(6.8),
            Weapon("PP-2011 (Mod Dano Corporal)", 125, 790.0, 6.0, 1.25).addMods(bodyMultiplierAddPercentage = 4.0),
            Weapon("PP-2011 (Mod Cadência e Dano Corporal)", 125, 790.0, 6.0, 1.25).addMods(6.8, bodyMultiplierAddPercentage = 4.0),
            Weapon("CSV-9 Comodo (Mod Dano e Dano Corporal)", 92, 980.0, 4.8, 1.2).addMods(-13.0, 29, 25.0).addMods(bodyMultiplierAddPercentage = 8.0),
            Weapon("Famae SAF-200 (Mod Cadência)", 125, 750.0, 6.0, 1.3).addMods(10.0),
            Weapon("CZ Scorpion (Mod Cadência)", 128, 740.0, 6.0, 1.28).addMods(10.0),
            Weapon("SR-3M (Mod Cadência)", 100, 985.0, 4.0, 1.6).addMods(10.0),
            Weapon("AMB-17 (Mod Cadência)", 125, 745.0, 6.0, 1.3).addMods(10.0),
            Weapon("Taurus CT9 G2 (Mod Cadência)", 100, 815.0, 6.2, 1.05).addMods(3.0),
            Weapon("PPSH-41 Modern (Mod Cadência)", 150, 625.0, 6.0, 1.45).addMods(10.0),
            Weapon("Scar-L PDW (Mod cadência)", 150, 630.0, 5.3, 1.45).addMods(10.0),
        )

        val pistolas: List<Weapon> = listOf(
            Weapon("Taurus Raging Hunter (Mod Cadência)", 350, 160.0, 6.0, 1.10).addMods(5.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal, Precisão e Cadência)", 350, 160.0, 6.0, 1.10).addMods(5.0).addMods(bodyMultiplierAddPercentage = 10.0).addMods(-10.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal e Cadência)", 350, 160.0, 6.0, 1.10).addMods(5.0).addMods(bodyMultiplierAddPercentage = 10.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal, Cadência e Cadência [especial])", 350, 160.0, 6.0, 1.10).addMods(5.0).addMods(bodyMultiplierAddPercentage = 10.0)
                .addMods(55.0, -90),
            Weapon("SIG Sauer P226 (Mod Cadência e Dano)", 200, 275.0, 4.0, 1.3).addMods(8.0).addMods(damageAdd = 26),
            Weapon("SIG Sauer P226 (Mod Cadência, Dano e Precisão)", 200, 275.0, 4.0, 1.3).addMods(8.0).addMods(damageAdd = 26).addMods(-30.0, 80),
            Weapon("Maxim 9", 130, 290.0, 3.5, 1.15),
            Weapon("ST Kinetics (Mod Cadência e Corporal)", 108, 950.0, 5.0, 1.1).addMods(12.0).addMods(bodyMultiplierAddPercentage = 16.0),
            Weapon("ST Kinetics (Mod Cadência, Corporal e Dupla)", 108, 950.0, 5.0, 1.1).addMods(12.0).addMods(bodyMultiplierAddPercentage = 16.0).addMods(7.0, -15, -20.0)
                .addMods(100.0),
            Weapon("Taurus Judge (Mod Cadência)", 680, 100.0, 3.0, 1.4).addMods(10.0),
            Weapon("Mauser (Mod Dupla, Cadência e Dano)", 175, 400.0, 3.8, 1.05).addMods(30.0, -20).addMods(100.0).addMods(5.0).addMods(damageAdd = 20),
            Weapon("Deset Eagle (Mod Cadência)", 275, 270.0, 4.25, 1.35).addMods(20.0),
            Weapon("M1911A1 (Mod Dano e Cadência [especial], Cadência e Dano [especial])", 200, 290.0, 6.0, 1.1).addMods(20.0, 30).addMods(5.0).addMods(damageAdd = 20),
            Weapon("M1911A (Mod Dano [especial], Cadência e Dano)", 200, 290.0, 6.0, 1.1).addMods(-50.0, 160).addMods(5.0).addMods(damageAdd = 20),
            Weapon("Glock 18c (Mod cadência)", 108, 800.0, 5.0, 1.25).addMods(10.0),
            Weapon("Makarov (Mod cadência, dano e dano [especial])", 135, 300.0, 4.0, 1.0).addMods(10.0).addMods(damageAdd = 15).addMods(-35.0, 35),
            Weapon("Makarov (Mod cadência, dano e cadência [especial]", 135, 300.0, 4.0, 1.0).addMods(10.0).addMods(damageAdd = 15).addMods(15.0)
        )
    }
}
