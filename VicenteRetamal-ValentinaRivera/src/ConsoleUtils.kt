object ConsoleUtils {

    fun mostrarMensaje(mensaje: String) {
        println(mensaje)
    }

    fun leerTexto(mensaje: String): String {
        print(mensaje)
        return readLine() ?: ""
    }

    fun leerNumeroEntero(mensaje: String): Int {
        print(mensaje)
        return readLine()?.toIntOrNull() ?: 0
    }

    fun leerNumeroDecimal(mensaje: String): Double {
        print(mensaje)
        return readLine()?.toDoubleOrNull() ?: 0.0
    }

    fun limpiarPantalla() {
        repeat(25) { println() }
    }

    fun mostrarSeparador() {
        println("=".repeat(25))
    }

    fun mostrarTitulo(titulo: String) {
        mostrarSeparador()
        println(titulo)
        mostrarSeparador()
    }


    fun pausar() {
        println("\nIngrese un caracter cualquiera para continuar: ")
        readLine()
    }
}