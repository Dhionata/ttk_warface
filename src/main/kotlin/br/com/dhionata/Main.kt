package br.com.dhionata

import br.com.dhionata.weapon.WeaponPresenter
import br.com.dhionata.weapon.WeaponRepository

fun main() {
    val setListOfCoop: List<Set> = listOf(
        Set.SetsAndEnemy.`Assault, CQB & Sniper`,
        Set.SetsAndEnemy.Demoman,
        Set.SetsAndEnemy.`G15 Pteranodon`,
        Set.SetsAndEnemy.`Heavy Gunner`,
        Set.SetsAndEnemy.`Spec-Ops`,
        Set.SetsAndEnemy.`SWAT Heavy Gunner`,
        Set.SetsAndEnemy.`Alpha, Beta e Omega`,
        Set.SetsAndEnemy.Screamer
    )

    val setListPvp: List<Set> = listOf(
        Set.SetsAndEnemy.Sirocco, Set.SetsAndEnemy.Nord
    )

    val allList = setListPvp + setListOfCoop

    allList.forEach { set ->
        WeaponRepository.fuzileiroWeapons.forEach { it.set = set }
        WeaponRepository.engenheiroWeapons.forEach { it.set = set }
        WeaponRepository.sniperWeapons.forEach { it.set = set }
        WeaponRepository.pistolas.forEach { it.set = set }
        WeaponPresenter.printDetailedAllWeaponsInfo(
            WeaponRepository.fuzileiroWeapons,
            WeaponRepository.engenheiroWeapons,
            WeaponRepository.sniperWeapons,
            WeaponRepository.pistolas
        )
    }
}
