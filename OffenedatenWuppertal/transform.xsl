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
		<xsl:apply-templates select="ExtendedData/SchemaData/SimpleData" />
		<xsl:text>}</xsl:text>
		<xsl:apply-templates select="Point|Polygon|MultiGeometry" />
		<xsl:text>}</xsl:text><xsl:if test="position()!=last()"><xsl:text>,</xsl:text></xsl:if>
	</xsl:template>
	<xsl:template match="SimpleData">
		<xsl:text>"</xsl:text><xsl:value-of select="@name" /><xsl:text>": "</xsl:text><xsl:value-of select="text()" /><xsl:text>"</xsl:text><xsl:if test="position()!=last()"><xsl:text>,</xsl:text></xsl:if>
	</xsl:template>
	<xsl:template match="Point">
		<xsl:text>
	 		, "geometry": {
				"type": "Point",
				"coordinates": [
		</xsl:text>
		<xsl:value-of select="tokenize(coordinates/text(), ',')[1]" /><xsl:text>, </xsl:text>
		<xsl:value-of select="tokenize(coordinates/text(), ',')[2]" />
		<xsl:text>]}</xsl:text>
	</xsl:template>
	<xsl:template match="Polygon">
		<xsl:text>
	 		, "geometry": {
				"type": "Polygon",
				"coordinates": [[
		</xsl:text>
		<xsl:for-each select="tokenize(outerBoundaryIs/LinearRing/coordinates/text(), ' ')">
			<xsl:text>[</xsl:text><xsl:value-of select="tokenize(., ',')[1]" /><xsl:text>, </xsl:text>
			<xsl:value-of select="tokenize(., ',')[2]" /><xsl:text>]</xsl:text>
			<xsl:if test="position()!=last()">,</xsl:if>
		</xsl:for-each>
		<xsl:text>]]}</xsl:text>
	</xsl:template>
	<xsl:template match="MultiGeometry">
		<xsl:text>
	 		, "geometry": {
				"type": "MultiLineString",
				"coordinates": [
		</xsl:text>
		<xsl:for-each select="LineString">
			<xsl:text>[</xsl:text>
			<xsl:for-each select="tokenize(coordinates/text(), ' ')">
				<xsl:text>[</xsl:text><xsl:value-of select="tokenize(., ',')[1]" /><xsl:text>, </xsl:text>
				<xsl:value-of select="tokenize(., ',')[2]" /><xsl:text>]</xsl:text>
				<xsl:if test="position()!=last()">,</xsl:if>
			</xsl:for-each>
			<xsl:text>]</xsl:text>
			<xsl:if test="position()!=last()">
				<xsl:text>,</xsl:text>
			</xsl:if>
		</xsl:for-each>
		<xsl:text>]}</xsl:text>
	</xsl:template>
</xsl:stylesheet>
