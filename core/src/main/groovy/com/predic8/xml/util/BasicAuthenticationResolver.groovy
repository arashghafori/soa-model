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

package com.predic8.xml.util

import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.params.ConnRoutePNames
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.predic8.schema.Import as SchemaImport
import com.predic8.wsdl.Import as WsdlImport
class BasicAuthenticationResolver extends ResourceResolver {

  private static final Logger log = LoggerFactory.getLogger(BasicAuthenticationResolver.class)

  def username = ''
  def password = ''
  def proxyHost
  def proxyPort

  def resolve(input, baseDir) {
    if ( input instanceof SchemaImport ) {
      if ( !input.schemaLocation ) return
      input = input.schemaLocation
    }

    if ( input instanceof WsdlImport ) {
      if ( !input.location ) return
      input = input.location
    }

    if ( input instanceof InputStream )  {
      return input;
    }

    if(input instanceof Reader) {
      throw new RuntimeException("Please use an InputStream instead of Reader!")
    }


    if(input instanceof File){
      return new FileInputStream(input)
    }
    if (! input instanceof String)
      throw new RuntimeException("Do not know how to resolve $input")

    if(input.startsWith('file')){
      def url = new URL(input)
      return new FileInputStream(url.getPath())
    } else if(input.startsWith('http') || input.startsWith('https')) {
      return resolveViaHttp(input)
    } else {
      if(baseDir && (baseDir.startsWith('http') || baseDir.startsWith('https'))){
        return resolveViaHttp(baseDir + input)
      } else if(baseDir) {
        return new FileInputStream(baseDir+input)
      }
      else {
        def file = new File(input)
        return new FileInputStream(file.getAbsolutePath())
      }
    }
  }

  private request(url) {
    CredentialsProvider provider = new BasicCredentialsProvider();
    if ( username ) {
      Credentials credentials = new UsernamePasswordCredentials(username, password)
      provider.setCredentials(AuthScope.ANY, credentials);
    }
    HttpClient client = HttpClientBuilder.create()
            .setDefaultCredentialsProvider(provider)
            .build();
    if ( proxyHost ){
      HttpHost proxy = new HttpHost(proxyHost, proxyPort);
      client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }

    HttpResponse response = client.execute(new HttpGet(url));
//    client.getParams().setParameter(HttpMethodParams.USER_AGENT,"SOA Model (see http://membrane-soa.org)")
    int status = response.getStatusLine()
            .getStatusCode();
    if(status != 200) {
      def rde = new ResourceDownloadException("could not get resource $url by HTTP")
      rde.status = status
      rde.url = url
      response.close()
      throw rde
    }
    response
  }

  protected resolveViaHttp(url) {
    URI uri = new URI(url)
    uri = uri.normalize()
    new StringReader(resolveAsString(uri.toString()))
  }

  protected resolveAsString(url) {
    HttpResponse con = request(url)
    EntityUtils.toString(con.entity)
  }

}
