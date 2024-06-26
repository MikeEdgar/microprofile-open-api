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

package org.eclipse.microprofile.openapi.annotations.media;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This object maps payload values to a particular Schema.
 *
 * @see <a href= "https://spec.openapis.org/oas/v3.1.0.html#discriminator-object">OpenAPI Specification Discriminator
 *      Object</a>
 **/
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DiscriminatorMapping {

    /**
     * The property value that will be mapped to a Schema
     *
     * @return the property value
     **/
    String value() default "";

    /**
     * The schema that is being mapped to a property value
     *
     * @return the Schema reference
     **/
    Class<?> schema() default Void.class;

}
