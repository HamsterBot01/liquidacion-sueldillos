fun main() {
    inicializarDatos()

    var opcion: Int
    do {
        ConsoleUtils.limpiarPantalla()
        mostrarMenuPrincipal()
        opcion = ConsoleUtils.leerNumeroEntero("Seleccione una opción: ")

        when (opcion) {
            1 -> listarEmpleados()
            2 -> agregarEmpleado()
            3 -> generarLiquidacion()
            4 -> listarLiquidaciones()
            5 -> filtrarEmpleadosPorAFP()
            6 -> eliminarEmpleado()
            7 -> mostrarTotalDescuentosNomina()
            0 -> ConsoleUtils.mostrarMensaje("Saliendo...")
            else -> ConsoleUtils.mostrarMensaje("Opción inválida, intente nuevamente.")
        }

        if (opcion != 0) {
            ConsoleUtils.pausar()
        }
    } while (opcion != 0)
}

fun mostrarMenuPrincipal() {
    ConsoleUtils.mostrarTitulo("SISTEMA DE GESTIÓN DE EMPLEADOS")
    println("1. Listar empleados")
    println("2. Agregar empleado")
    println("3. Generar liquidación por RUT")
    println("4. Listar liquidaciones")
    println("5. Filtrar empleados por AFP")
    println("6. Eliminar empleado")
    println("7. Mostrar total de descuentos de nómina")
    println("0. Salir")
}

fun inicializarDatos() {
    // Inicializamos afps para evitar que el programa no corra.
    Repositorio.afps.addAll(listOf(
        AFP("Capital", 0.114),
        AFP("GusitoX", 0.187),
        AFP("Habitat", 0.113),
        AFP("PlanVital", 0.106),
        AFP("Provida", 0.1105),
        AFP("Modelo", 0.1089)
    ))

    // Empleados de ejemplo.
    val empleadosEjemplo = listOf(
        Empleado(
            rut = "27011177-7",
            nombre = "Gustavito Antonio",
            sueldoBase = 850000.0,
            afp = Repositorio.afps[0],
            direccion = Direccion("Av. Principal", 777, "Santiago", "Metropolitana"),
            bonosImponibles = 50000.0,
            bonosNoImponibles = 20000.0
        ),
        Empleado(
            rut = "15943072-3",
            nombre = "Anakin Skywalker",
            sueldoBase = 4400000.0,
            afp = Repositorio.afps[1],
            direccion = Direccion("Calle oscura", 66, "Tatooine", "Arica y parinacota"),
            bonosImponibles = 89000.0,
            bonosNoImponibles = 22000.0
        ),
        Empleado(
            rut = "20666010-1",
            nombre = "Hatsune Miku",
            sueldoBase = 950000.0,
            afp = Repositorio.afps[2],
            direccion = Direccion("Celestina", 111, "Talca", "Maule"),
            bonosImponibles = 60000.0,
            bonosNoImponibles = 25000.0
        )
    )

    Repositorio.empleados.addAll(empleadosEjemplo)

    // Generamos las liquidaciones del mes para cada empleado.
    empleadosEjemplo.forEach { empleado ->
        val liquidacion = LiquidacionSueldo.generar("Octubre-2025", empleado)
        Repositorio.liquidaciones.add(liquidacion)
    }

    ConsoleUtils.mostrarMensaje("Sistema inicializado con 3 empleados de ejemplo y sus liquidaciones.")
}

fun listarEmpleados() {
    ConsoleUtils.mostrarTitulo("LISTA DE EMPLEADOS")

    if (Repositorio.empleados.isEmpty()) {
        ConsoleUtils.mostrarMensaje("No hay empleados registrados.")
        return
    }

    Repositorio.empleados.forEachIndexed { index, empleado ->
        println("${index + 1}. ${empleado.nombre}")
        println("   RUT: ${empleado.rut}")
        println("   Dirección: ${empleado.direccion.calle} #${empleado.direccion.numero}, ${empleado.direccion.ciudad}, ${empleado.direccion.region}")
        println("   Sueldo Base: $${"%,.0f".format(empleado.sueldoBase)}")
        println("   Bonos Imponibles: $${"%,.0f".format(empleado.bonosImponibles)}")
        println("   Bonos No Imponibles: $${"%,.0f".format(empleado.bonosNoImponibles)}")
        println("   Sueldo Imponible: $${"%,.0f".format(empleado.sueldoImponible())}")
        println("   AFP: ${empleado.afp.nombre}")
        println()
    }
}

