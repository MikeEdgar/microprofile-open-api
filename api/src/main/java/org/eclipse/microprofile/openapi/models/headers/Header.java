/**
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 * Copyright 2017 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.microprofile.openapi.models.headers;

import java.util.Map;

import org.eclipse.microprofile.openapi.models.Constructible;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.Reference;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.Schema;

/**
 * Header
 * <p>
 * Describes a single header parameter for an operation.
 * <p>
 *
 * @see <a href= "https://spec.openapis.org/oas/v3.1.0.html#header-object">OpenAPI Specification Header Object</a>
 */
public interface Header extends Constructible, Extensible<Header>, Reference<Header> {

    /**
     * Controls the style of serialization. Only one style is supported for headers.
     */
    public enum Style {
        SIMPLE("simple");

        private final String value;

        Style(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    /**
     * Returns the description property from a Header instance.
     *
     * @return a brief description of the header parameter.
     **/
    String getDescription();

    /**
     * Sets this Header's description property to the given string.
     *
     * @param description
     *            a brief description of the header parameter
     */
    void setDescription(String description);

    /**
     * Sets this Header's description property to the given string.
     *
     * @param description
     *            a brief description of the header parameter
     * @return the current Header instance
     */
    default Header description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Returns the required property from a Header instance.
     *
     * @return whether this parameter is mandatory
     **/
    Boolean getRequired();

    /**
     * Sets this Header's required property to the given value.
     *
     * @param required
     *            whether this parameter is mandatory
     */
    void setRequired(Boolean required);

    /**
     * Sets this Header's required property to the given value.
     *
     * @param required
     *            whether this parameter is mandatory
     * @return the current Header instance
     */
    default Header required(Boolean required) {
        setRequired(required);
        return this;
    }

    /**
     * Returns the deprecated property from a Header instance.
     *
     * @return whether the header parameter is deprecated
     **/
    Boolean getDeprecated();

    /**
     * Sets this Header's deprecated property to the given value.
     *
     * @param deprecated
     *            whether the header parameter is deprecated
     */
    void setDeprecated(Boolean deprecated);

    /**
     * Sets this Header's deprecated property to the given value.
     *
     * @param deprecated
     *            whether the header parameter is deprecated
     * @return the current Header instance
     */
    default Header deprecated(Boolean deprecated) {
        setDeprecated(deprecated);
        return this;
    }

    /**
     * Returns the allowEmptyValue property from a Header instance.
     *
     * @return the ability to pass empty-valued parameters
     **/
    Boolean getAllowEmptyValue();

    /**
     * Sets this Header's allowEmptyValue property to the given value.
     *
     * @param allowEmptyValue
     *            specify the ability to pass empty-valued parameters
     */
    void setAllowEmptyValue(Boolean allowEmptyValue);

    /**
     * Sets this Header's allowEmptyValue property to the given value.
     *
     * @param allowEmptyValue
     *            specify the ability to pass empty-valued parameters
     * @return the current Header instance
     */
    default Header allowEmptyValue(Boolean allowEmptyValue) {
        setAllowEmptyValue(allowEmptyValue);
        return this;
    }

    /**
     * Returns the style property from a Header instance.
     *
     * @return how the parameter value will be serialized
     **/
    Style getStyle();

    /**
     * Sets this Header's style property to the given style.
     *
     * @param style
     *            how the parameter value will be serialized
     */
    void setStyle(Style style);

    /**
     * Sets this Header's style property to the given style.
     *
     * @param style
     *            how the parameter value will be serialized
     * @return the current Header instance
     */
    default Header style(Style style) {
        setStyle(style);
        return this;
    }

    /**
     * Returns the explode property from a Header instance.
     *
     * @return whether parameter values of type "array" or "object" generate separate parameters for each value
     **/
    Boolean getExplode();

    /**
     * Sets this Header's explode property to the given value.
     *
     * @param explode
     *            whether parameter values of type "array" or "object" generate separate parameters for each value
     */
    void setExplode(Boolean explode);

    /**
     * Sets this Header's explode property to the given value.
     *
     * @param explode
     *            whether parameter values of type "array" or "object" generate separate parameters for each value
     * @return the current Header instance
     */
    default Header explode(Boolean explode) {
        setExplode(explode);
        return this;
    }

    /**
     * Returns the schema property from a Header instance.
     *
     * @return schema defining the type used for the header parameter
     **/
    Schema getSchema();

    /**
     * Sets this Header's schema property to the given object.
     *
     * @param schema
     *            schema defining the type used for the header parameter
     */
    void setSchema(Schema schema);

    /**
     * Sets this Header's schema property to the given object.
     *
     * @param schema
     *            schema defining the type used for the header parameter
     * @return the current Header instance
     */
    default Header schema(Schema schema) {
        setSchema(schema);
        return this;
    }

    /**
     * Returns the examples property from a Header instance.
     *
     * @return a copy Map (potentially immutable) of examples of the header
     **/
    Map<String, Example> getExamples();

    /**
     * Sets the examples property of this Header instance to the given map. Each example should contain a value in the
     * correct format as specified in the parameter encoding. The examples object is mutually exclusive of the example
     * object.
     *
     * @param examples
     *            examples of the header
     */
    void setExamples(Map<String, Example> examples);

    /**
     * Sets the examples property of this Header instance to the given map. Each example should contain a value in the
     * correct format as specified in the parameter encoding. The examples object is mutually exclusive of the example
     * object.
     *
     * @param examples
     *            examples of the header
     * @return the current Header instance
     */
    default Header examples(Map<String, Example> examples) {
        setExamples(examples);
        return this;
    }

    /**
     * Adds an example of the header using the specified key to this Header instance. The example should contain a value
     * in the correct format as specified in the parameter encoding.
     *
     * @param key
     *            string to represent the example
     * @param example
     *            example of the header. null values will be rejected (implementation will throw an exception) or
     *            ignored.
     * @return the current Header instance
     */
    Header addExample(String key, Example example);

    /**
     * Removes an example of the header using the specified key to this Header instance.
     *
     * @param key
     *            string to represent the example
     */
    void removeExample(String key);

    /**
     * Returns the example property from a Header instance.
     *
     * @return example of the header
     **/
    Object getExample();

    /**
     * Sets this Header's example property to the given object. The example should match the specified schema and
     * encoding properties if present. The examples object is mutually exclusive of the example object.
     *
     * @param example
     *            example of the header
     */
    void setExample(Object example);

    /**
     * Sets this Header's example property to the given object. The example should match the specified schema and
     * encoding properties if present. The examples object is mutually exclusive of the example object.
     *
     * @param example
     *            example of the header
     * @return the current Header instance
     */
    default Header example(Object example) {
        setExample(example);
        return this;
    }

    /**
     * Returns the content property from a Header instance.
     *
     * @return a map containing the media representations for the parameter
     **/
    Content getContent();

    /**
     * Sets this Header's content property to the given object.
     *
     * @param content
     *            a map containing the media representations for the parameter
     */
    void setContent(Content content);

    /**
     * Sets this Header's content property to the given object.
     *
     * @param content
     *            a map containing the media representations for the parameter
     * @return the current Header instance
     */
    default Header content(Content content) {
        setContent(content);
        return this;
    }

}