package br.com.dhionata.analysis

import br.com.dhionata.Set
import br.com.dhionata.calculator.ITTKCalculator
import br.com.dhionata.weapon.Weapon

class WeaponAnalyzerService(
    private val ttkCalculator: ITTKCalculator,
) {

    private fun analyze(weapon: Weapon, targetSet: Set): AnalyzedWeapon {
        val ttkHead = ttkCalculator.calculateTTK(weapon, targetSet, true)
        val ttkBody = ttkCalculator.calculateTTK(weapon, targetSet, false)

        val avgShots = (ttkHead.first + ttkBody.first) / 2
        val avgTime = (ttkHead.second + ttkBody.second) / 2
        val ttkAverage = Pair(avgShots, avgTime)

        return AnalyzedWeapon(
            weapon = weapon,
            targetSet = targetSet,
            ttkHead = ttkHead,
            ttkBody = ttkBody,
            ttkAverage = ttkAverage
        )
    }

    fun analyzeAll(weapons: List<Weapon>, targetSet: Set): List<AnalyzedWeapon> {
        return weapons.map { analyze(it, targetSet) }
    }
}
