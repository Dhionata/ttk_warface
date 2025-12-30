package br.com.dhionata

import br.com.dhionata.weapon.Weapon
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object TTKCalculator {

    // Constantes para simulação de alvo (em metros)
    private const val HEAD_RADIUS = 0.15
    private const val BODY_RADIUS = 0.35

    /**
     * Calcula o dano de UM ÚNICO projétil/pellet.
     * Retorna Double para manter precisão antes da soma total.
     */
    private fun calculateSinglePelletDamage(
        weaponDamagePerPellet: Double, // Dano total da arma / numero de pellets
        damageMultiplier: Double,
        absorption: Double,
        pellets: Int,
        resistance: Double,
        weaponTypeResistance: Double,
    ): Double {
        // Regra da Wiki: Absorption é dividida pelo número de pellets
        val effectiveAbsorption = absorption / pellets.toDouble()

        val baseDamage = (weaponDamagePerPellet * damageMultiplier) - effectiveAbsorption

        if (baseDamage <= 0) return 0.0

        return baseDamage * (1 - resistance) * (1 - weaponTypeResistance)
    }

    // Fator de conversão: Quanto 1 ponto de Spread abre o cone em metros por metro de distância.
    // Ajuste este valor para calibrar a "realidade" do jogo.
    // 0.014 é um valor empírico para Warface.
    private const val SPREAD_FACTOR = 1

    private fun calculateHitRate(
        distance: Double,
        spread: Double,
        isHeadshot: Boolean,
    ): Double {
        if (distance <= 1.5) return 1.0 // Até 1.5m consideramos acerto total (cano no peito)
        if (spread <= 0.0) return 1.0

        // Cálculo do raio do cone de dispersão nessa distância
        val spreadRadius = distance * (spread * SPREAD_FACTOR)

        // Raio do alvo (Cabeça ou Corpo)
        val targetRadius = if (isHeadshot) HEAD_RADIUS else BODY_RADIUS

        // Se o alvo é maior que o espalhamento, 100% dos pellets acertam
        if (targetRadius >= spreadRadius) return 1.0

        // Se o espalhamento é maior, calculamos a proporção
        val hitRatio = (targetRadius * targetRadius) / (spreadRadius * spreadRadius)

        return min(1.0, max(0.0, hitRatio))
    }

    /**
     * Função Principal: Retorna o número de TIROS (cliques) para matar.
     * Retorna INT, como no seu código original.
     */
    fun bulletsToKillWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean = false,
        debug: Boolean = false,
        distance: Double = 0.0,
    ): Int {
        // Dados do Alvo
        var remainingArmor = weapon.set.armor.toDouble()
        var remainingHealth = weapon.set.hp.toDouble()

        // Multiplicadores
        val equipmentProtection = if (isHeadshot) weapon.set.headProtection else weapon.set.bodyProtection
        val damageMultiplier =
            ((if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier) * weapon.set.entityDmgMult * (1 + weapon.set.cyborgDmgBuff)) - equipmentProtection

        // Dano Base na Distância
        val effectiveDamage = if (distance > 0) weapon.getEffectiveDamage(distance) else weapon.damage

        // --- Lógica Diferenciada: Shotgun vs Rifle ---
        val totalPellets = weapon.pellets
        val damagePerPellet = effectiveDamage / totalPellets

        // 1. Calcula dano de 1 pellet sofrendo absorção (Absorção / QuantidadePellets)
        val singlePelletDamage = calculateSinglePelletDamage(
            damagePerPellet,
            damageMultiplier,
            weapon.set.absorption,
            totalPellets,
            weapon.set.resistance,
            weapon.set.weaponTypeResistance
        )

        // 2. Define quantos pellets acertam
        // Se for arma de 1 bala (Rifle, SMG, Slug), HitRate é sempre 1.0 (comportamento original)
        // Se for Shotgun, calcula baseado na dispersão.
        val hitRate = if (totalPellets > 1) {
            val spreadVal = if (distance == 0.0) weapon.spreadMin else weapon.hipAccuracy.toDouble()
            val usedSpread = if (weapon.zoomSpreadMin > 0 && weapon.zoomSpreadMin < weapon.spreadMin) weapon.zoomSpreadMin else weapon.spreadMin
            calculateHitRate(distance, usedSpread, isHeadshot)
        } else {
            1.0
        }

        // Dano total do disparo (soma dos pellets que acertaram)
        val finalDamagePerShot = (singlePelletDamage * totalPellets * hitRate).roundToInt()

        if (finalDamagePerShot <= 0) return Int.MAX_VALUE

        // --- Simulação dos Tiros ---
        var shots = 0

        // Definição de absorção da armadura (Geralmente 80%, exceto SEDs 99% ou casos raros)
        val armorAbsorptionRatio = if (weapon.name.contains("SED", true)) 0.99 else 0.80

        while (remainingHealth > 0) {
            shots++

            // Verifica se ainda tem armadura para absorver o tiro ATUAL
            val currentShotArmorDamage: Int
            val currentShotHealthDamage: Int

            if (remainingArmor > 0) {
                // Tem armadura: Aplica a regra 80/20
                val absorbAmount = (finalDamagePerShot * armorAbsorptionRatio).roundToInt()

                // Se o dano à armadura for maior que a armadura restante, o excedente NÃO vai pra vida (regra padrão Warface simples),
                // mas a armadura zera. O tiro seguinte pegará na carne.
                if (remainingArmor - absorbAmount < 0) {
                    currentShotArmorDamage = remainingArmor.toInt() // Quebra a armadura toda
                    // O dano à vida é os 20% fixos + (opcionalmente) o excedente se a mecânica for penetration
                    // Vamos manter o padrão: recebe o dano de HP calculado originalmente
                    currentShotHealthDamage = finalDamagePerShot - absorbAmount
                } else {
                    currentShotArmorDamage = absorbAmount
                    currentShotHealthDamage = finalDamagePerShot - absorbAmount
                }
            } else {
                // Sem armadura: 100% dano na vida
                currentShotArmorDamage = 0
                currentShotHealthDamage = finalDamagePerShot
            }

            remainingArmor -= currentShotArmorDamage
            if (remainingArmor < 0) remainingArmor = 0.0

            remainingHealth -= currentShotHealthDamage

            if (debug) {
                println("Tiro $shots | Dano Total: $finalDamagePerShot | Armor Dmg: $currentShotArmorDamage | HP Dmg: $currentShotHealthDamage | Restante -> Armor: $remainingArmor HP: $remainingHealth")
            }

            if (shots > 200) return Int.MAX_VALUE
        }

        return shots
    }

    /**
     * Calcula o TTK retornando o PAR (Tiros, Tempo).
     * Mantém compatibilidade com o resto do seu código.
     */
    fun calculateTTKWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean,
        debug: Boolean = false,
    ): Pair<Int, Double> {
        val shotsNeeded = bulletsToKillWithProtectionInt(weapon, isHeadshot, debug, 0.0)

        if (shotsNeeded == Int.MAX_VALUE) {
            return Pair(shotsNeeded, Double.POSITIVE_INFINITY)
        }

        val shotsPerSecond = weapon.fireRate / 60.0

        // Cálculo de tempo clássico (Ciclo completo)
        val timeToKill = shotsNeeded / shotsPerSecond

        return Pair(shotsNeeded, timeToKill)
    }

    /**
     * Mesma lógica, mas permitindo passar uma distância customizada.
     */
    fun calculateTTKAtDistance(
        weapon: Weapon,
        isHeadshot: Boolean,
        distance: Double,
        debug: Boolean = false,
    ): Pair<Int, Double> {
        val shotsNeeded = bulletsToKillWithProtectionInt(weapon, isHeadshot, debug, distance)

        if (shotsNeeded == Int.MAX_VALUE) {
            return Pair(shotsNeeded, Double.POSITIVE_INFINITY)
        }

        val shotsPerSecond = weapon.fireRate / 60.0
        val timeToKill = shotsNeeded / shotsPerSecond

        return Pair(shotsNeeded, timeToKill)
    }

    fun calculateMaxDistanceForKill(
        weapon: Weapon,
        isHeadshot: Boolean,
        debug: Boolean = false,
    ): Double {
        val hp = weapon.set.hp.toDouble()
        val armor = weapon.set.armor.toDouble()
        val equipmentProtection = if (isHeadshot) weapon.set.headProtection else weapon.set.bodyProtection
        val baseMultiplier = if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier
        val damageMultiplier = (baseMultiplier * weapon.set.entityDmgMult * (1 + weapon.set.cyborgDmgBuff)) - equipmentProtection

        if (damageMultiplier <= 0) return 0.0

        val damageForKill = min(hp * 5.0, hp + armor)

        val resistanceFactor = (1 - weapon.set.resistance) * (1 - weapon.set.weaponTypeResistance)
        if (resistanceFactor <= 0) return 0.0

        val damageBeforeResistance = damageForKill / resistanceFactor

        val damageBeforeAbsorption = damageBeforeResistance + weapon.set.absorption

        val requiredWeaponDamage = damageBeforeAbsorption / damageMultiplier

        if (weapon.damage < requiredWeaponDamage) return 0.0
        if (weapon.minDamage >= requiredWeaponDamage) return Double.POSITIVE_INFINITY

        val allowedDrop = weapon.damage - requiredWeaponDamage
        val distAdd = allowedDrop / weapon.damageDropPerMeter

        return weapon.range + distAdd
    }

    fun printWeaponTTKWithProtection(
        weapon: Weapon,
    ) {
        println("Arma: ${weapon.name}")

        println(" - ${weapon.ttk.first().first} tiro(s) na cabeça em ${"%.3f".format(weapon.ttk.first().second)}(s)")

        println(" - ${weapon.ttk.elementAt(1).first} tiro(s) no corpo em ${"%.3f".format(weapon.ttk.elementAt(1).second)}(s)")

        println(" - TTK Médio: ${weapon.ttk.last().first} tiro(s) em ${"%.3f".format(weapon.ttk.last().second)}(s)")
        println("------------------------------")
    }

    fun findBestTTK(
        weapons: List<Weapon>,
        debug: Boolean = false,
    ): Triple<Weapon, Weapon, Weapon> {
        val bestHeadWeapon = weapons.minBy { it.ttk.first().second }
        val bestBodyWeapon = weapons.minBy { it.ttk.elementAt(1).second }
        val bestTTKMediaWeapon = weapons.minBy { it.ttk.last().second }

        println("===================================")
        println(
            "Melhor TTK para a Cabeça: ${bestHeadWeapon.name} com TTK de ${"%.3f".format(bestHeadWeapon.ttk.first().second)} segundos em ${
                bulletsToKillWithProtectionInt(bestHeadWeapon, true, debug)
            } tiro(s)"
        )
        println(
            "Melhor TTK para o Corpo: ${bestBodyWeapon.name} com TTK de ${"%.3f".format(bestBodyWeapon.ttk.elementAt(1).second)} segundos em${
                bulletsToKillWithProtectionInt
                    (bestBodyWeapon, debug = debug)
            } tiros(s)"
        )
        println(
            "Melhor TTK Médio (Cabeça e Corpo): ${bestTTKMediaWeapon.name} com TTK Médio de ${"%.3f".format(bestTTKMediaWeapon.ttk.last().second)} segundos em ${
                (bulletsToKillWithProtectionInt(
                    bestTTKMediaWeapon, true, debug
                ) + bulletsToKillWithProtectionInt(bestTTKMediaWeapon, debug = debug)) / 2
            } tiro(s)"
        )
        println("===================================")

        return Triple(bestHeadWeapon, bestBodyWeapon, bestTTKMediaWeapon)
    }
}
