import br.com.dhionata.Set
import br.com.dhionata.TTKCalculator
import br.com.dhionata.Weapon
import org.junit.jupiter.api.Test

class TTKCalculatorTest {

    @Test
    fun bulletsToKillWithProtection() {
        TTKCalculator.bulletsToKillWithProtection(Weapon.fuzileiroWeapons.find { it.name == "PKM Zenit (Mod Cadência e Cadência [especial]" }!!, Set.sirocco, true, true)
    }
}
