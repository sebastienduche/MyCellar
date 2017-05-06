<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:variable name="table1">1</xsl:variable>
   <xsl:variable name="table0">0</xsl:variable>
   <xsl:template match="/">
      <html>
        <title>Cave</title>
        <head>
            <style>
            table.internal {
    border-spacing: 0;
    width: 100%;
    
}

table.external {
    width: 100%;
    
}

td.cell {
    border: 1px solid black;
    padding: 5;
    text-align: center;
}

</style>
        </head>
         <body>
            <xsl:for-each select="cave/rangement"><br/>
            <xsl:variable name="cols" select="100 div @columns"></xsl:variable>
            
            <center><font size="5"><b><xsl:value-of select="name" /></b></font></center>
            <xsl:for-each select="partie"><br/>
            <center><font size="2"><b><xsl:value-of select="nom-partie" /></b></font></center><br/>
            <table class="external">
               <xsl:for-each select="ligne">
                  <tr>
                     <td>
                        <table class="internal">
                           <tr>
                              <xsl:for-each select="vin">
                                 <td class="cell" height="50" width="{$cols}%">
                                    <xsl:value-of select="vin1" />
                                 </td>
                              </xsl:for-each>
                              <xsl:for-each select="nonvin">
                                 <td height="50" width="{$cols}%"></td>
                              </xsl:for-each>
                           </tr>
                        </table>
                     </td>
                  </tr>
               </xsl:for-each>
               <xsl:for-each select="caisse/vin">
                  <tr>
                    <td class="cell" height="50" width="{$cols}%">
                       <xsl:value-of select="vin1" />
                    </td>
					</tr>
               </xsl:for-each>
            </table>
            </xsl:for-each>
            </xsl:for-each>
         </body>
      </html>
   </xsl:template>
</xsl:stylesheet>
