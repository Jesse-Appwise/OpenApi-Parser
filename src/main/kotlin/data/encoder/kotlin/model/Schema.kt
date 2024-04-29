package data.encoder.kotlin.model

import model.schema.Schema
import util.isSnakeCase
import util.snakeToCamelCase
import util.toModelClassName
import java.io.File

/**
 * Converts the Schema.Types to their respective Kotlin default values.
 *
 * @return The Kotlin default value as a String.
 */
fun Schema.Types.toKotlinDefaultValue(): String {
    return when (this) {
        Schema.Types.string -> "\"\""
        Schema.Types.number -> "0.0"
        Schema.Types.integer -> "0"
        Schema.Types.boolean -> "false"
        Schema.Types.`object` -> "null"
        Schema.Types.array -> "emptyList()"
        Schema.Types.`null` -> "null"
    }
}

/**
 * Converts the Schema.Types to their respective Kotlin types.
 *
 * @param knownObjectClassName The known object class name, if any.
 * @return The Kotlin type as a String.
 */
fun Schema.toKotlinType(knownObjectClassName: String? = null): String {

    val objectType = knownObjectClassName ?: "Any"

    return when (type) {
        Schema.Types.string -> "String"
        Schema.Types.number -> "Double"
        Schema.Types.integer -> "Int"
        Schema.Types.boolean -> "Boolean"
        Schema.Types.`object` -> objectType
        Schema.Types.array -> "List<$objectType>"
        else -> objectType
    }
}

/**
 * Converts the inherited schemas to their respective Kotlin inheritance.
 *
 * @return The Kotlin inheritance as a String.
 */
fun Schema.toKotlinInheritance(): String {
    if (inheritedSchemes.isEmpty()) return ""

    return inheritedSchemes.mapNotNull { it.`$ref`?.split('/')?.lastOrNull() }
        .joinToString(separator = ", ", postfix = "()")
}

/**
 * Converts the pair of property name and schema to a Kotlin property.
 *
 * @return The Kotlin property as a String.
 */
fun Pair<String, Schema>.toKotlinProperty(): String {
    val (name, schema) = this

    val isSnakeCase = name.isSnakeCase()
    val camelSafeName = if (isSnakeCase) name.snakeToCamelCase() else name

    val serializeAnnotation = if (isSnakeCase) "@SerializedName(\"$name\") " else ""

    val type = schema.toKotlinType(name.toModelClassName())
    val nullable = if (schema.type == Schema.Types.`object`) "?" else ""

    return "${serializeAnnotation}val $camelSafeName: $type$nullable = ${schema.type?.toKotlinDefaultValue()}"
}

/**
 * Converts the schema to a Kotlin class.
 *
 * @param name The name of the class.
 * @return The Kotlin class as a String.
 */
fun Schema.toKotlinClass(name: String): String {

    return buildString {

        description(this@toKotlinClass)

        clazz(
            name = name,
            properties = {
                properties(this@toKotlinClass)
            },
            inheritance = {
                inheritance(this@toKotlinClass)
            },
            content = null
        )

        appendLine()
    }
}

/**
 * Writes the schema as a Kotlin class to a file.
 *
 * @param path The path where the file should be written.
 * @param name The name of the class.
 */
fun Schema.writeKotlinSchemaClass(path: String, name: String) {

    val nestedProperties = properties.filter { it.value.type == Schema.Types.`object` }

    val isDto = !name.endsWith("response", ignoreCase = true) && !name.endsWith("request", ignoreCase = true)

    var className = name.toModelClassName()

    val actualPath = if (path.endsWith("/dto") && !isDto){
        when {
            name.endsWith("response") -> path.replace("/dto", "/response")
            name.endsWith("request") -> path.replace("/dto", "/request")
            else -> path
        }
    } else path

    val filePath = buildString {
        append("$actualPath/")
        if (nestedProperties.isNotEmpty()) {
            append("${name.lowercase()}/")
        }
        append("$className.kt")
    }

    val file = File(filePath).apply {
        if (exists()) {
            delete()
        }
        parentFile.mkdirs()
        createNewFile()
    }

    println("Writing file: $filePath")

    val content = toKotlinClass(className)
    file.writeText(content)

    properties.filter { it.value.type == Schema.Types.`object` }.forEach { (propertyName, schema) ->
        schema.writeKotlinSchemaClass("$actualPath/${name.lowercase()}", propertyName)
    }
}

/**
 * Appends the schema's description to the StringBuilder.
 *
 * @param schema The schema whose description should be appended.
 */
private fun StringBuilder.description(schema: Schema) {
    schema.description?.let {
        appendLine("/**")
        appendLine("* $it")
        appendLine("*/")
    }
}

/**
 * Appends a Kotlin class to the StringBuilder.
 *
 * @param name The name of the class.
 * @param properties The properties of the class.
 * @param inheritance The inheritance of the class.
 * @param content The content of the class.
 */
private fun StringBuilder.clazz(
    name: String,
    properties: StringBuilder.() -> Unit,
    inheritance: StringBuilder.() -> Unit = {},
    content: (StringBuilder.() -> Unit)? = null
) {
    append("data class $name(")
    properties()
    append(")")

    inheritance()

    content?.let {
        appendLine("{")
        appendLine()
        append(it.invoke(this))
        appendLine()
        appendLine("}")
    }
}

/**
 * Appends the schema's properties to the StringBuilder.
 *
 * @param schema The schema whose properties should be appended.
 */
private fun StringBuilder.properties(schema: Schema) {
    if (schema.properties.isNotEmpty()) {
        appendLine()

        val propertyLines = schema
            .properties
            .map { (key, value) ->
                (key to value).toKotlinProperty()
            }.joinToString(separator = ",\n\t", prefix = "\t")

        appendLine(propertyLines)
    }

    val extraProperties = schema
        .allOf
        .filter { it.type == Schema.Types.`object` }
        .flatMap { it.properties.entries }

    if (extraProperties.isNotEmpty()) {
        appendLine()
        val propertyLines = extraProperties
            .joinToString(separator = ",\n\t", prefix = "\t") { (key, value) ->
                (key to value).toKotlinProperty()
            }

        appendLine(propertyLines)
    }
}

/**
 * Appends the schema's inheritance to the StringBuilder.
 *
 * @param schema The schema whose inheritance should be appended.
 */
private fun StringBuilder.inheritance(schema: Schema) {
    if (schema.inheritedSchemes.isNotEmpty()) {
        append(" : ")
        append(schema.toKotlinInheritance())
    }
}
