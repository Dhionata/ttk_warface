package br.com.dhionata

import br.com.dhionata.Weapon.WeaponsLists.engenheiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.fuzileiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.pistolas
import br.com.dhionata.WeaponPresenter.printDetailedAllWeaponsInfo

fun main() {
    val setList: List<Set> = listOf(Set.sirocco, Set.nord)

    setList.forEach {
        fuzileiroWeapons.forEach { it.set = Set.nord }
        engenheiroWeapons.forEach { it.set = Set.nord }
        printDetailedAllWeaponsInfo(fuzileiroWeapons, engenheiroWeapons, pistolas, Set.sirocco)
    }
}
