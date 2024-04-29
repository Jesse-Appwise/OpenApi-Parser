package util

fun String.isSnakeCase(): Boolean {
    return this.contains('_')
}

fun String.snakeToCamelCase(): String {
    return removeCharToCamelCase('_')
}

fun String.removeCharToCamelCase(char: Char): String {
    return this.split(char).joinToString(separator = "") { it.replaceFirstChar { it.uppercase() } }
        .replaceFirstChar { it.lowercase() }
}

fun String.toModelClassName(): String {
    return this.snakeToCamelCase().replaceFirstChar(Char::uppercaseChar).apply {
        return if (endsWith("response", true) || endsWith("request", true)) {
            this
        } else {
            this.plus("Dto")
        }
    }
}