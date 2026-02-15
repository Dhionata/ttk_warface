package br.com.dhionata.analysis

import br.com.dhionata.Set
import br.com.dhionata.weapon.Weapon

data class AnalyzedWeapon(
    val weapon: Weapon,
    val targetSet: Set,
    val ttkHead: Pair<Int, Double>,
    val ttkBody: Pair<Int, Double>,
    val ttkAverage: Pair<Int, Double>
)
