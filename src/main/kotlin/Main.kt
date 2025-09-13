package br.com.dhionata

import br.com.dhionata.Weapon.WeaponsLists.engenheiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.fuzileiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.pistolas
import br.com.dhionata.Weapon.WeaponsLists.sniperWeapons
import br.com.dhionata.WeaponPresenter.printDetailedAllWeaponsInfo

fun main() {
    val setListOfCoop: List<Set> = listOf(
        Set.`Assault, CQB & Sniper`,
        Set.Demoman,
        Set.`G15 Pteranodon`,
        Set.`Heavy Gunner`,
        Set.`Spec-Ops`,
        Set.`SWAT Heavy Gunner`,
        Set.`Alpha, Beta e Omega`,
        Set.Screamer
    )

    val setListPvp: List<Set> = listOf(
        Set.Sirocco, Set.Nord
    )

    val allList = setListPvp + setListOfCoop

    allList.forEach { set ->
        fuzileiroWeapons.forEach { it.set = set }
        engenheiroWeapons.forEach { it.set = set }
        sniperWeapons.forEach { it.set = set }
        pistolas.forEach { it.set = set }
        printDetailedAllWeaponsInfo(fuzileiroWeapons, engenheiroWeapons, sniperWeapons, pistolas)
    }
}
