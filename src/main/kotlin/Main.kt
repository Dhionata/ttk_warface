import WeaponPresenter.printDetailedAllWeaponsInfo
import WeaponRepository.engenheiroWeapons
import WeaponRepository.fuzileiroWeapons
import WeaponRepository.pistolas
import WeaponRepository.sniperWeapons
import br.com.dhionata.Set

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
        fuzileiroWeapons.forEach { it.set = set }
        engenheiroWeapons.forEach { it.set = set }
        sniperWeapons.forEach { it.set = set }
        pistolas.forEach { it.set = set }
        printDetailedAllWeaponsInfo(fuzileiroWeapons, engenheiroWeapons, sniperWeapons, pistolas)
    }
}
