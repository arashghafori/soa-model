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

package com.predic8.wstool.creator;

import groovy.xml.*
import groovy.namespace.*

import com.predic8.schema.BuiltInSchemaType;

class TemplateUtil {

  public static getTemplateValue(QName type){
    if ( !type ) return ""
    getTemplateValue(type.localPart)
  }
  
	public static getTemplateValue(BuiltInSchemaType type){
		getTemplateValue(type.type)
	}
	
  public static getTemplateValue(String type){
      switch (type) {
          case 'string' : return 'string'
          case ['int','integer', 'long' , 'short','nonNegativeInteger','positiveInteger'] : return 'number'
          case 'nonPositiveInteger' : return 'number'
          case ['double','float','decimal'] : return 'number'
          case 'boolean' : return 'boolean'
          case 'date' : return '2008-12-31'
          case 'dateTime' : return '2008-12-31T23:59:00.000-05:00'
          default :
              //"Type: ${type} is not supported yet"
              return '???'
      }
  }
}
