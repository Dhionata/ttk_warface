package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

data class Weapon(
    val name: String,
    var damage: Double,
    private var _fireRate: Double, // valor interno para cálculos precisos
    var headMultiplier: Double,
    var bodyMultiplier: Double,
    val ttk: MutableList<Pair<Int, Double>> = mutableListOf(),
    private val mods: MutableSet<String> = mutableSetOf(),
) {

    var set: Set = Set.Sirocco
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
        name: String,
        fireRateAddPercentage: Double? = null,
        damageAdd: Double? = null,
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

        if (name.isNotBlank()) {
            mods.add(name)
        }

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
            Weapon("AK Alpha", 100.0, 800.0, 6.5, 1.0),
            Weapon("AK Alpha", 100.0, 800.0, 6.5, 1.0).addMods("Rate of Fire", damageAdd = 6.0),
            Weapon("AK Alpha", 100.0, 800.0, 6.5, 1.0).addMods("Rate of Fire", 6.0),
            Weapon("AK Alpha (Mod Cadência e Dano)", 100.0, 800.0, 6.5, 1.0).addMods("Rate of Fire", 6.0, 4.0),
            Weapon("AK Alpha (Mod RAJADA)", 100.0, 800.0, 6.5, 1.0).addMods("Rate of Fire", -35.0, 50.0),
            Weapon("AK Alpha (Mod RAJADA, Cadência e Dano)", 100.0, 800.0, 6.5, 1.0).addMods("Rate of Fire", -35.0, 50.0).addMods("Rate of Fire", 6.0)
                .addMods("Rate of Fire", damageAdd = 4.0),
            Weapon("AK-12", 105.0, 735.0, 7.0, 1.25).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 1.5)
                .addMods("Spread", damageAdd = 1.5).addMods("Recoil", damageAdd = 1.5).addMods("Aim Speed", damageAdd = 1.5).addMods("Magazine Capacity", damageAdd = 1.5)
                .addMods(
                    "Reload Speed", damageAdd = 1.5
                ).addMods("Switch Speed", damageAdd = 1.5),
            Weapon("Beretta", 120.0, 810.0, 4.0, 1.4).addMods("Rate of Fire", 10.0).addMods("Damage", 1.0, 3.0).addMods("Range", 1.0).addMods("Spread", 1.0)
                .addMods("Recoil", 1.0).addMods("Aim Speed", 1.0).addMods("Magazine Capacity", 1.0).addMods("Reload Speed", 1.0).addMods("Switch Speed", 1.0),
            Weapon("Carmel (Mod Dano, Cadência e Corporal)", 96.0, 720.0, 7.0, 1.07).addMods("Heavy Metal", -27.5, 70.0, bodyMultiplierAddPercentage = 12.0)
                .addMods("Rate of Fire", 6.0).addMods("Body Damage", bodyMultiplierAddPercentage = 12.0),
            Weapon("Carmel (Mod Cadência [normal e especial] e Corporal)", 96.0, 720.0, 7.0, 1.07).addMods("Suppressive Fire", 45.0, headMultiplierAddPercentage = -45.0)
                .addMods("Rate of Fire", 6.0).addMods("Body Damage", bodyMultiplierAddPercentage = 12.0),
            Weapon("Cobalt (Mod Cadência [normal], +2 Corporal)", 95.0, 735.0, 7.0, 1.05).addMods("Rate of Fire", 5.2)
                .addMods("Rate of Fire", bodyMultiplierAddPercentage = 10.0).addMods("Rate of Fire", bodyMultiplierAddPercentage = 40.0),
            Weapon("Cobalt (Mod Cadência [especial] e Corporal)", 95.0, 735.0, 7.0, 1.05).addMods("Rate of Fire", 40.0, headMultiplierAddPercentage = -32.0)
                .addMods("Rate of Fire", 5.2).addMods("Rate of Fire", bodyMultiplierAddPercentage = 10.0),
            Weapon("Kord (Mod de Recoil)", 175.0, 640.0, 6.0, 1.15).addMods("Rate of Fire", 10.0).addMods("Accurate Shot", -26.0),
            Weapon("Kord", 175.0, 640.0, 6.0, 1.15).addMods("Rate of Fire", 10.0),
            Weapon("PKM Zenit", 145.0, 440.0, 5.5, 1.0).addMods("Rate of Fire", 6.0).addMods("Reduced Magazine  Capacity", 70.0, -40.0)
                .addMods("Body Damage", bodyMultiplierAddPercentage = 6.0),
            Weapon("QBZ", 106.0, 720.0, 7.0, 1.12).addMods("Rate of Fire", 8.0).addMods("Body Damage", bodyMultiplierAddPercentage = 13.0),
            Weapon("STK", 110.0, 865.0, 4.0, 1.25).addMods("Rate of Fire", 8.0).addMods("Body Damage", bodyMultiplierAddPercentage = 13.0).addMods(
                "Heavy Metal", -42.0, 60.0, bodyMultiplierAddPercentage = -10.0
            ),
            Weapon("FN SCAR-H", 175.0, 540.0, 7.0, 1.24).addMods("Rate of Fire", 10.0, 2.5).addMods("Damage", 1.0, 5.0).addMods("Range", 1.0, 2.5).addMods(
                "Spread", 1.0, 2.5
            ).addMods("Recoil", 1.0, 2.5).addMods("Aim Speed", 1.0, 2.5).addMods("Magazine  Capacity", 1.0, 2.5).addMods("Reload Speed", 1.0, 2.5)
                .addMods("Switch Speed", 1.0, 2.5),
            Weapon("AN-94", 125.0, 700.0, 7.0, 1.8).addMods("Rate of Fire", 10.0, 1.0).addMods("Range", damageAdd = 1.0).addMods("Limbs Damage", damageAdd = 1.0)
                .addMods("Spread", damageAdd = 1.0).addMods("Recoil", damageAdd = 1.0).addMods("Aim Speed", damageAdd = 1.0).addMods("Magazine  Capacity", damageAdd = 1.0)
                .addMods(
                    "Reload Speed", damageAdd = 1.0
                ).addMods("Switch Speed", damageAdd = 1.0),
            Weapon("MPAR-556", 120.0, 850.0, 4.0, 1.4).addMods("Rate of Fire", 10.0).addMods("Damage", 1.0, 3.0).addMods("Range", 1.0).addMods("Spread", 1.0)
                .addMods("Recoil", 1.0).addMods("Aim Speed", 1.0).addMods("Magazine Capacity", 1.0).addMods("Reload Speed", 1.0).addMods("Switch Speed", 1.0),
            Weapon("As-Val", 105.0, 765.0, 7.0, 1.25).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 1.5)
                .addMods("Spread", damageAdd = 1.5).addMods("Recoil", damageAdd = 1.5).addMods("Aim Speed", damageAdd = 1.5).addMods("Magazine Capacity", damageAdd = 1.5)
                .addMods(
                    "Reload " + "Speed", damageAdd = 1.5
                ).addMods("Switch Speed", damageAdd = 1.5),
            Weapon("A-545", 106.0, 735.0, 7.0, 1.18).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 1.5)
                .addMods("Spread", damageAdd = 1.5).addMods("Recoil", 1.5).addMods("Aim Speed", damageAdd = 1.5).addMods("Magazine Capacity", damageAdd = 1.5).addMods(
                    "Reload Speed", damageAdd = 1.5
                ).addMods("Switch Speed", damageAdd = 1.5),
            Weapon("M16A3 Custom", 108.0, 730.0, 7.0, 1.24).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 1.5)
                .addMods("Spread", damageAdd = 1.5).addMods("Recoil", damageAdd = 1.5).addMods("Aim Speed", damageAdd = 1.5).addMods("Magazine Capacity", damageAdd = 1.5)
                .addMods(
                    "Switch Speed", damageAdd = 1.5
                ),
            Weapon("FN Evolys", 140.0, 875.0, 6.0, 1.25).addMods("Rate of Fire", 10.0).addMods("Range", 1.0).addMods("Spread", 1.0).addMods("Spread Attack", 1.0)
                .addMods("Recoil", 1.0).addMods("Aim Speed", 1.0).addMods("Magazine Capacity", 1.0).addMods("Reload Speed", 1.0).addMods("Switch Speed", 1.0),
            Weapon("Type 97", 106.0, 740.0, 7.0, 1.18).addMods("Recoil", damageAdd = 1.5).addMods("Speed", damageAdd = 1.5).addMods("Switch Speed", damageAdd = 1.5).addMods(
                "Aim Speed", damageAdd = 1.5
            ).addMods("Range", damageAdd = 1.5).addMods("Spread", damageAdd = 1.5).addMods("Rate of Fire", 10.0, 1.5).addMods("Magazine Capacity", damageAdd = 1.5)
                .addMods("Damage", damageAdd = 3.0),
            Weapon("algumacoisa12", 200.0, 685.0, 7.0, 1.24),
            Weapon("Para", 140.0, 1012.0, 6.0, 1.25)
        )

        val engenheiroWeapons: List<Weapon> = listOf(
            Weapon("Tavor CTAR-21", 102.0, 970.0, 4.0, 1.6).addMods("Rate of Fire", 10.0, 1.0).addMods("Damage", 1.0, 3.0).addMods("Range", 1.0, 1.0).addMods(
                "Spread", 1.0, 1.0
            ).addMods("Recoil", 1.0, 1.0).addMods("Aim Speed", 1.0, 1.0).addMods("Magazine Capacity", 1.0, 1.0).addMods("Reload Speed", 1.0, 1.0)
                .addMods("Switch Speed", 1.0, 1.0),
            Weapon("Honey Badger", 128.0, 785.0, 6.0, 1.24).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 1.5)
                .addMods("Spread", damageAdd = 1.5).addMods("Recoil", damageAdd = 1.5).addMods("Aim Speed", damageAdd = 1.5).addMods("Magazine Capacity", damageAdd = 1.5)
                .addMods(
                    "Reload Speed", damageAdd = 1.5
                ).addMods("Switch Speed", damageAdd = 1.5),
            Weapon("Kriss Super V Custom (Precisão)", 100.0, 800.0, 4.5, 1.1).addMods("Rate of Fire", 5.0).addMods("Damage", damageAdd = 9.0)
                .addMods("Deadly Precision", damageAdd = 16.0, headMultiplierAddPercentage = 20.0),
            Weapon("Kriss Super V Custom (Light Bullets)", 100.0, 800.0, 4.5, 1.1).addMods("Rate of Fire", 5.0).addMods("Light Bullets", 40.0).addMods(
                "Damage", damageAdd = 9.0
            ),
            Weapon("Magpul", 100.0, 1010.0, 4.0, 1.42).addMods("Rate of Fire", 8.0).addMods("Body Damage", bodyMultiplierAddPercentage = 13.0),
            Weapon("PP-2011", 125.0, 790.0, 6.0, 1.25).addMods("Rate of Fire", 6.8).addMods("Body Damage", bodyMultiplierAddPercentage = 4.0),
            Weapon("CSV-9 Comodo", 92.0, 980.0, 4.8, 1.2).addMods("Rate of Fire", -13.0, 29.0, 25.0).addMods("Rate of Fire", bodyMultiplierAddPercentage = 8.0),
            Weapon("Famae SAF-200", 125.0, 750.0, 6.0, 1.3).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 1.5)
                .addMods("Spread", damageAdd = 1.5).addMods("Recoil", damageAdd = 1.5).addMods("Aim Speed", damageAdd = 1.5).addMods("Magazine Capacity", damageAdd = 1.5)
                .addMods("Reload Speed", damageAdd = 1.5).addMods("Switch Speed", damageAdd = 1.5),
            Weapon("CZ Scorpion", 128.0, 740.0, 6.0, 1.28).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 3.0)
                .addMods("Spread", 1.5).addMods("Recoil", 1.5).addMods("Aim Speed", 1.5).addMods("Magazine Capacity", 1.5).addMods("Reload Speed", 1.5)
                .addMods("Switch Speed", 1.5),
            Weapon("SR-3M", 100.0, 985.0, 4.0, 1.6).addMods("Rate of Fire", 10.0, 1.0).addMods("Damage", 1.0, 3.0).addMods("Range", 1.0, 1.0).addMods("Spread", 1.0, 1.0)
                .addMods("Recoil", 1.0, 1.0).addMods("Aim Speed", 1.0, 1.0).addMods("Magazine Capacity", 1.0, 1.0).addMods("Reload Speed", 1.0, 1.0)
                .addMods("Switch Speed", 1.0, 1.0),
            Weapon("AMB-17", 125.0, 745.0, 6.0, 1.3).addMods("Rate of Fire", 10.0, 1.5).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 1.5)
                .addMods("Spread", damageAdd = 1.5).addMods("Recoil", damageAdd = 1.5).addMods("Aim Speed", damageAdd = 1.5).addMods("Magazine Capacity").addMods(
                    "Reload Speed", damageAdd = 1.5
                ).addMods("Switch Speed", damageAdd = 1.5),
            Weapon("Taurus CT9 G2", 100.0, 815.0, 6.2, 1.05).addMods("Rate of Fire", 3.0).addMods("Body Damage", bodyMultiplierAddPercentage = 8.0),
            Weapon("Taurus CT9 G2 (Triple Threat", 100.0, 815.0, 6.2, 1.05).addMods("Rate of Fire", 3.0).addMods("Body Damage", bodyMultiplierAddPercentage = 8.0)
                .addMods("Triple Threat", -10.0, 15.0),
            Weapon("PPSH-41 Modern", 150.0, 625.0, 6.0, 1.45).addMods("Rate of Fire", 10.0, 2.0).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 2.0)
                .addMods("Spread", damageAdd = 2.0).addMods("Recoil", damageAdd = 2.0).addMods("Aim Speed", damageAdd = 2.0).addMods("Magazine Capacity", damageAdd = 2.0)
                .addMods("Reload Speed", damageAdd = 2.0).addMods("Switch Speed", damageAdd = 2.0),
            Weapon("Scar-L PDW", 150.0, 630.0, 5.3, 1.45).addMods("Rate of Fire", 10.0, 2.0).addMods("Damage", damageAdd = 3.0).addMods("Range", damageAdd = 2.0)
                .addMods("Spead", damageAdd = 2.0).addMods("Recoil", damageAdd = 2.0).addMods("Aim Speed", damageAdd = 2.0).addMods("Magazine Capacity", damageAdd = 2.0)
                .addMods("Reload Speed", damageAdd = 2.0).addMods("Switch Speed", damageAdd = 2.0),
            Weapon("XM8 Compact", 105.0, 950.0, 4.0, 1.66).addMods("Rate of Fire", 10.0, 1.0).addMods("Damage", 1.0, 3.0).addMods("Range", 1.0, 1.0)
                .addMods("Spread", 1.0, 1.0).addMods("Recoil", 1.0, 1.0).addMods("Aim Speed", 1.0, 1.0).addMods("Magazine Capacity", 1.0, 1.0).addMods(
                    "Reload Speed", 1.0, 1.0
                ).addMods("Switch Speed", 1.0, 1.0),
            Weapon("RONI", 116.0, 1126.0, 4.00, 1.66)
        )

        val pistolas: List<Weapon> = listOf(
            Weapon("Taurus Raging Hunter (Mod Cadência)", 350.0, 160.0, 6.0, 1.10).addMods("Rate of Fire", 5.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal, Precisão e Cadência)", 350.0, 160.0, 6.0, 1.10).addMods("Rate of Fire", 5.0).addMods(
                "Body Damage", bodyMultiplierAddPercentage = 10.0
            ).addMods("Deadeye", -10.0),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal e Cadência)", 350.0, 160.0, 6.0, 1.10).addMods("Rate of Fire", 5.0).addMods(
                "Body Damage", bodyMultiplierAddPercentage = 10.0
            ),
            Weapon("Taurus Raging Hunter (Mod Dano Corporal, Cadência e Cadência [especial])", 350.0, 160.0, 6.0, 1.10).addMods("Rate of Fire", 5.0)
                .addMods("Body Damage", bodyMultiplierAddPercentage = 10.0).addMods("High Noon", 55.0, -90.0),
            Weapon("SIG Sauer P226 (Mod Cadência e Dano)", 200.0, 275.0, 4.0, 1.3).addMods("Rate of Fire", 8.0).addMods("Damage", damageAdd = 26.0),
            Weapon("SIG Sauer P226 (Mod Cadência, Dano e Precisão)", 200.0, 275.0, 4.0, 1.3).addMods("Rate of Fire", 8.0).addMods("Damage", damageAdd = 26.0).addMods(
                "Expanding Bullets", -30.0, 80.0
            ),
            Weapon("Maxim 9", 220.0, 290.0, 3.5, 1.15).addMods("Range", 1.5).addMods("Damage Drop", 1.5).addMods("Rate of Fire", 10.0).addMods("Spread", 1.5)
                .addMods("Recoil", 1.5).addMods("Aim Speed", 1.5).addMods("Magazine Capacity", 1.5).addMods("Reload Speed", 1.5).addMods("Switch Speed", 1.5),
            Weapon("ST Kinetics (Mod Cadência e Corporal)", 108.0, 950.0, 5.0, 1.1).addMods("Rate of Fire", 12.0).addMods("Body Damage", bodyMultiplierAddPercentage = 16.0),
            Weapon("ST Kinetics (Mod Cadência, Corporal e Dupla)", 108.0, 950.0, 5.0, 1.1).addMods("Rate of Fire", 12.0).addMods(
                "Body Damage", bodyMultiplierAddPercentage = 16.0
            ).addMods(
                "With Two Hands", 7.0, -15.0, -20.0
            ).addMods("2X Weapons", 100.0),
            Weapon("Taurus Judge", 680.0, 100.0, 3.0, 1.4).addMods("Rate of Fire", 10.0, 0.6).addMods("Damage", damageAdd = 1.2).addMods("Range", damageAdd = 0.6)
                .addMods("Damage Drop", damageAdd = 0.6).addMods("Spread", damageAdd = 0.6).addMods("Recoil", damageAdd = 0.6).addMods("Aim Speed", damageAdd = 0.6).addMods(
                    "Reload Speed", damageAdd = 0.6
                ).addMods("Switch Speed", damageAdd = 0.6),
            Weapon("Mauser (Prohibited Assembly)", 175.0, 400.0, 3.8, 1.05).addMods("Rate of Fire", 5.0).addMods("Damage", damageAdd = 20.0).addMods(
                "Prohibited Assembly", 45.0, -40.0
            ),
            Weapon("Mauser (With Two Hands)", 175.0, 400.0, 3.8, 1.05).addMods("Rate of Fire", 5.0).addMods("Damage", damageAdd = 20.0).addMods(
                "With Two Hands", damageAdd = -20.0, headMultiplierAddPercentage = -10.0
            ).addMods("2X Weapons", 100.0),
            Weapon(
                "Deset Eagle", 275.0, 270.0, 4.25, 1.35
            ).addMods("Rate of Fire", 10.0).addMods("Range", 1.5).addMods("Damage Drop", 1.5).addMods("Spread", 1.5).addMods("Recoil", 1.5).addMods("Aim Speed", 1.5)
                .addMods("Magazine", 1.5).addMods("Reload Speed", 1.5).addMods("Switch Speed", 1.5),
            Weapon("M1911A1 (Hide!)", 200.0, 290.0, 6.0, 1.1).addMods("Hide!", 20.0, 30.0).addMods("Rate of Fire", 5.0).addMods("Damage", damageAdd = 20.0),
            Weapon("M1911A1 (One is Enough)", 200.0, 290.0, 6.0, 1.1).addMods("One is Enough", -50.0, 160.0).addMods("Rate of Fire", 5.0).addMods("Damage", damageAdd = 20.0),
            Weapon("Glock 18c", 108.0, 800.0, 5.0, 1.25).addMods("Rate of Fire", 10.0, 1.0).addMods("Damage", 1.0, 1.0).addMods("Damage Drop", 1.0, 1.0)
                .addMods("Spread", 1.0, 1.0).addMods("Recoil", 1.0, 1.0).addMods("Aim Speed", 1.0, 1.0).addMods("Magazine Capacity", 1.0, 1.0)
                .addMods("Reload Speed", 1.0, 1.0).addMods("Switch Speed", 1.0, 1.0),
            Weapon("Makarov (Golden Bullets)", 135.0, 300.0, 4.0, 1.0).addMods("Rate of Fire", 10.0).addMods("Damage", damageAdd = 15.0)
                .addMods("Golden Bullets", -35.0, 35.0),
            Weapon("Makarov (Blockbuster)", 135.0, 300.0, 4.0, 1.0).addMods("Rate of Fire", 10.0).addMods("Damage", damageAdd = 15.0).addMods("Blockbuster", 15.0),
            Weapon("R8", 275.0, 281.0, 4.25, 1.35),
            Weapon("VP9", 170.0, 434.0, 3.8, 1.3),
            Weapon("Marakov, futura att", 345.0, 156.0, 4.0, 1.30)
        )

        val sniperWeapons: List<Weapon> = listOf(
            Weapon("FN SCAR Creedmoor", 250.0, 370.0, 5.0, 1.40).addMods("Rate of Fire", fireRateAddPercentage = 25.0)
        )
    }
}
