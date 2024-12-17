package finder.indexing

import finder.*
import finder.ngram.ngramProvider

fun MutableMap<Ngram, MutableList<Chunk>>.indexChunk(
    content: String,
    path: String,
    lineNumber: String,
    type: String,
    options: DuplicateFinderOptions
) {
    val ngramProvider = ngramProvider(options)
    val ngrams = ngramProvider.ngrams(content)
    val chunk = Chunk(
        content = content,
        path = path,
        lineNumber = lineNumber,
        type = type,
    )
    ngrams.forEach { ngram ->
        val chunksWithNgram = getOrPut(ngram) { mutableListOf() }
        chunksWithNgram.add(chunk)
    }
}