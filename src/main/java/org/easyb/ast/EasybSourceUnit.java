package org.easyb.ast;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.SourceUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the source text for an assertion statement and translates
 * coordinates in the original source text to coordinates relative to the
 * assertion's (normalized) source text.
 *
 * @author Peter Niederwieser
 * @author Richard Vowles
 */

public class EasybSourceUnit {
      private final int firstLine;
    private String normalizedText;

    private final List<Integer> lineOffsets = new ArrayList<Integer>();
    private final List<Integer> textOffsets = new ArrayList<Integer>();

    /**
     * Constructs a <tt>SourceText</tt> by reading the given assertion's source
     * text from the given source unit.
     *
     * @param expr       an assertion statement
     * @param sourceUnit the source unit containing the assertion statement
     * @param janitor    a <tt>Janitor</tt> for cleaning up reader sources
     */
    public EasybSourceUnit(org.codehaus.groovy.ast.expr.Expression expr, SourceUnit sourceUnit, Janitor janitor) {
        if (!hasPlausibleSourcePosition(expr))
            throw new EasybSourceNotAvailableException(expr, sourceUnit, "Invalid source position");

        firstLine = expr.getLineNumber();
        textOffsets.add(0);
        normalizedText = "";

        for (int line = expr.getLineNumber(); line <= expr.getLastLineNumber(); line++) {
            String lineText = sourceUnit.getSample(line, 0, janitor);
            if (lineText == null)
                throw new EasybSourceNotAvailableException(expr, sourceUnit, "SourceUnit.getSample() returned null");

            if (line == expr.getLastLineNumber())
                lineText = lineText.substring(0, expr.getLastColumnNumber() - 1);
            if (line == expr.getLineNumber()) {
                lineText = lineText.substring(expr.getColumnNumber() - 1);
                lineOffsets.add(expr.getColumnNumber() - 1);
            } else
                lineOffsets.add(countLeadingWhitespace(lineText));

            //lineText = lineText.trim();
            if (line != expr.getLastLineNumber() && lineText.length() > 0)
                lineText += '\n';
            normalizedText += lineText;
            textOffsets.add(normalizedText.length());
        }
    }

    /**
     * Returns the assertion's source text after removing line breaks.
     * <p>Limitation: Line comments within the assertion's source text are not
     * handled.
     *
     * @return the assertion's source text after removing line breaks.
     */
    public String getNormalizedText() {
        return normalizedText;
    }

    /**
     * Returns the column in <tt>getNormalizedText()</tt> corresponding
     * to the given line and column in the original source text. The
     * first character in the normalized text has column 1.
     *
     * @param line   a line number
     * @param column a column number
     * @return the column in getNormalizedText() corresponding to the given line
     *         and column in the original source text
     */
    public int getNormalizedColumn(int line, int column) {
        int deltaLine = line - firstLine;
        if (deltaLine < 0) // wrong line information
            return -1;
        int deltaColumn = column - lineOffsets.get(deltaLine);
        if (deltaColumn < 0) // wrong column information
            return -1;

        return textOffsets.get(deltaLine) + deltaColumn;
    }

    /**
     * Returns the normalized column of the last occurrence of the given
     * substring within the original source text ending before the given
     * line and column (exclusive), or -1 if no such occurrence is found.
     *
     * @param str    a substring in the original source text
     * @param line   a line number
     * @param column a column number
     * @return the normalized column of the last occurrence of the given
     *         substring within the original source text ending before the given
     *         line and column (exclusive), or -1 if no such occurrence is found
     */
    public int getNormalizedColumn(String str, int line, int column) {
        return getNormalizedText().lastIndexOf(str, getNormalizedColumn(line, column) - 1 - str.length()) + 1;
    }

    private boolean hasPlausibleSourcePosition(ASTNode node) {
        return node.getLineNumber() > 0
                && node.getColumnNumber() > 0
                && node.getLastLineNumber() >= node.getLineNumber()
                && node.getLastColumnNumber() >
                (node.getLineNumber() == node.getLastLineNumber() ? node.getColumnNumber() : 0);
    }

    private int countLeadingWhitespace(String lineText) {
        int result = 0;
        while (result < lineText.length() && Character.isWhitespace(lineText.charAt(result)))
            result++;
        return result;
    }
}
