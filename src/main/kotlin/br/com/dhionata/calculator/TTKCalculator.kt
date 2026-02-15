package br.com.dhionata.calculator

import br.com.dhionata.Set
import br.com.dhionata.weapon.Weapon
import kotlin.math.ceil
import kotlin.math.roundToInt

class TTKCalculator(
    private val damageCalculator: IDamageCalculator,
) : ITTKCalculator {

    override fun calculateTTK(
        weapon: Weapon,
        targetSet: Set,
        isHeadshot: Boolean,
        distance: Double,
        isAiming: Boolean,
        debug: Boolean,
    ): Pair<Int, Double> {
        val shotsNeeded = calculateShotsToKill(weapon, targetSet, isHeadshot, distance, isAiming, debug)

        if (shotsNeeded == Int.MAX_VALUE) {
            return Pair(shotsNeeded, Double.POSITIVE_INFINITY)
        }

        val shotsPerSecond = weapon.fireRate / 60.0
        var timeToKill = shotsNeeded / shotsPerSecond

        if (weapon.magazineCapacity in 1..<shotsNeeded) {
            val reloadsNeeded = (shotsNeeded - 1) / weapon.magazineCapacity
            timeToKill += reloadsNeeded * (weapon.reloadTime / 1000.0)
        }

        return Pair(shotsNeeded, timeToKill)
    }

    private fun calculateShotsToKill(
        weapon: Weapon,
        targetSet: Set,
        isHeadshot: Boolean,
        distance: Double,
        isAiming: Boolean,
        debug: Boolean,
    ): Int {
        val finalDamagePerShot = damageCalculator.calculateDamagePerShot(weapon, targetSet, distance, isHeadshot, isAiming)

        if (debug) {
            println("Dano Final do Tiro: $finalDamagePerShot")
        }

        if (finalDamagePerShot <= 0) {
            if (debug) println("DEBUG: Dano final por tiro <= 0. Impossível matar.")
            return Int.MAX_VALUE
        }

        val armorAbsorptionRatio = if (weapon.name.contains("SED", true)) 0.99 else 0.80

        var remainingArmor = targetSet.armor
        var remainingHealth = targetSet.hp
        var shots = 0

        // Modo DEBUG detalhado passo a passo
        if (debug) {
            // Reinicia variáveis locais para simulação
            var simArmor = remainingArmor
            var simHealth = remainingHealth

            while (simHealth > 0) {
                shots++
                val absorbAmount = (finalDamagePerShot * armorAbsorptionRatio).roundToInt()

                val currentArmorDmg: Double
                val currentHealthDmg: Double

                if (simArmor > 0) {
                    if (simArmor - absorbAmount < 0) {
                        currentArmorDmg = simArmor
                        currentHealthDmg = (finalDamagePerShot - absorbAmount).toDouble()
                    } else {
                        currentArmorDmg = absorbAmount.toDouble()
                        currentHealthDmg = (finalDamagePerShot - absorbAmount).toDouble()
                    }
                } else {
                    currentArmorDmg = 0.0
                    currentHealthDmg = finalDamagePerShot.toDouble()
                }

                simArmor -= currentArmorDmg
                if (simArmor < 0) simArmor = 0.0
                simHealth -= currentHealthDmg

                println("Tiro $shots | Dano: $finalDamagePerShot | HP Restante: ${"%.1f".format(simHealth)}")

                // Prevenção de loop infinito em debug
                if (shots > 100) break
            }
            return shots
        }

        // --- Cálculo Matemático Otimizado (Sem loop) ---
        val absorbAmount = (finalDamagePerShot * armorAbsorptionRatio).roundToInt()
        val damageToHealthPhase1 = finalDamagePerShot - absorbAmount

        if (remainingArmor > 0 && absorbAmount > 0) {
            val shotsToBreakArmor = ceil(remainingArmor / absorbAmount).toInt()
            val potentialHealthDamage = shotsToBreakArmor.toDouble() * damageToHealthPhase1

            if (remainingHealth <= potentialHealthDamage) {
                return ceil(remainingHealth / damageToHealthPhase1).toInt()
            } else {
                shots += shotsToBreakArmor
                remainingHealth -= potentialHealthDamage
            }
        }

        if (remainingHealth > 0) {
            shots += ceil(remainingHealth / finalDamagePerShot).toInt()
        }

        return shots
    }

    override fun calculateMaxDistanceForKill(
        weapon: Weapon,
        targetSet: Set,
        isHeadshot: Boolean,
    ): Double {
        val hp = targetSet.hp
        val armor = targetSet.armor
        val equipmentProtection = if (isHeadshot) targetSet.headProtection else targetSet.bodyProtection
        val baseMultiplier = if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier
        val damageMultiplier = (baseMultiplier * targetSet.entityDmgMult * (1 + targetSet.cyborgDmgBuff)) - equipmentProtection

        if (damageMultiplier <= 0) return 0.0

        val damageForKill = kotlin.math.min(hp * 5.0, hp + armor)

        val resistanceFactor = (1 - targetSet.resistance) * (1 - targetSet.weaponTypeResistance)
        if (resistanceFactor <= 0) return 0.0

        val damageBeforeResistance = damageForKill / resistanceFactor
        val damageBeforeAbsorption = damageBeforeResistance + targetSet.absorption
        val requiredWeaponDamage = damageBeforeAbsorption / damageMultiplier

        if (weapon.damage < requiredWeaponDamage) return 0.0
        if (weapon.minDamage >= requiredWeaponDamage) return Double.POSITIVE_INFINITY

        val allowedDrop = weapon.damage - requiredWeaponDamage
        val distAdd = allowedDrop / weapon.damageDropPerMeter

        return weapon.range + distAdd
    }
}
