<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://www.opengis.net/kml/2.2">
<xsl:output method="text" encoding="utf-8"/>
  <xsl:template match="/">
		<xsl:text>{
			"type" : "FeatureCollection",
			"name" : "</xsl:text><xsl:value-of select="kml/Document/name/text()" /><xsl:text>",
			"features" : [
		</xsl:text>
		<xsl:apply-templates select="kml/Document/Folder/Placemark" />
		<xsl:text>]}</xsl:text>
	</xsl:template>
	<xsl:template match="Placemark">
		<xsl:text>{
			"type": "Feature",
			"properties": {
		</xsl:text>
		<xsl:for-each select="ExtendedData/SchemaData/SimpleData">
			<xsl:text>"</xsl:text><xsl:value-of select="@name" /><xsl:text>": "</xsl:text><xsl:value-of select="text()" /><xsl:text>"</xsl:text><xsl:if test="position()!=last()"><xsl:text>,</xsl:text></xsl:if>
		</xsl:for-each>
		<xsl:text>},
			"geometry": {
				"type": "Point",
				"coordinates": [
		</xsl:text>
		<xsl:value-of select="tokenize(Point/coordinates/text(), ',')[1]" /><xsl:text>, </xsl:text>
		<xsl:value-of select="tokenize(Point/coordinates/text(), ',')[2]" />
		<xsl:text>]}}</xsl:text><xsl:if test="position()!=last()"><xsl:text>,</xsl:text></xsl:if>
	</xsl:template>
</xsl:stylesheet>
