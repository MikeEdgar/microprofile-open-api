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

package org.eclipse.microprofile.openapi.models.tags;

import org.eclipse.microprofile.openapi.models.Constructible;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;

/**
 * Tag
 * <p>
 * An object to store metadata to be available in the OpenAPI document.
 *
 * @see <a href= "https://spec.openapis.org/oas/v3.1.0.html#tag-object">OpenAPI Specification Tag Object</a>
 */
public interface Tag extends Constructible, Extensible<Tag> {

    /**
     * Returns the name property from a Tag instance.
     *
     * @return the name property from this tag
     **/
    String getName();

    /**
     * Sets the name property of a Tag instance to the given string.
     *
     * @param name
     *            the name property for this tag
     */
    void setName(String name);

    /**
     * Sets the name property of a Tag instance to the given string.
     *
     * @param name
     *            the name property for this tag
     * @return the current Tag instance
     */
    default Tag name(String name) {
        setName(name);
        return this;
    }

    /**
     * Returns the description property from a Tag instance.
     *
     * @return the description property from this tag
     **/
    String getDescription();

    /**
     * Sets the description property of a Tag instance to the given string.
     *
     * @param description
     *            the description property for this tag
     */
    void setDescription(String description);

    /**
     * Sets the description property of a Tag instance to the given string.
     *
     * @param description
     *            the description property for this tag
     * @return the current Tag instance
     */
    default Tag description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Returns the externalDocs property from a Tag instance.
     *
     * @return additional external documentation from this tag
     **/
    ExternalDocumentation getExternalDocs();

    /**
     * Sets the externalDocs property of a Tag instance to the given object.
     *
     * @param externalDocs
     *            additional external documentation for this tag
     */
    void setExternalDocs(ExternalDocumentation externalDocs);

    /**
     * Sets the externalDocs property of a Tag instance to the given object.
     *
     * @param externalDocs
     *            additional external documentation for this tag
     * @return the current Tag instance
     */
    default Tag externalDocs(ExternalDocumentation externalDocs) {
        setExternalDocs(externalDocs);
        return this;
    }

}