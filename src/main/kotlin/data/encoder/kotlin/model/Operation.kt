package data.encoder.kotlin.model

import com.cesarferreira.pluralize.singularize
import model.path.Operation
import model.path.Path

fun Operation.toKotlinRetrofitServiceCall(
    method: Path.Method,
    path: String
): String {
    val functionName = createFunctionName(path, method)

    val builder = StringBuilder()
    builder.append("    @${method}(\"${path}\")\n")
    builder.append("    suspend fun ${functionName}(")

    this.parameters.forEachIndexed { index, parameter ->
        builder.append(parameter.toKotlinRetrofitParameter())
        if (index < this.parameters.size - 1) {
            builder.append(", ")
        }
    }

    val responseSchema = this.successResponse?.jsonResponseSchema
    val possibleResponseClass = responseSchema?.`$ref`?.split("/")?.lastOrNull()
    builder.append("): Response<${responseSchema?.toKotlinType(possibleResponseClass)}>")
    return builder.toString()
}


private fun createFunctionName(path: String, method: Path.Method) = buildString {
    append(method.name.lowercase())
    val pathParts = path.split("/")
        .filter { it.isNotEmpty() && !it.matches("v\\d+".toRegex()) }

    pathParts.forEachIndexed { index, part ->
        when {
            part.contains("{") -> {
                // do nothing
            }
            index < pathParts.size - 1 && (pathParts.getOrNull(index + 1)?.contains("{") != false) -> {
                append(part.singularize().replaceFirstChar(Char::titlecase))
            }
            else -> {
                append(part.replaceFirstChar(Char::titlecase))
            }
        }
    }
}