<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <!-- ========================= -->
  <!-- root element: cellar -->
  <!-- ========================= -->
<xsl:template match="cellar">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="2cm" margin-bottom="2cm" margin-left="1cm" margin-right="1cm">
<fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="simpleA4">
        <fo:flow flow-name="xsl-region-body">
          <fo:block font-size="10pt" space-after="5mm">MyCellar 
          </fo:block>
<fo:block font-size="8pt">
            <fo:table table-layout="fixed">
<fo:table-column column-width="6cm"/>
<fo:table-column column-width="1cm"/>
<fo:table-column column-width="1cm"/>
<fo:table-column column-width="2cm"/>
<fo:table-column column-width="1cm"/>
<fo:table-column column-width="1cm"/>
<fo:table-column column-width="1cm"/>
<fo:table-column column-width="2cm"/>
<fo:table-column column-width="4cm"/>
<fo:table-header><fo:table-row><fo:table-cell><fo:block font-weight="bold">Nom</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Année</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Contenance</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Rangement</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Numéro de partie</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Ligne</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Colonne</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Prix</fo:block></fo:table-cell>
<fo:table-cell><fo:block font-weight="bold">Autre 3</fo:block></fo:table-cell>
</fo:table-row></fo:table-header><fo:table-body>
<xsl:apply-templates/>
</fo:table-body>
</fo:table>
</fo:block>
</fo:flow>
</fo:page-sequence>
    </fo:root>
  </xsl:template>
  <!-- ========================= -->
  <!-- child element: wine     -->
  <!-- ========================= -->
  <xsl:template match="wine">
    <fo:table-row>
      <xsl:if test="function = 'lead'">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
      </xsl:if>
      <fo:table-cell>
<fo:block>
<xsl:value-of select="name"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="year"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="half"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="place"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="num-place"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="line"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="column"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="price"/>
</fo:block>
</fo:table-cell>
<fo:table-cell>
<fo:block>
<xsl:value-of select="other3"/>
</fo:block>
</fo:table-cell>
</fo:table-row>
  </xsl:template>
</xsl:stylesheet>
