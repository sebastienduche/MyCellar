<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> <xsl:template match="/">
<html>
<body>
<table border="1" cellspacing="0" cellpadding="3">
<tr bgcolor="#FFFF00">
<td>Nom</td>
<td>Ann&eacute;e</td>
<td>Contenance</td>
<td>Rangement</td>
<td>Num&eacute;ro</td>
<td>Ligne</td>
<td>Colonne</td>
<td>Prix</td>
<td>Commentaires</td>
</tr>
<xsl:for-each select="cellar/wine">
<xsl:sort select="name"/>
<tr>
<td><xsl:value-of select="name"/></td><td><xsl:value-of select="year"/></td><td><xsl:value-of select="half"/></td><td><xsl:value-of select="place"/></td><td><xsl:value-of select="num-place"/></td><td><xsl:value-of select="line"/></td><td><xsl:value-of select="column"/></td><td><xsl:value-of select="price"/></td><td><xsl:value-of select="comment"/></td><td><xsl:value-of select="dateOfC"/></td><td><xsl:value-of select="parker"/></td><td><xsl:value-of select="appellation"/></td></tr>
</xsl:for-each>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>