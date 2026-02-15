package br.com.dhionata

import br.com.dhionata.analysis.WeaponAnalyzerService
import br.com.dhionata.calculator.DamageCalculator
import br.com.dhionata.calculator.TTKCalculator
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

    // Composição das dependências (Injeção de Dependência Manual)
    val damageCalculator = DamageCalculator()
    val ttkCalculator = TTKCalculator(damageCalculator)
    val analyzerService = WeaponAnalyzerService(ttkCalculator)
    val weaponPresenter = WeaponPresenter(ttkCalculator)

    try {
        println("Autor: Dhionatã Carlos Vieira\n")

        val formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        println(LocalDateTime.now().format(formatador))

        allList.forEach { set ->
            // Analisar as armas para o conjunto atual
            val fuzileiroAnalyzed = analyzerService.analyzeAll(WeaponRepository.fuzileiroWeapons, set)
            val engenheiroAnalyzed = analyzerService.analyzeAll(WeaponRepository.engenheiroWeapons, set)
            val sniperAnalyzed = analyzerService.analyzeAll(WeaponRepository.sniperWeapons, set)
            val medicAnalyzed = analyzerService.analyzeAll(WeaponRepository.medicWeapons, set)
            val pistolasAnalyzed = analyzerService.analyzeAll(WeaponRepository.pistolas, set)

            weaponPresenter.printDetailedAllWeaponsInfo(
                fuzileiroAnalyzed,
                engenheiroAnalyzed,
                sniperAnalyzed,
                medicAnalyzed,
                pistolasAnalyzed
            )
        }
    } catch (e: Exception) {
        e.printStackTrace(originalOut)
        file.delete()
        println("Arquivo deletado devido à exception")
    } finally {
        System.setOut(originalOut)
        if (file.exists()) {
            println("Relatório gerado em: $desktopPath")
        }
        printStream.close()
    }
}

fun formatValue(value: Double, scale: Int = 2): String {
    if (value.isInfinite()) return "Infinity"
    if (value.isNaN()) return "NaN"
    return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).toString()
}
