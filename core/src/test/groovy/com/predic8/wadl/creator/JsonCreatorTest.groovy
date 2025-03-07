package com.predic8.wadl.creator

import groovy.json.JsonBuilder

import com.predic8.schema.Schema
import com.predic8.schema.SchemaParser
import com.predic8.xml.util.ClasspathResolver
import groovy.test.GroovyTestCase

class JsonCreatorTest extends GroovyTestCase {

	Schema schema

	protected void setUp() throws Exception {
		SchemaParser parser = new SchemaParser(resourceResolver: new ClasspathResolver())
		schema = parser.parse('group.xsd')
	}

	void testJsonCreator() {
		def element = schema.getElement('employee')
		assert '{"employee":{"person":{"firstName":"string","lastName":"string"}}}' == element.asJson.replaceAll("\\s","")
	}
}
