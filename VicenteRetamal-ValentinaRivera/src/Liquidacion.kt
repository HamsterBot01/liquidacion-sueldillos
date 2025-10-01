data class LiquidacionSueldo(
    val periodo: String,
    val empleado: Empleado,
    val imponible: Double,
    val noImponible: Double,
    val descAfp: Double,
    val descSalud: Double,
    val descCesantia: Double,
    val totalDescuentos: Double,
    val sueldoLiquido: Double
) {
    companion object {
        fun generar(periodo: String, empleado: Empleado): LiquidacionSueldo {
            val imponible = empleado.sueldoImponible()
            val noImponible = empleado.bonosNoImponibles

            val descAfp = imponible * empleado.afp.tasa
            val descSalud = imponible * 0.07
            val descCesantia = imponible * 0.006
            val totalDescuentos = descAfp + descSalud + descCesantia
            val sueldoLiquido = (imponible + noImponible) - totalDescuentos

            return LiquidacionSueldo(
                periodo = periodo,
                empleado = empleado,
                imponible = imponible,
                noImponible = noImponible,
                descAfp = descAfp,
                descSalud = descSalud,
                descCesantia = descCesantia,
                totalDescuentos = totalDescuentos,
                sueldoLiquido = sueldoLiquido
            )
        }
    }
}