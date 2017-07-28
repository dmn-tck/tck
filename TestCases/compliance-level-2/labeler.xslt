<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:n1="http://www.omg.org/spec/DMN/20151101/dmn.xsd" xmlns:n2="http://www.omg.org/spec/DMN/20160719/testcase" xmlns="http://www.omg.org/spec/DMN/20160719/testcase" xmlns:feel="http://www.omg.org/spec/FEEL/20140401">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="foldername" select="substring-before(//n2:modelName,'.dmn')"/>
		<xsl:variable name="folderpath" select="concat($foldername,'/',//n2:modelName)"/>
		<xsl:variable name="dmn" select="doc($folderpath)"/>
		<xsl:variable name="autolabels" as="item()*">
			<xsl:if test="$dmn//n1:businessKnowledgeModel">
				<xsl:sequence select="'Business Knowledge Model'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:context">
				<xsl:sequence select="'Context'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:decisionTable[count(n1:output) gt 1]">
				<xsl:sequence select="'Decision Table: Multiple Output'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:decisionTable[count(n1:output) = 1]">
				<xsl:sequence select="'Decision Table: Single Output'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\b\s*(\+|\-|\*|/)')]">
				<xsl:sequence select="'FEEL Arithmetic'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\bif\b')]">
				<xsl:sequence select="'FEEL Conditionals'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\[((?!\.\.)[^,])+\]')]">
				<xsl:sequence select="'FEEL Filter (10.3.2.5)'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\bfunction\s*\(')]">
				<xsl:sequence select="'FEEL Function Literals'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\b(date|time|duration)\s*\(')]">
				<xsl:sequence select="'FEEL Functions: date and time'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\b(string|substring|string length|upper case|lower case|substring before|substring after|replace|starts with|ends with|matches)\s*\(')]">
				<xsl:sequence select="'FEEL Functions: string'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'contains\s*\((?&lt;!list )')]">
				<xsl:sequence select="'FEEL Functions: string'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\b(list contains|count|min|max|mean|and|or|sublist|append|concatenate|insert before|remove|reverse|index of|union|distinct values|flatten)\s*\(')]">
				<xsl:sequence select="'FEEL Functions: list'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\b(number|floor|ceiling|decimal)\s*\(')]">
				<xsl:sequence select="'FEEL Functions: numeric'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\bfor \.*\bin ')]">
				<xsl:sequence select="'FEEL Iteration'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:text[matches(.,'\b(some |every )\.*\bin ')]">
				<xsl:sequence select="'FEEL Quantifiers'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:relation">
				<xsl:sequence select="'Relation'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:variable/@name[matches(.,'(\s|\.|\+|\-|\&apos;&apos;|\*|/)')]">
				<xsl:sequence select="'FEEL Special-character Names'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:invocation">
				<xsl:sequence select="'Function Invocation'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:decisionTable[@hitPolicy='ANY']">
				<xsl:sequence select="'Hit Policy: ANY'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:decisionTable[@hitPolicy='COLLECT']">
				<xsl:sequence select="'Hit Policy: COLLECT'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:decisionTable[@hitPolicy='PRIORITY']">
				<xsl:sequence select="'Hit Policy: PRIORITY'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:decisionTable[@hitPolicy='UNIQUE' or not(@hitPolicy)]">
				<xsl:sequence select="'Hit Policy: UNIQUE'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:itemDefinition">
				<xsl:sequence select="'Item Definition'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:literalExpression">
				<xsl:sequence select="'Literal Expression'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[contains(lower-case(string(.)),':string')]|$dmn//@typeRef[contains(lower-case(string(.)),':string')]">
				<xsl:sequence select="'Data Type: String'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[contains(lower-case(string(.)),':number')]|$dmn//@typeRef[contains(lower-case(string(.)),':number')]">
				<xsl:sequence select="'Data Type: Number'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[contains(lower-case(string(.)),':boolean')]|$dmn//@typeRef[contains(lower-case(string(.)),':boolean')]">
				<xsl:sequence select="'Data Type: Boolean'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:itemDefinition/n1:itemComponent">
				<xsl:sequence select="'Data Type: Structure'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:itemDefinition[@isCollection=true()]|$dmn//n1:itemDefinition//n1:itemComponent[@isCollection=true()]">
				<xsl:sequence select="'Data Type: Collection'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[matches(string(.),':date(?! and time)')]|$dmn//@typeRef[matches(string(.),':date(?! and time)')]">
				<xsl:sequence select="'Data Type: Date'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[matches(string(.),':time\b')]|$dmn//@typeRef[matches(string(.),':time\b')]">
				<xsl:sequence select="'Data Type: Time'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[matches(string(.),':date and time')]|$dmn//@typeRef[matches(string(.),':date and time')]">
				<xsl:sequence select="'Data Type: Date and Time'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[contains(string(.),':days and time duration')]|$dmn//@typeRef[contains(string(.),':days and time duration)')]">
				<xsl:sequence select="'Data Type: Days and Time Duration'"/>
			</xsl:if>
			<xsl:if test="$dmn//n1:typeRef[contains(string(.),':years and months duration')]|$dmn//@typeRef[contains(string(.),':years and months duration)')]">
				<xsl:sequence select="'Data Type: Years and Months Duration'"/>
			</xsl:if>
		</xsl:variable>
		<xsl:for-each select="//n2:testCases">
			<xsl:copy>
				<xsl:for-each select="n2:modelName">
					<xsl:copy-of select="."/>
				</xsl:for-each>
				<xsl:for-each select="n2:labels">
					<xsl:copy copy-namespaces="yes">
						<xsl:copy-of select="n2:label"/>
						<xsl:for-each select="$autolabels[not(. =current()/n2:label)]">
							<xsl:element name="label" namespace="http://www.omg.org/spec/DMN/20160719/testcase">
								<xsl:value-of select="."/>
							</xsl:element>
						</xsl:for-each>
					</xsl:copy>
				</xsl:for-each>
				<xsl:for-each select="n2:testCase">
					<xsl:copy-of select="."/>
				</xsl:for-each>
			</xsl:copy>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
