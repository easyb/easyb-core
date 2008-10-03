<?xml version="1.0"?>
<!--

    This is the XSL HTML configuration file for the Hibernate
    Reference Documentation.

    It took me days to figure out this stuff and fix most of
    the obvious bugs in the DocBook XSL distribution, so if you
    use this stylesheet, give some credit back to the Hibernate
    project.

    christian.bauer@bluemars.de
-->

<!DOCTYPE xsl:stylesheet [
    <!ENTITY db_xsl_path        "xsl">
    <!ENTITY stylesheet_location  "../../style/site.css">
    ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns="http://www.w3.org/TR/xhtml1/transitional"
                exclude-result-prefixes="#default">

  <xsl:import href="&db_xsl_path;/xhtml/docbook.xsl"/>

  <xsl:output method="html"
              encoding="ISO-8859-1"
              indent="no"/>

  <xsl:param name="build.year"/>
  <xsl:param name="build.date"/>
  <xsl:param name="project.version"/>

  <!--###################################################
                  HTML Settings
 ################################################### -->

  <xsl:param name="html.stylesheet">&stylesheet_location;</xsl:param>

  <!-- These extensions are required for table printing and other stuff -->
  <xsl:param name="use.extensions">0</xsl:param>
  <xsl:param name="tablecolumns.extension">0</xsl:param>
  <xsl:param name="callout.extensions">0</xsl:param>
  <xsl:param name="graphicsize.extension">0</xsl:param>

  <!--###################################################
                   Table Of Contents
 ################################################### -->

  <!-- Generate the TOCs for named components only -->
  <xsl:param name="generate.toc">
    book toc
  </xsl:param>

  <!-- Show only Sections up to level 3 in the TOCs -->
  <xsl:param name="toc.section.depth">3</xsl:param>

  <!--###################################################
                      Labels
 ################################################### -->

  <!-- Label Chapters and Sections (numbering) -->
  <xsl:param name="chapter.autolabel">1</xsl:param>
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.label.includes.component.label" select="1"/>

  <!--###################################################
                      Callouts
 ################################################### -->

  <!-- Use images for callouts instead of (1) (2) (3) -->
  <xsl:param name="callout.graphics">0</xsl:param>

  <!-- Place callout marks at this column in annotated areas -->
  <xsl:param name="callout.defaultcolumn">90</xsl:param>

  <!--###################################################
                    Admonitions
 ################################################### -->

  <!-- Use nice graphics for admonitions -->
  <xsl:param name="admon.graphics">0</xsl:param>

  <!--###################################################
                       Misc
 ################################################### -->

  <!-- Placement of titles -->
  <xsl:param name="formal.title.placement">
    figure after
    example before
    equation before
    table before
    procedure before
  </xsl:param>

  <xsl:template name="user.head.content">
    <xsl:param name="node" select="."/>
    <style type="text/css">
      body {
        margin:10px;
      }
      div#logo * {
        vertical-align:middle;
      }
    </style>
  </xsl:template>

  <xsl:template name="book.titlepage.recto">
    <div id="logo">
      <img src="images/logo_easyb.gif" alt="easyb"/>
      <h1 style="display:inline;">
        <xsl:value-of select="bookinfo/title"/>
      </h1>
    </div>

    <table class="grid">
      <tr>
        <th class="label">Publish Date</th>
        <td>
          <xsl:value-of select="$build.date"/>
        </td>
      </tr>
      <tr>
        <th class="label">Version</th>
        <td>
          <xsl:choose>
            <xsl:when test="bookinfo/releaseinfo">
              <xsl:value-of select="bookinfo/releaseinfo"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$project.version"/>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
      <tr>
        <th class="label">Author</th>
        <td>
          <xsl:apply-templates select="bookinfo/author"/>
        </td>
      </tr>
      <tr>
        <th class="label">Copyright</th>
        <td>Copyright
          <xsl:value-of select="$build.year"/>
          easyb.org
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="tgroup" name="tgroup">
    <xsl:if test="not(@cols)">
      <xsl:message terminate="yes">
        <xsl:text>Error: CALS tables must specify the number of columns.</xsl:text>
      </xsl:message>
    </xsl:if>

    <xsl:variable name="summary">
      <xsl:call-template name="dbhtml-attribute">
        <xsl:with-param name="pis" select="processing-instruction('dbhtml')"/>
        <xsl:with-param name="attribute" select="'table-summary'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="cellspacing">
      <xsl:call-template name="dbhtml-attribute">
        <xsl:with-param name="pis" select="processing-instruction('dbhtml')"/>
        <xsl:with-param name="attribute" select="'cellspacing'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="cellpadding">
      <xsl:call-template name="dbhtml-attribute">
        <xsl:with-param name="pis" select="processing-instruction('dbhtml')[1]"/>
        <xsl:with-param name="attribute" select="'cellpadding'"/>
      </xsl:call-template>
    </xsl:variable>

    <table class="grid">

      <xsl:if test="$cellspacing != '' or $html.cellspacing != ''">
        <xsl:attribute name="cellspacing">
          <xsl:choose>
            <xsl:when test="$cellspacing != ''">
              <xsl:value-of select="$cellspacing"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$html.cellspacing"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>

      <xsl:if test="$cellpadding != '' or $html.cellpadding != ''">
        <xsl:attribute name="cellpadding">
          <xsl:choose>
            <xsl:when test="$cellpadding != ''">
              <xsl:value-of select="$cellpadding"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$html.cellpadding"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>

      <xsl:variable name="colgroup">
        <colgroup>
          <xsl:call-template name="generate.colgroup">
            <xsl:with-param name="cols" select="@cols"/>
          </xsl:call-template>
        </colgroup>
      </xsl:variable>

      <xsl:variable name="explicit.table.width">
        <xsl:call-template name="dbhtml-attribute">
          <xsl:with-param name="pis" select="../processing-instruction('dbhtml')[1]"/>
          <xsl:with-param name="attribute" select="'table-width'"/>
        </xsl:call-template>
      </xsl:variable>

      <xsl:copy-of select="$colgroup"/>

      <xsl:apply-templates select="thead"/>
      <xsl:apply-templates select="tfoot"/>
      <xsl:apply-templates select="tbody"/>

      <xsl:if test=".//footnote">
        <tbody class="footnotes">
          <tr>
            <td colspan="{@cols}">
              <xsl:apply-templates select=".//footnote" mode="table.footnote.mode"/>
            </td>
          </tr>
        </tbody>
      </xsl:if>
    </table>
  </xsl:template>

</xsl:stylesheet>