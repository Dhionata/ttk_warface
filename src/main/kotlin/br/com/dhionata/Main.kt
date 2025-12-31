package br.com.dhionata

import br.com.dhionata.weapon.WeaponPresenter
import br.com.dhionata.weapon.WeaponRepository
import java.io.File
import java.io.PrintStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val setListOfCoop: List<Set> = listOf(
        Set.SetsAndEnemy.`Assault, CQB & Sniper`,
        Set.SetsAndEnemy.Demoman,
        Set.SetsAndEnemy.`G15 Pteranodon`,
        Set.SetsAndEnemy.`Heavy Gunner`,
        Set.SetsAndEnemy.`Spec-Ops`,
        Set.SetsAndEnemy.`SWAT Heavy Gunner`,
        Set.SetsAndEnemy.`Alpha, Beta e Omega`,
        Set.SetsAndEnemy.Screamer,
        Set.SetsAndEnemy.`Fast Hybrid`
    )

    val setListPvp: List<Set> = listOf(
        Set.SetsAndEnemy.Sirocco, Set.SetsAndEnemy.Nord
    )

    val allList = setListPvp + setListOfCoop

    // Redirecionar saída para arquivo na área de trabalho
    val desktopPath = System.getProperty("user.home") + "/Desktop/TTK.txt"
    val file = File(desktopPath)
    val printStream = PrintStream(file)
    val originalOut = System.out
    System.setOut(printStream)

    try {
        println("Autor: Dhionatã Carlos Vieira\n")

        val formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        println(LocalDateTime.now().format(formatador))

        allList.forEach { set ->
            WeaponRepository.fuzileiroWeapons.forEach { it.set = set }
            WeaponRepository.engenheiroWeapons.forEach { it.set = set }
            WeaponRepository.sniperWeapons.forEach { it.set = set }
            WeaponRepository.medicWeapons.forEach { it.set = set }
            WeaponRepository.pistolas.forEach { it.set = set }

            WeaponPresenter.printDetailedAllWeaponsInfo(
                WeaponRepository.fuzileiroWeapons,
                WeaponRepository.engenheiroWeapons,
                WeaponRepository.sniperWeapons,
                WeaponRepository.medicWeapons,
                WeaponRepository.pistolas
            )
        }
    } catch (e: Exception) {
        e.printStackTrace(originalOut)
        file.delete()
        println("Arquivo deletado devido à exception")
    } finally {
        // Restaurar saída original e fechar stream
        System.setOut(originalOut)
        if (file.exists()) {
            println("Relatório gerado em: $desktopPath")
        }
        printStream.close()
    }
}

fun formatValue(value: Double, scale: Int = 2): String {
    if (value.isInfinite() || value.isNaN()) return "Infinito"
    return BigDecimal(value).setScale(scale, RoundingMode.HALF_UP).toString()
}
