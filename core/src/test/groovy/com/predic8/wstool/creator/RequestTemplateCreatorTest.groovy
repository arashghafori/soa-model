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

package com.predic8.wstool.creator

import groovy.test.GroovyTestCase;
import groovy.xml.*
import groovy.namespace.*

import com.predic8.wsdl.*
import com.predic8.xml.util.*

class RequestTemplateCreatorTest extends GroovyTestCase {

  def definitions
  def operationName ='getBank'
  def portType = 'BLZServicePortType'

  void setUp() {
    definitions = getDefinitions()
  }

  void testElementRequestTemplate() {
		def element = definitions.getInputElementForOperation(portType, operationName)
    def requestTemplate = new XmlSlurper().parseText(element.requestTemplate).declareNamespace('ns1':'http://thomas-bayer.com/blz/')
    assertEquals('string', requestTemplate.blz.text())
    assertEquals('number', requestTemplate.'@ns1:testAttribute'.toString())
  }
	
	void testRequestTemplateForElementWithDefaultAndFixet() {
/**
 * see RequestCreatorTest.groovy.	
 */
	}

  private def getDefinitions() {
    def parser = new WSDLParser(resourceResolver: new ClasspathResolver())
    definitions = parser.parse("/BLZService.wsdl")
  }
}
