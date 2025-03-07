/* Copyright 2012 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.predic8.schema

import groovy.test.GroovyTestCase

import javax.xml.stream.*
import groovy.xml.*
import groovy.namespace.*

import com.predic8.xml.util.*
import com.predic8.schema.creator.*
import com.predic8.schema.diff.*
import com.predic8.wstool.creator.*

class ComplexContentTest extends GroovyTestCase{

  Schema schemaA
  Schema schemaB

  void setUp() {
    def parser = new SchemaParser(resourceResolver: new ClasspathResolver())
    schemaA = parser.parse("/schema/complexcontent/person-a.xsd")
    schemaB = parser.parse("/schema/complexcontent/person-b.xsd")
  }

	void testParseAnnotation() {
		assert schemaA.getType('AmigoType').model.annotation.contents[0].content == 'Test OK'
	}

  void testParseMixedCC(){
    assert(schemaA.getType('AmigoType').model.mixed)
  }

  void testSchemaCreatorExtension(){
    def strWriter = new StringWriter()
    def creator = new SchemaCreator(builder : new MarkupBuilder(strWriter))
    schemaA.create(creator, new SchemaCreatorContext())
    def testSchema = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('department', testSchema.complexType[1].complexContent.extension.sequence.element.@name.toString())
  }

  void testSchemaCreatorRestriction(){
    def strWriter = new StringWriter()
    def creator = new SchemaCreator(builder : new MarkupBuilder(strWriter))
    schemaA.create(creator, new SchemaCreatorContext())
	def schemaAsString = strWriter.toString()
    def testSchema = new XmlSlurper().parseText(schemaAsString)
	assertEquals('true', testSchema.complexType[2].complexContent.@mixed.toString())
    assertEquals('firstName', testSchema.complexType[2].complexContent.restriction.sequence.element.@name.toString())
  }

  void testRequestCreatorExtension() {
    def strWriter = new StringWriter()
    def creator = new RequestCreator(builder : new MarkupBuilder(strWriter))
    schemaA.getElement('employee').create(creator, new RequestCreatorContext(formParams: ["xpath:/employee/firstName":"Kaveh", "xpath:/employee/lastName":"Keshavarzi","xpath:/employee/department/name":"IT"]))
    def emp = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('Kaveh', emp.firstName.toString())
    assertEquals('Keshavarzi', emp.lastName.toString())
    assertEquals('IT', emp.department.name.toString())
  }

  void testRequestCreatorRestriction() {
    def strWriter = new StringWriter()
    def creator = new RequestCreator(builder : new MarkupBuilder(strWriter))
    schemaA.getElement('amigo').create(creator, new RequestCreatorContext(formParams: ["xpath:/amigo/firstName":"Kaveh"]))
    def amigo = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('Kaveh', amigo.firstName.toString())
    assertEquals('', amigo.lastName.toString())
  }

  void testRequestTemplateCreator() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schemaA.getElement('employee').create(creator, new RequestTemplateCreatorContext())
    def emp = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('string', emp.firstName.toString())
    assertEquals('string', emp.lastName.toString())
    assertEquals('string', emp.department.name.toString())
    assertEquals('number', emp.department.id.toString())
  }

  void testSchemaDiffGenerator(){
    def diffGen = new SchemaDiffGenerator(a: schemaA, b: schemaB)
    def msgs = diffGen.compare()
    assertEquals(3, msgs.size())
    assertTrue(msgs[1].dump().contains("Extension"))
    assertTrue(msgs[2].dump().contains("Content model of complexContent changed from 'mixed' to 'element-only'"))
    assertTrue(msgs[2].dump().contains("ComplexContent changed from 'restriction' to 'extension'"))
  }
}