import data.decoder.YamlDecoder
import data.encoder.kotlin.KotlinNetworkEncoder
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {

    require(args.size == 2) { "Usage: <input file> <output file>" }

    val inputFilePath = args[0]
    val outputFilePath = args[1]

    Files.newInputStream(Paths.get(inputFilePath)).use { inputStream ->
        val apiDescription = YamlDecoder().decode(inputStream)

        KotlinNetworkEncoder(apiDescription, outputFilePath).encode()
    }

}