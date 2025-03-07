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
import groovy.xml.*
import groovy.namespace.*
import com.predic8.wstool.creator.*
import com.predic8.xml.util.ClasspathResolver

class BuiltInTest extends GroovyTestCase{
  
  def schema
  def static final compTypeName = new QName("http://predic8.com" , "compType")
  
  void setUp() {
    def parser = new SchemaParser(resourceResolver: new ClasspathResolver())
    schema = parser.parse("/schema/BuiltInTest.xsd")
  }
  
  void testCreateXmlString() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('Country').create(creator, new RequestTemplateCreatorContext())
    def Country = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('Country' , Country.name())
    assertEquals('string' , Country.text())
  }
  
  void testCreateXmlInteger() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('Population').create(creator, new RequestTemplateCreatorContext())
    def Population = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('Population' , Population.name())
    assertEquals('number' , Population.text())
  }
  
  void testCreateXmlShort() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('States').create(creator, new RequestTemplateCreatorContext())
    def States = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('States' , States.name())
    assertEquals('number' , States.text())
  }
  
  void testCreateXmlDouble() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('Area').create(creator, new RequestTemplateCreatorContext())
    def Area = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('Area' , Area.name())
    assertEquals('number' , Area.text())
  }
  
  void testCreateXmlFloat() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('Border').create(creator, new RequestTemplateCreatorContext())
    def Border = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('Border' , Border.name())
    assertEquals('number' , Border.text())
  }
  
  void testCreateXmlBoolean() {
    def strWriter = new StringWriter()
    def creator = new RequestTemplateCreator(builder : new MarkupBuilder(strWriter))
    schema.getElement('NatoMember').create(creator, new RequestTemplateCreatorContext())
    def NatoMember = new XmlSlurper().parseText(strWriter.toString())
    assertEquals('NatoMember' , NatoMember.name())
    assertEquals('boolean' , NatoMember.text())
  }
}
