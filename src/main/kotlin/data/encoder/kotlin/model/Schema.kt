package data.encoder.kotlin.model

import model.schema.Schema
import util.isSnakeCase
import util.snakeToCamelCase
import java.io.File

/**
 * Creates a kotlin representation of the default value for the given schema type.
 *
 * @return The default value for the given schema type.
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
 * Creates a kotlin representation of the class type.
 *
 * @param knownObjectClassName The name of the known object class.
 * @return The kotlin representation of the type for the given schema.
 */
fun Schema.toKotlinType(knownObjectClassName: String? = null): String {
    return when (type) {
        Schema.Types.string -> "String"
        Schema.Types.number -> "Double"
        Schema.Types.integer -> "Int"
        Schema.Types.boolean -> "Boolean"
        Schema.Types.`object` -> knownObjectClassName ?: "Any"
        Schema.Types.array -> "List<${items?.`$ref`?.split('/')?.lastOrNull()?.plus("Dto")}>"
        else -> knownObjectClassName ?: "Any"
    }
}

fun Schema.toKotlinInheritance(): String {
    return inheritedSchemes.mapNotNull { it.`$ref`?.split('/')?.lastOrNull() }
        .joinToString(separator = ", ", postfix = "()")
}

/**
 * Creates a kotlin representation of the property.
 *
 * @return The kotlin representation of the property.
 */
fun Pair<String, Schema>.toKotlinProperty(): String {
    val (name, schema) = this

    val isSnakeCase = name.isSnakeCase()
    val camelSafeName = if (isSnakeCase) name.snakeToCamelCase() else name

    val serializeAnnotation = if (isSnakeCase) "@SerializedName(\"$name\") " else ""

    val type = schema.toKotlinType(name.snakeToCamelCase().replaceFirstChar { it.uppercase() }.plus("Dto"))
    val nullable = if (schema.type == Schema.Types.`object`) "?" else ""

    return "${serializeAnnotation}val $camelSafeName: $type$nullable = ${schema.type?.toKotlinDefaultValue()}"
}

/**
 * Creates a kotlin representation of the class.
 *
 * @param name The name of the class.
 * @return
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

fun Schema.writeKotlinDtoClass(path: String, name: String) {
    val nestedProperties = properties.filter { it.value.type == Schema.Types.`object` }

    val className = name.replaceFirstChar { it.uppercase() } + "Dto"

    val filePath = buildString {
        append("$path/")
        if (nestedProperties.isNotEmpty()) {
            append("$name/")
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
        schema.writeKotlinDtoClass("$path/$name", propertyName)
    }
}

private fun StringBuilder.description(schema: Schema) {
    schema.description?.let {
        appendLine("/**")
        appendLine(" * $it")
        appendLine(" */")
    }
}

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
        append(it)
        appendLine()
        appendLine("}")
    }
}

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

private fun StringBuilder.inheritance(schema: Schema) {
    if (schema.inheritedSchemes.isNotEmpty()) {
        append(" : ")
        append(schema.toKotlinInheritance())
    }
}
