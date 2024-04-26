package data.encoder.kotlin.model

import model.path.Path
import java.io.File

fun Map<String, Path>.writeKotlinRetrofitService(outputPath: String, name: String = "Api") {
    val fileName = name.replaceFirstChar(Char::uppercaseChar) + "Service"

    val filePath = buildString {
        append("$outputPath/")
        append("$fileName.kt")
    }

    val file = File(filePath).apply {
        if (exists()) {
            delete()
        }
        parentFile.mkdirs()
        createNewFile()
    }

    println("Writing file: $filePath")

    val content = toKotlinRetrofitCalls(fileName)
    file.writeText(content)
}

fun Map<String, Path>.toKotlinRetrofitCalls(name: String) = buildString {

    appendLine("interface $name {\n")

    this@toKotlinRetrofitCalls.forEach { (key, value) ->
        append(value.toKotlinRetrofitCalls("$key"))
    }

    appendLine("}")
}

fun Path.toKotlinRetrofitCalls(path: String) = buildString {
    this@toKotlinRetrofitCalls.get?.let {
        append(it.toKotlinRetrofitServiceCall(Path.Method.GET, "$path"))
        append("\n\n")
    }
    this@toKotlinRetrofitCalls.post?.let {
        append(it.toKotlinRetrofitServiceCall(Path.Method.POST, "$path"))
        append("\n\n")
    }
    this@toKotlinRetrofitCalls.put?.let {
        append(it.toKotlinRetrofitServiceCall(Path.Method.PUT, "$path"))
        append("\n\n")
    }
    this@toKotlinRetrofitCalls.delete?.let {
        append(it.toKotlinRetrofitServiceCall(Path.Method.DELETE, "$path"))
        append("\n\n")
    }
}