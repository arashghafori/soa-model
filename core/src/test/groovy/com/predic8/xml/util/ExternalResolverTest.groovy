/* Copyright 2012 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. *//*


package com.predic8.xml.util

import org.junit.Assume
import org.junit.Before
import org.junit.Test

class ExternalResolverTest {
  
	def resolver
	def url

    @Before
	void setUp() {
	  resolver = new ExternalResolver()
	  url = 'http://www.thomas-bayer.com/axis2/services/BLZService?wsdl'
	}

   @Test
   void testResolveAsString() {
     Assume.assumeTrue(!System.getenv('OFFLINETESTING'))
     assert resolver.resolveAsString(url) != null
   }
}
*/
