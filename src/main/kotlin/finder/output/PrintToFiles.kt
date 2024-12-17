package finder.output

import com.google.gson.Gson
import finder.DuplicateFinderOptions
import finder.DuplicateFinderReport
import finder.indexing.Chunk
import finder.similarity.similarity
import java.nio.file.*
import kotlin.io.path.writeText

fun printToFiles(
    report: DuplicateFinderReport,
    options: DuplicateFinderOptions
) {
    Files.createDirectories(options.outputDirectory)

    val processedChunks = mutableSetOf<Chunk>()
    val jsonDocs = mutableListOf<Doc>()

    report.duplicates.toList().distinctBy { it.first.content }
        .forEach { (referenceChunk, duplicateChunks) ->
//            val fileName =
//                "${referenceChunk.path.replace("/", "-").replace("\\", "")}_" +
//                        "${referenceChunk.lineNumber}_" +
//                        "${duplicateChunks.size}.txt"
//
//            val outputFile = options.outputDirectory.resolve(fileName)
//
//            val content = buildString {
//                referenceChunkHeader()
//                referenceChunkInfo(referenceChunk)
//                referenceChunkContent(referenceChunk)
//                separator()
//                duplicateChunksHeader()
//                duplicateChunksInfo(referenceChunk, duplicateChunks, options)
//            }
//
//            outputFile.writeText(content.toString())

            if (referenceChunk !in processedChunks && !referenceChunk.path.contains("Generated")) {
                val locations = duplicateChunks.filter { chunk ->
                    chunk.content.replace("\\s".toRegex(), "") != referenceChunk.content.replace("\\s".toRegex(), "")
                }.map { chunk ->
                    "${chunk.path}:${chunk.lineNumber}"
                }
                val doc = Doc(
                    query = referenceChunk.content,
                    locations = locations
                )
                jsonDocs.add(doc)
                processedChunks.add(referenceChunk)
                processedChunks.addAll(duplicateChunks)
            }
        }

    val json = Gson().toJson(jsonDocs)
    val jsonFile = options.outputDirectory.resolve("almost_matches.json")
    jsonFile.writeText(json)
}

private fun StringBuilder.referenceChunkHeader() = appendLine("Reference chunk:").appendBlankLine()
private fun StringBuilder.referenceChunkInfo(referenceChunk: Chunk) = appendLine("${referenceChunk.path} ${referenceChunk.lineNumber}").appendBlankLine()
private fun StringBuilder.referenceChunkContent(referenceChunk: Chunk) = appendLine(referenceChunk.content.trim()).appendBlankLine()
private fun StringBuilder.separator() = appendLine("====================").appendBlankLine()
private fun StringBuilder.duplicateChunksHeader() = appendLine("Duplicate chunks:").appendBlankLine()
private fun StringBuilder.appendBlankLine() = appendLine("\n")
private fun StringBuilder.duplicateChunksInfo(rChunk: Chunk, dChunks: List<Chunk>, opts: DuplicateFinderOptions) =
    dChunks.forEach { dChunk ->
        append(
            "${dChunk.similarity(rChunk, opts)}% " +
                    "${dChunk.path} " +
                    "${dChunk.lineNumber}\n"
        )
    }

data class Doc(
    val query: String,
    val locations: List<String>
)