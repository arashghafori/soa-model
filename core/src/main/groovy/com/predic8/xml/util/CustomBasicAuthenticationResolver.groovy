package com.predic8.xml.util

import com.predic8.schema.Import as SchemaImport
import com.predic8.wsdl.Import as WsdlImport
import org.apache.commons.httpclient.Credentials
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpMethod
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.params.HttpMethodParams
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CustomBasicAuthenticationResolver extends ResourceResolver{
    private static final Logger log = LoggerFactory.getLogger(CustomBasicAuthenticationResolver.class)

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
        HttpClient client = new HttpClient();
        if ( username ) {
            Credentials defaultcreds = new UsernamePasswordCredentials(username, password)
            client.state.setCredentials(AuthScope.ANY, defaultcreds)
        }
        if ( proxyHost )
            client.getHostConfiguration().setProxy(proxyHost, proxyPort)

        HttpMethod method = new GetMethod(url)
        method.params.setParameter(HttpMethodParams.USER_AGENT,"SOA Model (see http://membrane-soa.org)")
        int status = client.executeMethod(method);
        if(status != 200) {
            def rde = new ResourceDownloadException("could not get resource $url by HTTP")
            rde.status = status
            rde.url = url
            method.releaseConnection()
            throw rde
        }
        method
    }

    protected resolveViaHttp(url) {
        new StringReader(resolveAsString(url))
    }

    protected resolveAsString(url) {
        def con = request(url)
        def res = con.getResponseBodyAsString()
        con.releaseConnection()
        res
    }
}
