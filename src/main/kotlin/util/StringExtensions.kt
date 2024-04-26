package util

fun String.isSnakeCase(): Boolean {
    return this.contains('_')
}

fun String.snakeToCamelCase(): String {
    return this.split('_').joinToString(separator = "") { it.replaceFirstChar { it.uppercase() } }.replaceFirstChar { it.lowercase() }
}