fun agregarEmpleado() {
    ConsoleUtils.mostrarTitulo("AGREGAR EMPLEADO")

    val rut = ConsoleUtils.leerTexto("Ingrese RUT: ")
    if (rut.isEmpty()) {
        ConsoleUtils.mostrarMensaje("Error: El RUT no puede estar vacío.")
        return
    }

    if (Repositorio.empleados.any { it.rut == rut }) {
        ConsoleUtils.mostrarMensaje("Error: Ya existe un empleado con ese RUT.")
        return
    }

    val nombre = ConsoleUtils.leerTexto("Ingrese nombre: ")
    val sueldoBase = ConsoleUtils.leerNumeroDecimal("Ingrese sueldo base: ")
    val bonosImponibles = ConsoleUtils.leerNumeroDecimal("Ingrese bonos imponibles: ")
    val bonosNoImponibles = ConsoleUtils.leerNumeroDecimal("Ingrese bonos no imponibles: ")

    val calle = ConsoleUtils.leerTexto("Ingrese calle: ")
    val numero = ConsoleUtils.leerNumeroEntero("Ingrese número: ")
    val ciudad = ConsoleUtils.leerTexto("Ingrese ciudad: ")
    val region = ConsoleUtils.leerTexto("Ingrese región: ")

    ConsoleUtils.mostrarMensaje("\nSeleccione AFP:")
    Repositorio.afps.forEachIndexed { index, afp ->
        println("${index + 1}. ${afp.nombre} (${"%.2f".format(afp.tasa * 100)}%)")
    }

    val afpIndex = ConsoleUtils.leerNumeroEntero("Opción: ") - 1
    val afp = Repositorio.afps.getOrNull(afpIndex) ?: Repositorio.afps.first()

    val direccion = Direccion(calle, numero, ciudad, region)
    val empleado = Empleado(rut, nombre, sueldoBase, afp, direccion, bonosImponibles, bonosNoImponibles)
    Repositorio.empleados.add(empleado)

    ConsoleUtils.mostrarMensaje("Empleado agregado con éxito.")
}

fun generarLiquidacion() {
    ConsoleUtils.mostrarTitulo("GENERAR LIQUIDACIÓN")

    if (Repositorio.empleados.isEmpty()) {
        ConsoleUtils.mostrarMensaje("No hay empleados registrados.")
        return
    }

    val periodo = ConsoleUtils.leerTexto("Ingrese período (ej: Octubre-2025): ")
    val rut = ConsoleUtils.leerTexto("Ingrese RUT del empleado: ")
    val empleado = Repositorio.empleados.find { it.rut == rut }

    if (empleado == null) {
        ConsoleUtils.mostrarMensaje("Empleado no encontrado.")
        return
    }

    val liquidacion = LiquidacionSueldo.generar(periodo, empleado)
    Repositorio.liquidaciones.add(liquidacion)

    ConsoleUtils.mostrarTitulo("LIQUIDACIÓN GENERADA - $periodo")
    println("Empleado: ${empleado.nombre} (${empleado.rut})")
    println("AFP: ${empleado.afp.nombre}")
    println()
    println("INGRESOS:")
    println("  Sueldo Base: $${"%,.0f".format(empleado.sueldoBase)}")
    println("  Bonos Imponibles: $${"%,.0f".format(empleado.bonosImponibles)}")
    println("  Bonos No Imponibles: $${"%,.0f".format(empleado.bonosNoImponibles)}")
    println("  Total Imponible: $${"%,.0f".format(liquidacion.imponible)}")
    println("  Total No Imponible: $${"%,.0f".format(liquidacion.noImponible)}")
    println()
    println("DESCUENTOS:")
    println("  AFP (${"%.2f".format(empleado.afp.tasa * 100)}%): $${"%,.0f".format(liquidacion.descAfp)}")
    println("  Salud (7%): $${"%,.0f".format(liquidacion.descSalud)}")
    println("  Seguro Cesantía (0.6%): $${"%,.0f".format(liquidacion.descCesantia)}")
    println("  TOTAL DESCUENTOS: $${"%,.0f".format(liquidacion.totalDescuentos)}")
    println()
    println("SUELDO LÍQUIDO: $${"%,.0f".format(liquidacion.sueldoLiquido)}")
}

