package br.com.dhionata.weapon

import br.com.dhionata.formatValue
import kotlin.math.roundToInt

data class Weapon(
    val name: String,
    var damage: Double,
    var fireRateDouble: Double,
    var headMultiplier: Double,
    var bodyMultiplier: Double,
    var range: Double,
    var damageDropPerMeter: Double,
    var minDamage: Double,
    var pellets: Int = 1,
    var spreadMin: Double = 0.0,
    var spreadMax: Double = 0.0,
    var zoomSpreadMin: Double = 0.0,
    var zoomSpreadMax: Double = 0.0,
    var magazineCapacity: Int = 0,
    var reloadTime: Double = 0.0,
    private val mods: MutableSet<String> = mutableSetOf(),
) {

    val fireRate: Int
        get() = fireRateDouble.roundToInt()

    override fun toString(): String {
        return "Nome: $name | Dano: ${formatValue(damage)}"
    }

    private fun addMod(modifier: WeaponModifier): Weapon {
        modifier.apply(this)
        return this
    }

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
        magazineCapacityAdd: Int? = null,
        reloadTimeAddPercentage: Double? = null,
    ): Weapon {
        val modifiers = mutableListOf<WeaponModifier>()

        if (pellets != null) modifiers.add(PelletsModifier(pellets))
        if (pelletsAdd != null) modifiers.add(PelletsAddModifier(pelletsAdd))
        if (fireRateAddPercentage != null) modifiers.add(FireRateModifier(fireRateAddPercentage))
        if (damageAdd != null) modifiers.add(DamageModifier(damageAdd))
        if (bodyMultiplierAddPercentage != null) modifiers.add(BodyMultiplierModifier(bodyMultiplierAddPercentage))
        if (headMultiplierAddPercentage != null) modifiers.add(HeadMultiplierModifier(headMultiplierAddPercentage))
        if (rangeAdd != null) modifiers.add(RangeModifier(rangeAdd))
        if (damageDropPerMeterAddPercentage != null) modifiers.add(DamageDropModifier(damageDropPerMeterAddPercentage))
        if (damageAddDropPerMeter != null) modifiers.add(object : WeaponModifier {
            override fun apply(weapon: Weapon) {
                weapon.damageDropPerMeter += damageAddDropPerMeter
            }
        })
        if (spreadAddPercentage != null) modifiers.add(SpreadModifier(spreadAddPercentage))
        if (zoomSpreadAddPercentage != null) modifiers.add(ZoomSpreadModifier(zoomSpreadAddPercentage))
        if (minDamageAdd != null) modifiers.add(MinDamageModifier(minDamageAdd))
        if (magazineCapacityAdd != null) modifiers.add(MagazineCapacityModifier(magazineCapacityAdd))
        if (reloadTimeAddPercentage != null) modifiers.add(ReloadTimeModifier(reloadTimeAddPercentage))

        addMod(CompositeModifier(modifiers))

        if (name.isNotBlank()) {
            mods.add(name)
        }

        return this
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
        magazineCapacityAdd: Int? = null,
        reloadTimeAddPercentage: Double? = null,
    ): Weapon {
        return addMods(
            name, fireRateAddPercentage, damageAdd, headMultiplierAddPercentage, bodyMultiplierAddPercentage,
            rangeAdd, damageDropPerMeterAddPercentage, damageAddDropPerMeter, spreadAddPercentage,
            zoomSpreadAddPercentage, pellets, pelletsAdd, minDamageAdd, magazineCapacityAdd, reloadTimeAddPercentage
        )
    }

    fun getEffectiveDamage(distance: Double): Double {
        if (distance <= range) {
            return damage
        }
        val damageDrop = (distance - range) * damageDropPerMeter
        val effectiveDamage = damage - damageDrop
        return effectiveDamage.coerceAtLeast(minDamage)
    }
}
