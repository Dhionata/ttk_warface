package br.com.dhionata

import br.com.dhionata.Weapon.WeaponsLists.engenheiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.fuzileiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.pistolas
import br.com.dhionata.WeaponPresenter.printDetailedAllWeaponsInfo

fun main() {
    val setListOfCoop: List<Set> = listOf(
        Set.`Assault (CO-OP), CQB (CO-OP) & Sniper (CO-OP)`,
        Set.`Demoman (CO-OP)`,
        Set.`G15 Pteranodon`,
        Set.`Heavy Gunner (CO-OP)`,
        Set.`Spec-Ops (CO-OP)`,
        Set.`SWAT Heavy Gunner`
    )

    val setListPvp: List<Set> = listOf(
        Set.sirocco, Set.nord
    )

    val allList = setListPvp + setListOfCoop

    allList.forEach { set ->
        fuzileiroWeapons.forEach { it.set = set }
        engenheiroWeapons.forEach { it.set = set }
        pistolas.forEach { it.set = set }
        printDetailedAllWeaponsInfo(fuzileiroWeapons, engenheiroWeapons, pistolas)
    }
}
