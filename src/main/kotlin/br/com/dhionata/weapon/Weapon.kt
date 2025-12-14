package br.com.dhionata.weapon

import br.com.dhionata.Set
import br.com.dhionata.TTKCalculator
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

data class Weapon(
    val name: String,
    var damage: Double,
    private var _fireRate: Double, // valor interno para cálculos precisos
    var headMultiplier: Double,
    var bodyMultiplier: Double,
    var range: Double,
    var damageDropPerMeter: Double,
    var minDamage: Double,
    val ttk: MutableList<Pair<Int, Double>> = mutableListOf(),
    private val mods: MutableSet<String> = mutableSetOf(),
) {

    var set: Set = Set.SetsAndEnemy.Sirocco
        set(value) {
            field = value
            updateTTK()
        }

    val fireRate: Int
        get() = _fireRate.roundToInt()

    override fun toString(): String {
        val maxHeadDist = TTKCalculator.calculateMaxDistanceForKill(this, true)
        val maxBodyDist = TTKCalculator.calculateMaxDistanceForKill(this, false)

        val oneHitKillInfo = if (maxHeadDist > 0.0 || maxBodyDist > 0.0) {
            val headDistStr = if (maxHeadDist.isInfinite()) "Infinita" else "${"%.2f".format(maxHeadDist)}m"
            val bodyDistStr = if (maxBodyDist.isInfinite()) "Infinita" else "${"%.2f".format(maxBodyDist)}m"
            "| Dist. Max (1 tiro): Cabeça[$headDistStr], Corpo[$bodyDistStr] "
        } else {
            ""
        }

        return "Nome: $name | Dano: $damage | Cadência: $fireRate | Cabeça X $headMultiplier | Corpo X ${
            BigDecimal(bodyMultiplier).setScale(
                2, RoundingMode.HALF_UP
            )
        } | Alcance: ${range}m | Queda/m: $damageDropPerMeter | Dano Mín.: $minDamage $oneHitKillInfo| TTK[Tiro(s) em Tempo(s)]: Cabeça[${ttk.first().first} em ${
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
        rangeAdd: Double? = null,
        damageDropPerMeterAddPercentage: Double? = null
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
        if (rangeAdd != null) {
            range += rangeAdd
        }
        if (damageDropPerMeterAddPercentage != null) {
            damageDropPerMeter += damageDropPerMeter * (damageDropPerMeterAddPercentage / 100.0)
        }

        updateTTK()

        if (name.isNotBlank()) {
            mods.add(name)
        }

        return this
    }

    /**
     * Calcula o dano efetivo a uma determinada distância.
     *
     * @param distance A distância para a qual calcular o dano.
     * @return O dano efetivo naquela distância.
     */
    fun getEffectiveDamage(distance: Double): Double {
        if (distance <= range) {
            return damage
        }
        val damageDrop = (distance - range) * damageDropPerMeter
        val effectiveDamage = damage - damageDrop
        return effectiveDamage.coerceAtLeast(minDamage)
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
}
