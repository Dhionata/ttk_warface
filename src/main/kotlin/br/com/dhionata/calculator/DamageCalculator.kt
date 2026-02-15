package br.com.dhionata.calculator

import br.com.dhionata.Set
import br.com.dhionata.weapon.Weapon
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.tan

class DamageCalculator : IDamageCalculator {

    companion object {
        private const val HEAD_RADIUS = 0.13
        private const val BODY_RADIUS = 0.35
        private const val SPREAD_DENSITY_FACTOR = 3.0
    }

    override fun calculateDamagePerShot(
        weapon: Weapon,
        targetSet: Set,
        distance: Double,
        isHeadshot: Boolean,
        isAiming: Boolean,
    ): Int {
        val equipmentProtection = if (isHeadshot) targetSet.headProtection else targetSet.bodyProtection
        val baseMultiplier = if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier

        val damageMultiplier = (baseMultiplier * targetSet.entityDmgMult * (1 + targetSet.cyborgDmgBuff)) - equipmentProtection

        val effectiveDamage = if (distance > 0) weapon.getEffectiveDamage(distance) else weapon.damage

        val totalPellets = weapon.pellets
        val damagePerPellet = effectiveDamage / totalPellets

        val singlePelletDamage = calculateSinglePelletDamage(
            damagePerPellet,
            damageMultiplier,
            targetSet.absorption,
            totalPellets,
            targetSet.resistance,
            targetSet.weaponTypeResistance
        )

        val hitRate = if (totalPellets > 1) {
            val shouldUseScope = isAiming && weapon.zoomSpreadMin > 0
            val usedSpread = if (shouldUseScope) weapon.zoomSpreadMin else weapon.spreadMin
            calculateHitRate(distance, usedSpread, isHeadshot)
        } else {
            1.0
        }

        val rawDamage = singlePelletDamage * totalPellets * hitRate

        // Penalidade de membros a longa distância para shotguns
        val limbPenalty = if (distance > 15.0 && totalPellets > 1) 0.75 else 1.0

        return (rawDamage * limbPenalty).roundToInt()
    }

    private fun calculateSinglePelletDamage(
        weaponDamagePerPellet: Double,
        damageMultiplier: Double,
        absorption: Double,
        pellets: Int,
        resistance: Double,
        weaponTypeResistance: Double,
    ): Double {
        val effectiveAbsorption = absorption / pellets.toDouble()
        val baseDamage = (weaponDamagePerPellet * damageMultiplier) - effectiveAbsorption

        if (baseDamage <= 0) return 0.0

        return baseDamage * (1 - resistance) * (1 - weaponTypeResistance)
    }

    private fun calculateHitRate(
        distance: Double,
        spreadDegrees: Double,
        isHeadshot: Boolean,
    ): Double {
        if (distance <= 1.5) return 1.0
        if (spreadDegrees <= 0.0) return 1.0

        val effectiveAngle = spreadDegrees / SPREAD_DENSITY_FACTOR
        val spreadRadius = distance * tan(Math.toRadians(effectiveAngle))
        val targetRadius = if (isHeadshot) HEAD_RADIUS else BODY_RADIUS

        if (targetRadius >= spreadRadius) return 1.0

        val hitRatio = (targetRadius * targetRadius) / (spreadRadius * spreadRadius)
        return min(1.0, max(0.0, hitRatio))
    }
}
