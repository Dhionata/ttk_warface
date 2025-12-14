package br.com.dhionata

import br.com.dhionata.weapon.Weapon
import kotlin.math.min
import kotlin.math.roundToInt

object TTKCalculator {

    private fun calculateDamageInt(
        weaponDamage: Double,
        damageMultiplier: Double,
        absorption: Double,
        pellets: Int = 1,
        resistance: Double,
        weaponTypeResistance: Double,
    ): Int {
        val effectiveAbsorption = absorption / pellets
        val baseDamage = weaponDamage * damageMultiplier - effectiveAbsorption
        if (baseDamage <= 0) return 0
        val finalDamage = baseDamage * (1 - resistance) * (1 - weaponTypeResistance)
        return finalDamage.roundToInt()
    }

    fun bulletsToKillWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean = false,
        debug: Boolean = false,
        distance: Double = 0.0,
    ): Int {
        var remainingArmor = weapon.set.armor
        var remainingHealth = weapon.set.hp
        var shots = 0

        val equipmentProtection = if (isHeadshot) weapon.set.headProtection else weapon.set.bodyProtection

        val damageMultiplier =
            ((if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier) * weapon.set.entityDmgMult * (1 + weapon.set.cyborgDmgBuff)) - equipmentProtection

        val effectiveDamage = if (distance > 0) weapon.getEffectiveDamage(distance) else weapon.damage

        val finalDamage = calculateDamageInt(
            effectiveDamage, damageMultiplier, weapon.set.absorption, resistance = weapon.set.resistance, weaponTypeResistance = weapon.set.weaponTypeResistance
        )

        if (finalDamage <= 0) return Int.MAX_VALUE // Retorna um número alto se o dano for zero ou negativo

        val armorDamage = (finalDamage * 80) / 100
        val healthDamage = finalDamage - armorDamage

        while (remainingHealth > 0) {
            if (debug) {
                println("------------------------------")
                println("Arma: ${weapon.name} @ ${distance}m")
                println(if (isHeadshot) "Headshot" else "Corpo")
                println("Tiro ${shots + 1}:")
                println(" - Dano Efetivo da Arma: $effectiveDamage")
                println(" - Dano Total (arredondado): $finalDamage")
                println(" - Dano à Armadura: $armorDamage")
                println(" - Dano à Saúde: $healthDamage")
                println(" - Armadura Antes do Tiro: $remainingArmor")
                println(" - Saúde Antes do Tiro: $remainingHealth")
            }

            remainingArmor -= armorDamage
            if (remainingArmor < 0) {
                remainingHealth += remainingArmor
                remainingArmor = 0
            }

            remainingHealth -= healthDamage

            if (debug) {
                println(" - Armadura Após o Tiro: $remainingArmor")
                println(" - Saúde Após o Tiro: $remainingHealth")
                println("------------------------------")
            }
            shots++
        }

        return shots
    }

    fun calculateTTKWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean,
        debug: Boolean = false,
    ): Pair<Int, Double> {
        val shotsNeeded = bulletsToKillWithProtectionInt(weapon, isHeadshot, debug, 0.0)
        val shotsPerSecond = weapon.fireRate / 60.0
        val totalTime = shotsNeeded / shotsPerSecond
        return Pair(shotsNeeded, totalTime)
    }

    /**
     * Calcula o TTK (Time To Kill) de uma arma a uma distância específica.
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
        val totalTime = shotsNeeded / shotsPerSecond
        return Pair(shotsNeeded, totalTime)
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
            "Melhor TTK para o Corpo: ${bestBodyWeapon.name} com TTK de ${"%.3f".format(bestBodyWeapon.ttk.elementAt(1).second)} segundos em ${
                bulletsToKillWithProtectionInt(bestBodyWeapon, debug = debug)
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

    /**
     * Calcula a distância máxima para matar com um tiro na cabeça ou no corpo.
     * Considera a mecânica de 80% de dano na armadura e 20% na vida.
     */
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

        if (damageMultiplier <= 0) {
            if (debug) println("Multiplicador de dano <= 0. Impossível matar.")
            return 0.0
        }

        val damageForPassthroughKill = hp * 5.0

        val damageForDepletionKill = hp + armor

        val requiredFinalDamage = min(damageForPassthroughKill, damageForDepletionKill)

        if (debug) {
            println("------------------------------")
            println("Cálculo de Distância Máxima para: ${weapon.name} (${if (isHeadshot) "Cabeça" else "Corpo"})")
            println("Vida: $hp, Armadura: $armor")
            println("Dano para matar via 20% (HP*5): $damageForPassthroughKill")
            println("Dano para matar via Depleção (HP+Armor): $damageForDepletionKill")
            println("Dano Final Necessário (menor dos dois): $requiredFinalDamage")
        }

        val resistanceFactor = (1 - weapon.set.resistance) * (1 - weapon.set.weaponTypeResistance)

        if (resistanceFactor <= 0) {
            if (debug) println("Resistência total do alvo é 100%. Impossível matar.")
            return 0.0
        }

        val damageBeforeResistance = requiredFinalDamage / resistanceFactor

        val damageBeforeAbsorption = damageBeforeResistance + weapon.set.absorption

        val requiredWeaponDamage = damageBeforeAbsorption / damageMultiplier

        if (debug) {
            println("Multiplicador Total: $damageMultiplier")
            println("Fator de Resistência: $resistanceFactor")
            println("Dano Base Necessário na Arma: $requiredWeaponDamage")
            println("Dano Atual da Arma: ${weapon.damage}")
        }

        if (weapon.damage < requiredWeaponDamage) {
            if (debug) println("Arma não atinge o dano necessário nem à queima-roupa.")
            return 0.0
        }

        if (weapon.minDamage >= requiredWeaponDamage) {
            if (debug) println("Dano mínimo da arma é suficiente. Alcance infinito.")
            return Double.POSITIVE_INFINITY
        }

        val allowedDamageDrop = weapon.damage - requiredWeaponDamage
        val additionalDistance = allowedDamageDrop / weapon.damageDropPerMeter
        val maxDistance = weapon.range + additionalDistance

        if (debug) {
            println("Queda de Dano Permitida: $allowedDamageDrop")
            println("Distância Adicional: $additionalDistance")
            println("Distância Máxima Total: $maxDistance")
            println("------------------------------")
        }

        return maxDistance
    }
}
