package finder.indexing

class Chunk(
    val content: String,
    val path: String,
    val lineNumber: String,
    @Suppress("unused") val type: String,
) {
    val preview: String
        get() = "$this – ${content.substring(0, 15)}..."

    override fun toString(): String {
        return "$path:$lineNumber"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chunk

        if (lineNumber != other.lineNumber) return false
        if (path != other.path) return false

        return true
    }

    
    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + lineNumber.hashCode()
        return result
    }
}