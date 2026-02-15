package br.com.dhionata.calculator

import br.com.dhionata.Set
import br.com.dhionata.weapon.Weapon

interface IDamageCalculator {
    fun calculateDamagePerShot(
        weapon: Weapon,
        targetSet: Set,
        distance: Double,
        isHeadshot: Boolean,
        isAiming: Boolean
    ): Int
}