fun listarLiquidaciones() {
    ConsoleUtils.mostrarTitulo("LISTA DE LIQUIDACIONES")

    if (Repositorio.liquidaciones.isEmpty()) {
        ConsoleUtils.mostrarMensaje("No hay liquidaciones generadas.")
        return
    }

    Repositorio.liquidaciones.forEachIndexed { index, liquidacion ->
        println("${index + 1}. Período: ${liquidacion.periodo}")
        println("   Empleado: ${liquidacion.empleado.nombre} (${liquidacion.empleado.rut})")
        println("   Sueldo Líquido: $${"%,.0f".format(liquidacion.sueldoLiquido)}")
        println()
    }
}

fun filtrarEmpleadosPorAFP() {
    ConsoleUtils.mostrarTitulo("FILTRAR EMPLEADOS POR AFP")

    if (Repositorio.empleados.isEmpty()) {
        ConsoleUtils.mostrarMensaje("No hay empleados registrados.")
        return
    }

    ConsoleUtils.mostrarMensaje("AFPs disponibles:")
    Repositorio.afps.forEachIndexed { index, afp ->
        println("${index + 1}. ${afp.nombre}")
    }

    val afpNombre = ConsoleUtils.leerTexto("Ingrese nombre de AFP: ")

    // Filtrar y ordenar por sueldo líquido de mayor a menor
    val empleadosFiltrados = Repositorio.empleados
        .filter { it.afp.nombre.equals(afpNombre, ignoreCase = true) }
        .map { empleado ->
            val liquidacion = LiquidacionSueldo.generar("Temporal", empleado)
            empleado to liquidacion.sueldoLiquido
        }
        .sortedByDescending { it.second }
        .map { it.first }

    if (empleadosFiltrados.isEmpty()) {
        ConsoleUtils.mostrarMensaje("No se encontraron empleados en la AFP '$afpNombre'.")
        return
    }

    ConsoleUtils.mostrarMensaje("Empleados en AFP '$afpNombre' (ordenados por sueldo líquido descendente):")
    empleadosFiltrados.forEach { empleado ->
        val liquidacion = LiquidacionSueldo.generar("Temporal", empleado)
        println("• ${empleado.nombre}")
        println("  RUT: ${empleado.rut}")
        println("  Sueldo Imponible: $${"%,.0f".format(empleado.sueldoImponible())}")
        println("  Sueldo Líquido: $${"%,.0f".format(liquidacion.sueldoLiquido)}")
        println()
    }
}

fun eliminarEmpleado() {
    ConsoleUtils.mostrarTitulo("ELIMINAR EMPLEADO")

    if (Repositorio.empleados.isEmpty()) {
        ConsoleUtils.mostrarMensaje("No hay empleados registrados.")
        return
    }

    val rut = ConsoleUtils.leerTexto("Ingrese RUT del empleado a eliminar: ")
    val eliminado = Repositorio.empleados.removeIf { it.rut == rut }

    if (eliminado) {
        Repositorio.liquidaciones.removeAll { it.empleado.rut == rut }
        ConsoleUtils.mostrarMensaje("Empleado eliminado con éxito.")
    } else {
        ConsoleUtils.mostrarMensaje("No se encontró el empleado.")
    }
}

fun mostrarTotalDescuentosNomina() {
    ConsoleUtils.mostrarTitulo("TOTAL DE DESCUENTOS DE NÓMINA")

    if (Repositorio.liquidaciones.isEmpty()) {
        ConsoleUtils.mostrarMensaje("No hay liquidaciones generadas.")
        return
    }

    val totalDescuentosAFP = Repositorio.liquidaciones.sumOf { it.descAfp }
    val totalDescuentosSalud = Repositorio.liquidaciones.sumOf { it.descSalud }
    val totalDescuentosCesantia = Repositorio.liquidaciones.sumOf { it.descCesantia }
    val totalGeneralDescuentos = Repositorio.liquidaciones.sumOf { it.totalDescuentos }
    val totalSueldosLiquidos = Repositorio.liquidaciones.sumOf { it.sueldoLiquido }

    println("Total descuentos AFP: $${"%,.0f".format(totalDescuentosAFP)}")
    println("Total descuentos Salud: $${"%,.0f".format(totalDescuentosSalud)}")
    println("Total Seguro Cesantía: $${"%,.0f".format(totalDescuentosCesantia)}")
    ConsoleUtils.mostrarSeparador()
    println("TOTAL GENERAL DESCUENTOS: $${"%,.0f".format(totalGeneralDescuentos)}")
    println("TOTAL SUELDOS LÍQUIDOS: $${"%,.0f".format(totalSueldosLiquidos)}")
}