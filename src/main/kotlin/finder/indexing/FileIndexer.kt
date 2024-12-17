package finder.indexing

import finder.*
import java.nio.file.Path
import kotlin.io.path.readText

class FileIndexer(options: DuplicateFinderOptions) : ContentIndexer(options) {

    override fun indexFile(pathFromRoot: Path): Map<Length, Map<Ngram, List<Chunk>>> {
        val path = root.resolve(pathFromRoot)
        val fileContent = path.readText()
        return if (fileContent.length >= options.minLength) {
            val ngramIndex = buildMap<Ngram, MutableList<Chunk>> {
                indexChunk(fileContent, pathFromRoot.toString(), "0:0", "line", options)
            }
            mapOf(fileContent.length to ngramIndex)
        } else emptyMap()
    }
}