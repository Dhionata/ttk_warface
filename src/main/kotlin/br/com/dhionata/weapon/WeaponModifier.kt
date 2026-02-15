package br.com.dhionata.weapon

interface WeaponModifier {
    fun apply(weapon: Weapon)
}

class DamageModifier(private val damageAdd: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.damage += damageAdd
    }
}

class RangeModifier(private val rangeAdd: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.range += rangeAdd
    }
}

class FireRateModifier(private val percentage: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.fireRateDouble += weapon.fireRateDouble * (percentage / 100.0)
    }
}

class MagazineCapacityModifier(private val capacityAdd: Int) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.magazineCapacity += capacityAdd
    }
}

class ReloadTimeModifier(private val percentage: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.reloadTime += weapon.reloadTime * (percentage / 100.0)
    }
}

class SpreadModifier(private val percentage: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.spreadMin += weapon.spreadMin * (percentage / 100.0)
        weapon.spreadMax += weapon.spreadMax * (percentage / 100.0)
    }
}

class ZoomSpreadModifier(private val percentage: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.zoomSpreadMin += weapon.zoomSpreadMin * (percentage / 100.0)
        weapon.zoomSpreadMax += weapon.zoomSpreadMax * (percentage / 100.0)
    }
}

class BodyMultiplierModifier(private val percentage: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.bodyMultiplier += weapon.bodyMultiplier * (percentage / 100.0)
    }
}

class HeadMultiplierModifier(private val percentage: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.headMultiplier += weapon.headMultiplier * (percentage / 100.0)
    }
}

class DamageDropModifier(private val percentage: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.damageDropPerMeter += weapon.damageDropPerMeter * (percentage / 100.0)
    }
}

class MinDamageModifier(private val damageAdd: Double) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.minDamage += damageAdd
    }
}

class PelletsModifier(private val pellets: Int) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        weapon.pellets = pellets
    }
}

class PelletsAddModifier(private val pelletsAdd: Int) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        if (weapon.pellets > 0) {
            val oldPellets = weapon.pellets
            val newPellets = oldPellets + pelletsAdd
            weapon.damage = weapon.damage / oldPellets * newPellets
            weapon.pellets = newPellets
        }
    }
}

class CompositeModifier(private val modifiers: List<WeaponModifier>) : WeaponModifier {
    override fun apply(weapon: Weapon) {
        modifiers.forEach { it.apply(weapon) }
    }
}
