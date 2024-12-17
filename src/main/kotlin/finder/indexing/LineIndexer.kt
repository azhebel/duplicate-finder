package finder.indexing

import finder.*
import java.nio.file.*

class LineIndexer(
    options: DuplicateFinderOptions,
): ContentIndexer(options) {

    override fun indexFile(pathFromRoot: Path): Map<Length, Map<Ngram, List<Chunk>>> {
        val path = root.resolve(pathFromRoot)
        val lines = Files.readAllLines(path)

        val groupedByLength = buildMap<Int, MutableMap<String, MutableList<Chunk>>> {
            lines.filter { it.length >= options.minLength }.forEachIndexed { i, line ->
                val length = line.length
                val ngramIndex = getOrPut(length) { mutableMapOf() }
                val lineNumber = i + 1
                ngramIndex.indexChunk(line, pathFromRoot.toString(), lineNumber.toString(), "line", options)
            }
        }
        return groupedByLength
    }
}
