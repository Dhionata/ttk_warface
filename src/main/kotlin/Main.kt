package br.com.dhionata

import br.com.dhionata.Weapon.WeaponsLists.engenheiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.fuzileiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.pistolas
import br.com.dhionata.WeaponPresenter.printDetailedAllWeaponsInfo

fun main() {
    val setList: List<Set> = listOf(Set.sirocco, Set.nord)

    setList.forEach { set ->
        fuzileiroWeapons.forEach { it.set = set }
        engenheiroWeapons.forEach { it.set = set }
        pistolas.forEach { it.set = set }
        printDetailedAllWeaponsInfo(fuzileiroWeapons, engenheiroWeapons, pistolas, set)
    }
}
