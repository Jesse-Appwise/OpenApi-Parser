package util

fun String.isSnakeCase(): Boolean {
    return this.contains('_')
}

fun String.snakeToCamelCase(): String {
    return removeCharToCamelCase('_')
}

fun String.removeCharToCamelCase(char: Char): String {
    return this.split(char).joinToString(separator = "") { it.replaceFirstChar { it.uppercase() } }.replaceFirstChar { it.lowercase() }
}