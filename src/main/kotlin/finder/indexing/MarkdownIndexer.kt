package finder.indexing

import finder.*
import org.commonmark.node.*
import org.commonmark.parser.*
import org.commonmark.renderer.text.TextContentRenderer
import java.nio.file.Path
import kotlin.io.path.readText

class MarkdownIndexer(
    options: DuplicateFinderOptions,
): ContentIndexer(options) {
    override fun indexFile(pathFromRoot: Path): Map<Length, Map<Ngram, List<Chunk>>> {
        val path = root.resolve(pathFromRoot)
        val parser = Parser
            .builder()
            .includeSourceSpans(IncludeSourceSpans.BLOCKS)
            .build()
        val document = parser.parse(path.readText())

        val groupedByLength = mutableMapOf<Int, MutableMap<String, MutableList<Chunk>>>()

        fun addElement(block: Block, type: String) {
            val content = TextContentRenderer.builder().build().render(block)
            if (content.length < options.minLength) return
            val ngramIndex = groupedByLength.getOrPut(content.length) { mutableMapOf() }
            ngramIndex.indexChunk(
                content = content,
                path = pathFromRoot.toString(),
                lineNumber = "${block.sourceSpans.first().lineIndex}:${block.sourceSpans.first().columnIndex}",
                type = type,
                options = options
            )
        }

        document.accept(object : AbstractVisitor() {
            override fun visit(paragraph: Paragraph) = addElement(paragraph, "md_paragraph")
            override fun visit(fencedCodeBlock: FencedCodeBlock) = addElement(fencedCodeBlock, "md_fenced_code")
            override fun visit(heading: Heading) = addElement(heading, "md_heading")
            override fun visit(orderedList: OrderedList) = addElement(orderedList, "md_ordered_list")
            override fun visit(bulletList: BulletList) = addElement(bulletList, "md_bullet_list")
            override fun visit(listItem: ListItem) = addElement(listItem, "md_list_item")
            override fun visit(blockQuote: BlockQuote) = addElement(blockQuote, "md_block_quote")
            override fun visit(indentedCodeBlock: IndentedCodeBlock) = addElement(indentedCodeBlock, "md_indented_code")
            override fun visit(thematicBreak: ThematicBreak) = addElement(thematicBreak, "md_thematic_break")
            override fun visit(htmlBlock: HtmlBlock) = addElement(htmlBlock, "md_html_block")
            override fun visit(customBlock: CustomBlock) = addElement(customBlock, "md_custom_block")
        })

        return groupedByLength
    }
}