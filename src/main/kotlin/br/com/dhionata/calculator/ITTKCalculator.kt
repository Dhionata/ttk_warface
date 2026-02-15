package br.com.dhionata.calculator

import br.com.dhionata.Set
import br.com.dhionata.weapon.Weapon

interface ITTKCalculator {
    fun calculateTTK(
        weapon: Weapon,
        targetSet: Set,
        isHeadshot: Boolean,
        distance: Double = 0.0,
        isAiming: Boolean = false,
        debug: Boolean = false
    ): Pair<Int, Double>

    fun calculateMaxDistanceForKill(
        weapon: Weapon,
        targetSet: Set,
        isHeadshot: Boolean
    ): Double
}
