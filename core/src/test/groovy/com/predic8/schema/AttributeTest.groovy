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
import junit.framework.TestCase
import javax.xml.stream.*
import javax.xml.xpath.*
import groovy.xml.*
import groovy.namespace.*

import com.predic8.schema.creator.*
import com.predic8.wstool.creator.*

import com.predic8.xml.util.*

class AttributeTest extends GroovyTestCase{
  
  def schema
  

  void setUp() {
    def parser = new SchemaParser(resourceResolver: new ClasspathResolver())
    schema = parser.parse("/schema/attribute/attr-test.xsd")
  }
  
  void testParsing() {
    assertEquals('id', schema.getType('CarType').getAttribute('id').name)
    assertEquals('xsd:integer[]', schema.getType('CarType').getAttribute('id').arrayType)
    assertEquals('xsd:integer[]', schema.getElement('MyCar').arrayType)
    assertEquals('speed', schema.getType('CarType').getAttribute('speed').name)
    assertEquals('door',schema.getType('SmallCarType').model.derivation.attributes[0].name)
//	println schema.getType('SmallCarType').model.derivation.attributes[0].annotation.contents.content
  }
  
  void testSchemaCreator() {
    def strWriter = new StringWriter()
    def creator = new SchemaCreator(builder : new MarkupBuilder(strWriter))
    schema.create(creator, new SchemaCreatorContext())
    def testSchema = new XmlSlurper().parseText(strWriter.toString())
    def nameList = []
    testSchema.complexType.attribute.each { nameList << it.@name}
    assertEquals("[id, speed]", nameList.toString())
    assertEquals('door', testSchema.complexType[1].complexContent.extension.attribute.@name.toString())
    assertEquals('2', testSchema.complexType[1].complexContent.extension.attribute.@fixed.toString())
    assertEquals('required', testSchema.complexType[1].complexContent.extension.attribute.@use.toString())
  }
  
  void testRequestTemplateCreatorWithFixedAttribute() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('MyCar').create(creator, new RequestTemplateCreatorContext())
    def testXML = new XmlSlurper().parseText(strWriter.toString()).declareNamespace(car: 'http://predic8.com/car')
    assertEquals('string', testXML.'@car:id'.toString())
    assertEquals('number', testXML.'@car:speed'.toString())
    assertEquals('2', testXML.@door.toString())
  }

  void testRequestTemplateCreatorWithRefAttribute() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('MyNewCar').create(creator, new RequestTemplateCreatorContext())
    def testXML = new XmlSlurper().parseText(strWriter.toString()).declareNamespace(car: 'http://predic8.com/car')
    assertEquals('string', testXML.'@car:id'.toString())
    assertEquals('number', testXML.'@car:speed'.toString())
    assertEquals('number', testXML.@door.toString())
  }
  
  void testRequestCreator() {
    def strWriter = new StringWriter()
    def creator = new RequestCreator(builder : new MarkupBuilder(strWriter))
    def formParams = [:]
    formParams['xpath:/MyCar/@id']='BN-A1234'
    formParams['xpath:/MyCar/@speed']='200'
    formParams['xpath:/MyCar/@door']='4'
    schema.getElement('MyCar').create(creator, new RequestCreatorContext(formParams:formParams))
    def testXML = new XmlSlurper().parseText(strWriter.toString())
  }
  
  void testGetBuildIntTypeNameFromEmbeddedType() {
    assertEquals('int', schema.getAttribute('door').buildInTypeName)
  }
  
  void testGetBuildInTypeNameFromRefType() {
    assertEquals('int', schema.getType('NewSmallCarType').model.derivation.attributes[0].buildInTypeName)
  }
  
  void testGetBuildInTypeName() {
    assertEquals('integer', schema.getType('SmallCarType').model.derivation.attributes[0].buildInTypeName)
  }
	
	void testAnnotation() {
		assertEquals('This is a documentation for Attribute.', schema.getAttribute('door').annotation.documentations[0].content)
	}
}
