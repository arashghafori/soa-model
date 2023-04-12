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

package com.predic8.wadl.creator;

import groovy.json.JsonBuilder

import com.predic8.schema.*
import com.predic8.schema.creator.*
import com.predic8.wstool.creator.TemplateUtil

class JsonCreator extends AbstractSchemaCreator <JsonCreatorContext>{

	JsonBuilder builder

	String getElementAsJson(Element element, JsonCreatorContext ctx = new JsonCreatorContext()) {
		createElement(element, ctx)
		LinkedHashMap<GString, Object> map = ["${element.name}":ctx.jsonElements["${element.name}"]]
		org.json.JSONObject result = new org.json.JSONObject(map)
		for (String node : ctx.arrayNodes) {
			JsonUtil.getInstance().replaceObjectWithArray(result, node, false)
		}
		result.toString()
	}

	void createElement(Element element, JsonCreatorContext ctx) {
		if(element.ref) {
			element.schema.getElement(element.ref).create(this, ctx)
			return
		}
		def type
		if(!element.type) {
			type = element.embeddedType
		} else {
			type = element.schema.getType(element.type) ?: element.embeddedType
		}
		if(type instanceof BuiltInSchemaType){
			if (element.maxOccurs == "unbounded")
				ctx.jsonElements[element.name] = Collections.singletonList(TemplateUtil.getTemplateValue(type))
			else
				ctx.jsonElements[element.name] = TemplateUtil.getTemplateValue(type)

			return
		}
		if (element.maxOccurs == "unbounded"){
			if (!ctx.arrayNodes.contains(element.name))
				ctx.arrayNodes.add(element.name)
			else
				return
		}
		ctx.element = element
		type.create(this, ctx)
	}

	void createSequence(Sequence sequence, JsonCreatorContext ctx){
		sequence.particles.each {
			if(it instanceof Element){
				it.create(this, ctx)
				if(ctx.jsonElements[ctx.element.name]) {
					ctx.jsonElements[ctx.element.name] << ["${it.name}" : ctx.jsonElements[it.name]]
				} else {
					ctx.jsonElements[ctx.element.name] = ["${it.name}" : ctx.jsonElements[it.name]]
				}
			}
			else it.create(this, ctx)
		}
	}
}
