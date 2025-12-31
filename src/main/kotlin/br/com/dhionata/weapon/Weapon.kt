package br.com.dhionata.weapon

import br.com.dhionata.Set
import br.com.dhionata.TTKCalculator
import br.com.dhionata.formatValue
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
    var pellets: Int = 1,
    var spreadMin: Double = 0.0,
    private var spreadMax: Double = 0.0,
    var zoomSpreadMin: Double = 0.0,
    private var zoomSpreadMax: Double = 0.0,
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

    val hipAccuracy: Int
        get() = calculateAccuracy(spreadMin, spreadMax)

    private val aimAccuracy: Int
        get() = calculateAccuracy(zoomSpreadMin, zoomSpreadMax)

    private fun calculateAccuracy(min: Double, max: Double): Int {
        val sum = min + max
        return when {
            sum <= 20 -> ((40 - sum) / 0.4).roundToInt()
            sum <= 60 -> (50 - (sum - 20)).roundToInt()
            else -> (10 - ((sum - 60) * 0.165)).roundToInt()
        }
    }

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

        val distanceToMinDamage = if (damageDropPerMeter > 0) {
            range + ((damage - minDamage) / damageDropPerMeter)
        } else {
            Double.POSITIVE_INFINITY
        }
        val minDamageDistStr = if (distanceToMinDamage.isInfinite()) "Infinita" else "${"%.2f".format(distanceToMinDamage)}m"

        val accuracyInfo = if (spreadMin > 0 || spreadMax > 0) {
            "| Precisão: Hip[$hipAccuracy], Aim[$aimAccuracy] "
        } else {
            ""
        }

        val pelletsInfo = if (pellets > 1) "| Pellets: $pellets " else ""

        return "Nome: $name | Dano: ${formatValue(damage)} $pelletsInfo| Cadência: $fireRate | Cabeça X $headMultiplier | Corpo X ${
            formatValue(bodyMultiplier)
        } | Alcance: ${
            formatValue(range)
        }m | Queda/m: ${
            formatValue(damageDropPerMeter)
        } | Dano Mín.: $minDamage @ $minDamageDistStr $oneHitKillInfo$accuracyInfo| TTK[Tiro(s) em Tempo(s)]: Cabeça[${ttk.first().first} em ${
            formatValue(ttk.first().second, 3)
        }], Corpo[${ttk.elementAt(1).first} em ${
            formatValue(ttk.elementAt(1).second, 3)
        }], Média[${ttk.last().first} em ${formatValue(ttk.last().second, 3)}]"
    }

    fun attachments(
        name: String,
        fireRateAddPercentage: Double? = null,
        damageAdd: Double? = null,
        headMultiplierAddPercentage: Double? = null,
        bodyMultiplierAddPercentage: Double? = null,
        rangeAdd: Double? = null,
        damageDropPerMeterAddPercentage: Double? = null,
        damageAddDropPerMeter: Double? = null,
        spreadAddPercentage: Double? = null,
        zoomSpreadAddPercentage: Double? = null,
        pellets: Int? = null,
        pelletsAdd: Int? = null,
        minDamageAdd: Double? = null,
    ): Weapon {
        addMods(
            name,
            fireRateAddPercentage,
            damageAdd,
            headMultiplierAddPercentage,
            bodyMultiplierAddPercentage,
            rangeAdd,
            damageDropPerMeterAddPercentage,
            damageAddDropPerMeter,
            spreadAddPercentage,
            zoomSpreadAddPercentage,
            pellets,
            pelletsAdd,
            minDamageAdd
        )

        return this
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
        damageDropPerMeterAddPercentage: Double? = null,
        damageAddDropPerMeter: Double? = null,
        spreadAddPercentage: Double? = null,
        zoomSpreadAddPercentage: Double? = null,
        pellets: Int? = null,
        pelletsAdd: Int? = null,
        minDamageAdd: Double? = null,
    ): Weapon {
        if (pellets != null) {
            // Se o número de pellets mudar, ajustamos o dano base para refletir o dano por pellet antigo
            // A lógica anterior estava incorreta para a Huckleberry Packed Shells, pois ela muda de 1 pellet (slug) para 10 pellets (buckshot)
            // e o dano total é ajustado explicitamente pelo damageAdd.
            // Se pellets for definido explicitamente, apenas atualizamos o valor, assumindo que o dano será corrigido por damageAdd se necessário.
            this.pellets = pellets
        }

        if (pelletsAdd != null && this.pellets > 0) {
            // Se estamos adicionando pellets a uma arma que já tem pellets, assumimos que o dano total aumenta proporcionalmente
            // a menos que damageAdd seja usado para corrigir.
            // No entanto, para evitar comportamentos inesperados com mods complexos,
            // vamos confiar que o usuário configurou damageAdd corretamente se o dano por pellet mudar.
            // Para o caso simples de "mais pellets do mesmo tipo", o dano aumenta.
            val oldPellets = this.pellets
            val newPellets = oldPellets + pelletsAdd

            // Se não houver damageAdd explícito, ajustamos o dano proporcionalmente
            if (damageAdd == null) {
                this.damage = this.damage / oldPellets * newPellets
            }
            this.pellets = newPellets
        }

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
        if (spreadAddPercentage != null) {
            spreadMin += spreadMin * (spreadAddPercentage / 100.0)
            spreadMax += spreadMax * (spreadAddPercentage / 100.0)
        }
        if (zoomSpreadAddPercentage != null) {
            zoomSpreadMin += zoomSpreadMin * (zoomSpreadAddPercentage / 100.0)
            zoomSpreadMax += zoomSpreadMax * (zoomSpreadAddPercentage / 100.0)
        }
        if (minDamageAdd != null) {
            this.minDamage += minDamageAdd
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
