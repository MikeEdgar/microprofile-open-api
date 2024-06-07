/**
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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

package org.eclipse.microprofile.openapi.tck;

import static org.eclipse.microprofile.openapi.tck.utils.TCKMatchers.itemOrSingleton;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.core.CombinableMatcher.either;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.collection.IsMapWithSize;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import io.restassured.response.ValidatableResponse;

public class AirlinesAppTest extends AppTestBase {
    @Deployment(name = "airlines", testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "airlines.war")
                .addPackages(true, "org.eclipse.microprofile.openapi.apps.airlines")
                .addAsManifestResource("openapi.yaml", "openapi.yaml");
    }

    @Test(dataProvider = "formatProvider")
    public void testVersion(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("openapi", startsWith("3.1."));
    }

    @Test(dataProvider = "formatProvider")
    public void testInfo(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("info.title", equalTo("AirlinesRatingApp API"));
        vr.body("info.version", equalTo("1.0"));
        vr.body("info.summary", equalTo("An API for an Airline application"));
        vr.body("info.termsOfService", equalTo("http://airlinesratingapp.com/terms"));
        vr.body("info.x-info", equalTo("test-info"));
    }

    @Test(dataProvider = "formatProvider")
    public void testContact(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("info.contact.name", equalTo("AirlinesRatingApp API Support"));
        vr.body("info.contact.url", equalTo("http://exampleurl.com/contact"));
        vr.body("info.contact.email", equalTo("techsupport@airlinesratingapp.com"));
        vr.body("info.contact.x-contact", equalTo("test-contact"));
    }

    @Test(dataProvider = "formatProvider")
    public void testLicense(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("info.license.name", equalTo("Apache 2.0"));
        vr.body("info.license.url", equalTo("http://www.apache.org/licenses/LICENSE-2.0.html"));
        vr.body("info.license.x-license", equalTo("test-license"));
    }

    @Test(dataProvider = "formatProvider")
    public void testExternalDocumentation(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("externalDocs.description", equalTo("instructions for how to deploy this app"));
        vr.body("externalDocs.url", containsString("README.md"));
        vr.body("externalDocs.x-external-docs", equalTo("test-external-docs"));
    }

    @Test(dataProvider = "formatProvider")
    public void testServer(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("servers", hasSize(2));
        vr.body("servers.url", hasSize(2));

        String url = "https://{username}.gigantic-server.com:{port}/{basePath}";
        String serverPath = "servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("The production API server"));
        vr.body(serverPath + ".variables", aMapWithSize(4));
        vr.body(serverPath + ".variables.username.description", equalTo("Reviews of the app by users"));
        vr.body(serverPath + ".variables.username.default", equalTo("user1"));
        vr.body(serverPath + ".variables.username.enum", containsInAnyOrder("user1", "user2"));
        vr.body(serverPath + ".variables.port.description", equalTo("Booking data"));
        vr.body(serverPath + ".variables.port.default", equalTo("8443"));
        vr.body(serverPath + ".variables.user.description", equalTo("User data"));
        vr.body(serverPath + ".variables.user.default", equalTo("user"));
        vr.body(serverPath + ".variables.user.x-server-variable", equalTo("test-server-variable"));
        vr.body(serverPath + ".variables.basePath.default", equalTo("v2"));
        vr.body(serverPath + ".x-server", equalTo("test-server"));

        url = "https://test-server.com:80/basePath";
        serverPath = "servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("The test API server"));

        // Testing @Servers and @Server defined on top of a class with @Server
        // defined in an operation
        // org.eclipse.microprofile.openapi.apps.airlines.resources.ReviewResource
        vr.body("paths.'/reviews/{id}'.delete.servers", hasSize(3));
        vr.body("paths.'/reviews/{id}'.delete.servers.url", hasSize(3));

        url = "https://gigantic-server.com:443";
        serverPath = "paths.'/reviews/{id}'.delete.servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("Secure server"));

        url = "http://gigantic-server.com:80";
        serverPath = "paths.'/reviews/{id}'.delete.servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("Unsecure server"));
        vr.body(serverPath + ".variables", not(hasSize(greaterThan(0))));

        url = "{protocol}://test-server.com";
        serverPath = "paths.'/reviews/{id}'.delete.servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("The production API server"));
        vr.body(serverPath + ".variables", aMapWithSize(1));
        vr.body(serverPath + ".variables.protocol.default", equalTo("https"));
        vr.body(serverPath + ".variables.protocol.enum", containsInAnyOrder("http", "https"));

        // Testing two @Server defined in an @operation annotation on a method
        // org.eclipse.microprofile.openapi.apps.airlines.resources.ReviewResource.createReview(Review)
        vr.body("paths.'/reviews'.post.servers", hasSize(2));
        vr.body("paths.'/reviews'.post.servers.url", hasSize(2));
        url = "localhost:9080/{proxyPath}/reviews/id";
        serverPath = "paths.'/reviews'.post.servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("view of all the reviews"));
        vr.body(serverPath + ".variables", aMapWithSize(1));
        vr.body(serverPath + ".variables.proxyPath.description", equalTo("Base path of the proxy"));
        vr.body(serverPath + ".variables.proxyPath.default", equalTo("proxy"));
        url = "http://random.url/reviews";
        serverPath = "paths.'/reviews'.post.servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("random text"));

        // Testing two @Server defined on top of a class
        // org.eclipse.microprofile.openapi.apps.airlines.resources.BookingResource
        vr.body("paths.'/bookings'.get.servers", hasSize(2));
        vr.body("paths.'/bookings'.get.servers.url", hasSize(2));

        url = "http://gigantic-server.com:80";
        serverPath = "paths.'/bookings'.get.servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("Unsecure server"));
        vr.body(serverPath + ".variables", not(hasSize(greaterThan(0))));

        url = "https://gigantic-server.com:443";
        serverPath = "paths.'/bookings'.get.servers.find { it.url == '" + url + "' }";
        vr.body(serverPath + ".description", equalTo("Secure server"));
        vr.body(serverPath + ".variables", not(hasSize(greaterThan(0))));
    }

    @Test(dataProvider = "formatProvider")
    public void testOperationAirlinesResource(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/'.get.summary", equalTo("Retrieve all available airlines"));
        vr.body("paths.'/'.get.operationId", equalTo("getAirlines"));
    }

    @Test(dataProvider = "formatProvider")
    public void testOperationAvailabilityResource(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/availability'.get.summary", equalTo("Retrieve all available flights"));
        vr.body("paths.'/availability'.get.operationId", equalTo("getFlights"));
        vr.body("paths.'/availability'.get.x-operation", equalTo("test-operation"));
    }

    @Test(dataProvider = "formatProvider")
    public void testRestClientNotPickedUp(String type) {
        ValidatableResponse vr = callEndpoint(type);
        // We should not be picking up interfaces annotated with @RegisterRestClient
        vr.body("paths.'/player/{playerId}'", equalTo(null));
    }

    @Test(dataProvider = "formatProvider")
    public void testOperationBookingResource(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/bookings'.get.summary", equalTo("Retrieve all bookings for current user"));
        vr.body("paths.'/bookings'.get.operationId", equalTo("getAllBookings"));

        vr.body("paths.'/bookings'.post.summary", equalTo("Create a booking"));
        vr.body("paths.'/bookings'.post.description",
                equalTo("Create a new booking record with the booking information provided."));
        vr.body("paths.'/bookings'.post.operationId", equalTo("createBooking"));

        vr.body("paths.'/bookings/{id}'.get.summary", equalTo("Get a booking with ID"));
        vr.body("paths.'/bookings/{id}'.get.operationId", equalTo("getBookingById"));

        vr.body("paths.'/bookings/{id}'.put.summary", equalTo("Update a booking with ID"));
        vr.body("paths.'/bookings/{id}'.put.operationId", equalTo("updateBookingId"));

        vr.body("paths.'/bookings/{id}'.delete.summary", equalTo("Delete a booking with ID"));
        vr.body("paths.'/bookings/{id}'.delete.operationId", equalTo("deleteBookingById"));
    }

    @Test(dataProvider = "formatProvider")
    public void testOperationReviewResource(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/reviews'.get.summary", equalTo("get all the reviews"));
        vr.body("paths.'/reviews'.get.operationId", equalTo("getAllReviews"));

        vr.body("paths.'/reviews'.post.summary", equalTo("Create a Review"));
        vr.body("paths.'/reviews'.post.operationId", equalTo("createReview"));

        vr.body("paths.'/reviews/{id}'.get.summary", equalTo("Get a review with ID"));
        vr.body("paths.'/reviews/{id}'.get.operationId", equalTo("getReviewById"));

        vr.body("paths.'/reviews/{id}'.delete.summary", equalTo("Delete a Review with ID"));
        vr.body("paths.'/reviews/{id}'.delete.operationId", equalTo("deleteReview"));

        vr.body("paths.'/reviews/users/{user}'.get.summary", equalTo("Get all reviews by user"));
        vr.body("paths.'/reviews/users/{user}'.get.operationId", equalTo("getReviewByUser"));

        vr.body("paths.'/reviews/airlines/{airline}'.get.summary", equalTo("Get all reviews by airlines"));
        vr.body("paths.'/reviews/airlines/{airline}'.get.operationId", equalTo("getReviewByAirline"));

        vr.body("paths.'/reviews/{user}/{airlines}'.get.summary", equalTo("Get all reviews for an airline by User"));
        vr.body("paths.'/reviews/{user}/{airlines}'.get.operationId", equalTo("getReviewByAirlineAndUser"));
    }

    @Test(dataProvider = "formatProvider")
    public void testOperationUserResource(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/user'.post.summary", equalTo("Create user"));
        vr.body("paths.'/user'.post.description", equalTo("This can only be done by the logged in user."));
        vr.body("paths.'/user'.post.operationId", equalTo("createUser"));

        vr.body("paths.'/user/createWithArray'.post.summary", equalTo("Creates list of users with given input array"));
        vr.body("paths.'/user/createWithArray'.post.operationId", equalTo("createUsersFromArray"));

        vr.body("paths.'/user/createWithList'.post.summary", equalTo("Creates list of users with given input list"));
        vr.body("paths.'/user/createWithList'.post.operationId", equalTo("createUsersFromList"));

        vr.body("paths.'/user/username/{username}'.put.summary", equalTo("Update user"));
        vr.body("paths.'/user/username/{username}'.put.description",
                equalTo("This can only be done by the logged in user."));
        vr.body("paths.'/user/username/{username}'.put.operationId", equalTo("updateUser"));

        vr.body("paths.'/user/username/{username}'.delete.summary", equalTo("Delete user"));
        vr.body("paths.'/user/username/{username}'.delete.description",
                equalTo("This can only be done by the logged in user."));
        vr.body("paths.'/user/username/{username}'.delete.operationId", equalTo("deleteUser"));

        vr.body("paths.'/user/username/{username}'.get.summary", equalTo("Get user by user name"));
        vr.body("paths.'/user/username/{username}'.get.operationId", equalTo("getUserByName"));

        vr.body("paths.'/user/id/{id}'.get.summary", equalTo("Get user by id"));
        vr.body("paths.'/user/id/{id}'.get.operationId", equalTo("getUserById"));

        vr.body("paths.'/user/login'.get.summary", equalTo("Logs user into the system"));
        vr.body("paths.'/user/login'.get.operationId", equalTo("logInUser"));

        vr.body("paths.'/user/logout'.get.summary", equalTo("Logs out current logged in user session"));
        vr.body("paths.'/user/logout'.get.operationId", equalTo("logOutUser"));

        vr.body("paths.'/user/username/{username}'.patch.summary", equalTo("Change user password"));
        vr.body("paths.'/user/username/{username}'.patch.description",
                equalTo("This changes the password for the logged in user."));
        vr.body("paths.'/user/username/{username}'.patch.operationId", equalTo("changePassword"));
        vr.body("paths.'/user/username/{username}'.patch.parameters", hasSize(3));

        // Operation with hidden schemas
        vr.body("paths.'/user/special'.post.requestBody.content.'application/json'.schema", is(nullValue()));
        vr.body("paths.'/user/special'.post.parameters[0].schema", is(nullValue()));
    }

    @Test(dataProvider = "formatProvider")
    public void testAPIResponse(String type) {
        ValidatableResponse vr = callEndpoint(type);
        // @APIResponse at method level
        vr.body("paths.'/availability'.get.responses", aMapWithSize(2));
        vr.body("paths.'/availability'.get.responses.'200'.description", equalTo("successful operation"));
        vr.body("paths.'/availability'.get.responses.'404'.description", equalTo("No available flights found"));

        vr.body("paths.'/bookings'.post.responses", aMapWithSize(1));
        vr.body("paths.'/bookings'.post.responses.'201'.description", equalTo("Booking created"));

        // @APIResponse at class level overridden at method level
        vr.body("paths.'/user/username/{username}'.delete.responses", aMapWithSize(3));
        vr.body("paths.'/user/username/{username}'.delete.responses.'200'.description",
                equalTo("User deleted successfully"));
        vr.body("paths.'/user/username/{username}'.delete.responses.'400'.description",
                equalTo("Invalid username supplied"));
        vr.body("paths.'/user/username/{username}'.delete.responses.'404'.description", equalTo("User not found"));

        // @APIResponse at class level combined with method level
        vr.body("paths.'/user/username/{username}'.patch.responses", aMapWithSize(2));
        vr.body("paths.'/user/username/{username}'.patch.responses.'200'.description",
                equalTo("Password was changed successfully"));
        vr.body("paths.'/user/username/{username}'.patch.responses.'400'.description", equalTo("Invalid request"));
    }

    @Test(dataProvider = "formatProvider")
    public void testAPIResponses(String type) {
        ValidatableResponse vr = callEndpoint(type);
        // @APIResponse annotations nested within @APIResponses
        vr.body("paths.'/bookings/{id}'.get.responses", aMapWithSize(2));
        vr.body("paths.'/bookings/{id}'.get.responses.'200'.description", equalTo("Booking retrieved"));
        vr.body("paths.'/bookings/{id}'.get.responses.'404'.description", equalTo("Booking not found"));

        vr.body("paths.'/user/username/{username}'.put.responses", aMapWithSize(3));
        vr.body("paths.'/user/username/{username}'.put.responses.'200'.description",
                equalTo("User updated successfully"));
        vr.body("paths.'/user/username/{username}'.put.responses.'400'.description", equalTo("Invalid user supplied"));
        vr.body("paths.'/user/username/{username}'.put.responses.'404'.description", equalTo("User not found"));

        // @APIResponses on body combined with annotations on method
        vr.body("paths.'/reviews/users/{user}'.get.responses", aMapWithSize(4));
        vr.body("paths.'/reviews/users/{user}'.get.responses.'200'.description", equalTo("Review(s) retrieved"));
        vr.body("paths.'/reviews/users/{user}'.get.responses.'404'.description", equalTo("Review(s) not found"));
        vr.body("paths.'/reviews/users/{user}'.get.responses.'429'.description", equalTo("Client is rate limited"));
        vr.body("paths.'/reviews/users/{user}'.get.responses.'500'.description", equalTo("Server error"));
    }

    @Test(dataProvider = "formatProvider")
    public void testParameter(String type) {
        ValidatableResponse vr = callEndpoint(type);
        testAvailabilityGetParamater(vr);
        testBookingIdMethods(vr);
        testReviewIdMethods(vr);
        testUserLoginMethods(vr);
        testParameterWithObjectAndStyle(vr);
    }

    private void testParameterWithObjectAndStyle(ValidatableResponse vr) {
        String headParameters = "paths.'/zepplins'.head.parameters";
        String getParameters = "paths.'/zepplins'.get.parameters";

        vr.body(headParameters, hasSize(1));
        vr.body(getParameters, hasSize(1));

        vr.body(headParameters + "[0].schema.type", equalTo("object"));
        vr.body(getParameters + "[0].schema.type", equalTo("object"));

        vr.body(headParameters + "[0].style", equalTo("spaceDelimited"));
        vr.body(getParameters + "[0].style", equalTo("pipeDelimited"));
    }

    private void testUserLoginMethods(ValidatableResponse vr) {
        String reviewParameters = "paths.'/user/login'.get.parameters";
        vr.body(reviewParameters, hasSize(2));
        vr.body(reviewParameters + ".findAll { it }.name", hasItems("username", "password"));
        List<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"username", "The user name for login"});
        list.add(new String[]{"password", "The password for login in clear text"});

        for (int i = 0; i < list.size(); i++) {
            String currentParam = list.get(i)[0];
            String query = reviewParameters + ".findAll { it.name == '" + currentParam + "' }";

            vr.body(query + ".in", both(hasSize(1)).and(contains("query")));
            vr.body(query + ".description", both(hasSize(1)).and(contains(list.get(i)[1])));
            vr.body(query + ".required", both(hasSize(1)).and(contains(true)));
            vr.body(query + ".schema.type", both(hasSize(1)).and(contains(itemOrSingleton("string"))));
        }
    }

    private void testReviewIdMethods(ValidatableResponse vr) {
        String reviewParameters = "paths.'/reviews/{id}'.get.parameters";
        vr.body(reviewParameters, hasSize(1));
        vr.body(reviewParameters + ".findAll { it }.name", contains("id"));
        vr.body(reviewParameters + ".findAll { it.name == 'id' }.in", both(hasSize(1)).and(contains("path")));
        vr.body(reviewParameters + ".findAll { it.name == 'id' }.description",
                both(hasSize(1)).and(contains("ID of the booking")));
        vr.body(reviewParameters + ".findAll { it.name == 'id' }.required", both(hasSize(1)).and(contains(true)));
        vr.body(reviewParameters + ".findAll { it.name == 'id' }.content.'*/*'.schema.type",
                both(hasSize(1)).and(contains(itemOrSingleton("integer"))));
    }

    private void testBookingIdMethods(ValidatableResponse vr) {
        String bookingParameters = "paths.'/bookings/{id}'.%s.parameters";

        for (String method : new String[]{"put", "delete", "get"}) {
            bookingParameters = String.format(bookingParameters, method);

            vr.body(bookingParameters, hasSize(1));
            vr.body(bookingParameters + ".findAll { it }.name", contains("id"));
            vr.body(bookingParameters + ".findAll { it.name == 'id' }.required", both(hasSize(1)).and(contains(true)));
            vr.body(bookingParameters + ".findAll { it.name == 'id' }.schema.type",
                    both(hasSize(1)).and(contains(itemOrSingleton("integer"))));
        }

        bookingParameters = "paths.'/bookings/{id}'.get.parameters";
        vr.body(bookingParameters + ".findAll { it.name == 'id' }.style", both(hasSize(1)).and(contains("simple")));

    }

    private void testAvailabilityGetParamater(ValidatableResponse vr) {
        String availabilityParameters = "paths.'/availability'.get.parameters";

        vr.body(availabilityParameters, hasSize(6));
        vr.body(availabilityParameters + ".findAll { it }.name",
                hasItems("airportFrom", "returningDate", "airportTo", "numberOfAdults", "numberOfChildren"));

        List<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"airportFrom", "Airport the customer departs from"});
        list.add(new String[]{"returningDate", "Customer return date"});
        list.add(new String[]{"airportTo", "Airport the customer returns to"});
        list.add(new String[]{"numberOfAdults", "Number of adults on the flight"});
        list.add(new String[]{"numberOfChildren", "Number of children on the flight"});

        for (int i = 0; i < list.size(); i++) {
            String currentParam = list.get(i)[0];
            String query = availabilityParameters + ".findAll { it.name == '" + currentParam + "' }";

            vr.body(query + ".in", both(hasSize(1)).and(contains("query")));
            vr.body(query + ".description", both(hasSize(1)).and(contains(list.get(i)[1])));
            vr.body(query + ".required", both(hasSize(1)).and(contains(true)));
            vr.body(query + ".schema.type", both(hasSize(1)).and(contains(itemOrSingleton("string"))));
        }

        vr.body(availabilityParameters + ".findAll { it.name == 'numberOfAdults' }.schema.minimum",
                both(hasSize(1)).and(contains(0)));
        vr.body(availabilityParameters + ".findAll { it.name == 'numberOfChildren' }.schema.minimum",
                both(hasSize(1)).and(contains(0)));
        vr.body(availabilityParameters + ".findAll { it.name == 'airportFrom' }.x-parameter",
                contains("test-parameter"));

        vr.body(availabilityParameters + ".findAll { it.$ref == '#/components/parameters/departureDate'}",
                notNullValue());
    }

    @Test(dataProvider = "formatProvider")
    public void testExplode(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String explode =
                "paths.'/user/username/{username}'.put.responses.'200'.content.'application/xml'.encoding.password.explode";
        vr.body(explode, equalTo(true));
        explode =
                "paths.'/user/username/{username}'.put.responses.'200'.content.'application/xml'.encoding.password.explode";
        vr.body(explode, equalTo(true));
    }

    @Test(dataProvider = "formatProvider")
    public void testCallbackAnnotations(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String endpoint = "paths.'/streams'.post.callbacks";
        vr.body(endpoint, hasKey("onData"));
        vr.body(endpoint + ".onData", hasKey("{$request.query.callbackUrl}/data"));

        endpoint = "paths.'/reviews'.post.callbacks";
        vr.body(endpoint, hasKey("testCallback"));
        vr.body(endpoint + ".testCallback", hasKey("http://localhost:9080/oas3-airlines/reviews"));
        vr.body(endpoint + ".testCallback.x-callback", equalTo("test-callback"));

        endpoint = "paths.'/bookings'.post.callbacks";
        vr.body(endpoint, hasKey("bookingCallback"));
        vr.body(endpoint + ".'bookingCallback'", hasKey("http://localhost:9080/airlines/bookings"));

        endpoint = "components.callbacks.UserEvents";
        vr.body(endpoint, hasKey("http://localhost:9080/users/events"));
        vr.body(endpoint + ".'http://localhost:9080/users/events'.$ref", equalTo("#/components/pathItems/UserEvent"));
    }

    @Test(dataProvider = "formatProvider")
    public void testCallbackOperationAnnotations(String type) {
        ValidatableResponse vr = callEndpoint(type);

        // TODO: cover /streams endpoint
        String endpoint =
                "paths.'/bookings'.post.callbacks.'bookingCallback'.'http://localhost:9080/airlines/bookings'";
        vr.body(endpoint, hasKey("get"));
        vr.body(endpoint + ".get.summary", equalTo("Retrieve all bookings for current user"));
        vr.body(endpoint + ".get.responses.'200'.description", equalTo("Bookings retrieved"));
        vr.body(endpoint + ".get.responses.'200'.content.'application/json'.schema.type", itemOrSingleton("array"));

        endpoint = "paths.'/reviews'.post.callbacks.testCallback.'http://localhost:9080/oas3-airlines/reviews'";
        vr.body(endpoint, hasKey("get"));
        vr.body(endpoint + ".get.summary", equalTo("Get all reviews"));
        vr.body(endpoint + ".get.responses.'200'.description", equalTo("successful operation"));
        vr.body(endpoint + ".get.responses.'200'.content.'application/json'.schema.type", itemOrSingleton("array"));
        vr.body(endpoint + ".get.responses.'200'.content.'application/json'.schema.items", notNullValue());
        vr.body(endpoint + ".get.x-callback-operation", equalTo("test-callback-operation"));
    }

    @Test(dataProvider = "formatProvider")
    public void testRequestBodyAnnotations(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String endpoint = "paths.'/bookings'.post.requestBody";
        vr.body(endpoint + ".description", equalTo("Create a new booking with the provided information."));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".x-request-body", equalTo("test-request-body"));
        vr.body(endpoint + ".required", equalTo(true));

        // PUT method with entity parameter but no @RequestBody annotation
        endpoint = "paths.'/bookings/{id}'.put.requestBody";
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", equalTo(true));

        // GET method without @RequestBody annotation
        endpoint = "paths.'/bookings/{id}'.get.requestBody";
        vr.body(endpoint, nullValue());

        endpoint = "paths.'/user'.post.requestBody";
        vr.body(endpoint + ".description", equalTo("Record of a new user to be created in the system."));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", equalTo(true));

        endpoint = "paths.'/user/username/{username}'.put.requestBody";
        vr.body(endpoint + ".description", equalTo("Record of a new user to be created in the system."));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", either(nullValue()).or(equalTo(false)));

        endpoint = "paths.'/user/createWithArray'.post.requestBody";
        vr.body(endpoint + ".description", equalTo("Array of user object"));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", equalTo(true));

        endpoint = "paths.'/user/createWithList'.post.requestBody";
        vr.body(endpoint + ".description", equalTo("List of user object"));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", equalTo(true));

        endpoint = "components.requestBodies.review";
        vr.body(endpoint + ".description", equalTo("example review to add"));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", equalTo(true));

        endpoint = "components.requestBodies.nonRequiredReview";
        vr.body(endpoint + ".description", equalTo("example non-required review"));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", either(nullValue()).or(equalTo(false)));

        endpoint = "components.requestBodies.requiredReview";
        vr.body(endpoint + ".description", equalTo("example required review"));
        vr.body(endpoint + ".content", notNullValue());
        vr.body(endpoint + ".required", equalTo(true));
    }

    @Test(dataProvider = "formatProvider")
    public void testSecurityRequirement(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("security", containsInAnyOrder(
                allOf(
                        aMapWithSize(1),
                        hasEntry(equalTo("airlinesRatingApp_auth"), empty())),
                allOf(
                        aMapWithSize(2),
                        hasEntry(equalTo("testScheme1"), empty()),
                        hasEntry(equalTo("testScheme2"), empty()))));

        vr.body("paths.'/reviews'.post.security.reviewoauth2[0][0]", equalTo("write:reviews"));
        vr.body("paths.'/reviews'.post.security.reviewoauth2", hasSize(1));

        vr.body("paths.'/bookings'.post.security.bookingSecurityScheme[0][0]", equalTo("write:bookings"));
        vr.body("paths.'/bookings'.post.security.bookingSecurityScheme[0][1]", equalTo("read:bookings"));
        vr.body("paths.'/reviews'.post.security.bookingSecurityScheme", hasSize(1));
        vr.body("paths.'/bookings'.post.security.bookingSecurityScheme[0]", hasSize(2));

        vr.body("paths.'/user'.post.security", hasSize(1));
        vr.body("paths.'/user'.post.security[0].keySet()", contains("httpSchemeForTest"));
        vr.body("paths.'/user'.post.security[0].httpSchemeForTest", hasSize(0));

        vr.body("paths.'/user/login'.get.security", containsInAnyOrder(
                allOf(
                        aMapWithSize(1),
                        hasEntry(equalTo("httpTestScheme"), empty())),
                allOf(
                        aMapWithSize(2),
                        hasEntry(equalTo("testScheme1"), empty()),
                        hasEntry(equalTo("testScheme2"), empty())),
                anEmptyMap()));

        vr.body("paths.'/user/username/{username}'.patch.security", contains(
                allOf(aMapWithSize(2),
                        hasEntry(equalTo("userApiKey"), empty()),
                        hasEntry(equalTo("userBearerHttp"), empty()))));

        vr.body("paths.'/zepplins'.delete.security[0].mutualTLSScheme[0]", equalTo("zepplinScope"));
    }

    @Test(dataProvider = "formatProvider")
    public void testSecuirtyRequirementInCallback(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String callbackOpPath =
                "paths.'/reviews'.post.callbacks.testCallback.'http://localhost:9080/oas3-airlines/reviews'.get";
        vr.body(callbackOpPath + ".security", containsInAnyOrder(
                hasKey("httpTestScheme"),
                allOf(hasKey("testScheme1"), hasKey("testScheme2")),
                anEmptyMap()));
    }

    @Test(dataProvider = "formatProvider")
    public void testSecuritySchemes(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String s = "components.securitySchemes";
        vr.body(s, hasKey("httpSchemeForTest"));
        vr.body(s, hasKey("airlinesRatingApp_auth"));
        vr.body(s, hasKey("reviewoauth2"));
        vr.body(s, hasKey("bookingSecurityScheme"));
    }

    @Test(dataProvider = "formatProvider")
    public void testSecurityScheme(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String http = "components.securitySchemes.httpSchemeForTest.";
        vr.body(http + "type", equalTo("http"));
        vr.body(http + "description", equalTo("user security scheme"));
        vr.body(http + "scheme", equalTo("testScheme"));

        String booking = "components.securitySchemes.bookingSecurityScheme.";
        vr.body(booking + "type", equalTo("openIdConnect"));
        vr.body(booking + "description", equalTo("Security Scheme for booking resource"));
        vr.body(booking + "openIdConnectUrl", equalTo("http://openidconnect.com/testurl"));

        String auth = "components.securitySchemes.airlinesRatingApp_auth.";
        vr.body(auth + "type", equalTo("apiKey"));
        vr.body(auth + "description", equalTo("authentication needed to access Airlines app"));
        vr.body(auth + "name", equalTo("api_key"));
        vr.body(auth + "in", equalTo("header"));
        vr.body(auth + "x-security-scheme", equalTo("test-security-scheme"));

        String reviewoauth2 = "components.securitySchemes.reviewoauth2.";
        vr.body(reviewoauth2 + "type", equalTo("oauth2"));
        vr.body(reviewoauth2 + "description", equalTo("authentication needed to create and delete reviews"));

        String mutualTLS = "components.securitySchemes.mutualTLSScheme.";
        vr.body(mutualTLS + "type", equalTo("mutualTLS"));
        vr.body(mutualTLS + "description", equalTo("mutualTLS authentication needed to manage zepplins"));
    }

    @Test(dataProvider = "formatProvider")
    public void testOAuthFlows(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String t = "components.securitySchemes.reviewoauth2.flows";
        vr.body(t, hasKey("implicit"));
        vr.body(t, hasKey("authorizationCode"));
        vr.body(t, hasKey("password"));
        vr.body(t, hasKey("clientCredentials"));
        vr.body(t + ".x-oauth-flows", equalTo("test-oauth-flows"));
    }

    @Test(dataProvider = "formatProvider")
    public void testOAuthFlow(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String implicit = "components.securitySchemes.reviewoauth2.flows.implicit.";
        vr.body(implicit + "authorizationUrl", equalTo("https://example.com/api/oauth/dialog"));
        vr.body(implicit + "x-oauth-flow", equalTo("test-oauth-flow"));

        String authCode = "components.securitySchemes.reviewoauth2.flows.authorizationCode.";
        vr.body(authCode + "authorizationUrl", equalTo("https://example.com/api/oauth/dialog"));
        vr.body(authCode + "tokenUrl", equalTo("https://example.com/api/oauth/token"));

        String password = "components.securitySchemes.reviewoauth2.flows.password.";
        vr.body(password + "refreshUrl", equalTo("https://example.com/api/oauth/refresh"));

        String client = "components.securitySchemes.reviewoauth2.flows.clientCredentials.";
        vr.body(client + "tokenUrl", equalTo("https://example.com/api/oauth/token"));
    }

    @Test(dataProvider = "formatProvider")
    public void testOAuthScope(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String implicit = "components.securitySchemes.reviewoauth2.flows.implicit.";
        vr.body(implicit + "scopes.'write:reviews'", equalTo("create a review"));

        String client = "components.securitySchemes.reviewoauth2.flows.clientCredentials.";
        vr.body(client + "scopes.'read:reviews'", equalTo("search for a review"));
    }

    @Test(dataProvider = "formatProvider")
    public void testEncodingRequestBody(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String s = "paths.'/user'.post.requestBody.content.'application/json'.encoding.email.";
        vr.body(s + "contentType", equalTo("text/plain"));
        vr.body(s + "style", equalTo("form"));
        vr.body(s + "explode", equalTo(true));
        vr.body(s + "allowReserved", equalTo(true));
        vr.body(s + "x-encoding", equalTo("test-encoding"));
    }

    @Test(dataProvider = "formatProvider")
    public void testEncodingResponses(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String s =
                "paths.'/user/username/{username}'.put.responses.'200'.content.'application/json'.encoding.password.";
        vr.body(s + "contentType", equalTo("text/plain"));
        vr.body(s + "style", equalTo("form"));
        vr.body(s + "explode", equalTo(true));
        vr.body(s + "allowReserved", equalTo(true));

        String t = "paths.'/user/username/{username}'.put.responses.'200'.content.'application/xml'.encoding.password.";
        vr.body(t + "contentType", equalTo("text/plain"));
        vr.body(t + "style", equalTo("form"));
        vr.body(t + "explode", equalTo(true));
        vr.body(t + "allowReserved", equalTo(true));
    }

    @Test(dataProvider = "formatProvider")
    public void testLink(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String s = "paths.'/user/id/{id}'.get.responses.'200'.links.'User name'.";
        vr.body(s + "operationId", equalTo("getUserByName"));
        vr.body(s + "description", equalTo("The username corresponding to provided user id"));
        vr.body(s + "x-link", equalTo("test-link"));

        String t = "paths.'/user/id/{id}'.get.responses.'200'.links.Review.";
        vr.body(t + "operationRef", equalTo("#/paths/~1reviews~1users~1{user}/get"));
        vr.body(t + "description", equalTo("The reviews provided by user"));

        String k = "paths.'/reviews'.post.responses.'201'.links.Review.";
        vr.body(k + "operationId", equalTo("getReviewById"));
        vr.body(k + "description", equalTo("get the review that was added"));
    }

    @Test(dataProvider = "formatProvider")
    public void testLinkParameter(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String s = "paths.'/user/id/{id}'.get.responses.'200'.links.'User name'.";
        vr.body(s + "parameters.userId", equalTo("$request.path.id"));

        String t = "paths.'/user/id/{id}'.get.responses.'200'.links.Review.";
        vr.body(t + "parameters.'path.user'", equalTo("$response.body#userName"));

        String k = "paths.'/reviews'.post.responses.'201'.links.Review.";
        vr.body(k + "parameters.reviewId", equalTo("$request.path.id"));
    }

    @Test(dataProvider = "formatProvider")
    public void testSchema(String type) {
        ValidatableResponse vr = callEndpoint(type);

        // Basic properties
        vr.body("components.schemas.AirlinesRef.$ref", equalTo("#/components/schemas/Airlines"));
        vr.body("components.schemas.Airlines.title", equalTo("Airlines"));
        vr.body("components.schemas.Airlines.$comment", equalTo("This is an airline"));
        vr.body("components.schemas.Airlines.x-schema", equalTo("test-schema"));
        vr.body("paths.'/bookings'.post.responses.'201'.content.'application/json'.schema.type",
                itemOrSingleton("string"));
        vr.body("components.schemas.id.format", equalTo("int32"));
        vr.body("paths.'/bookings'.post.responses.'201'.content.'application/json'.schema.description",
                equalTo("id of the new booking"));
        vr.body("components.schemas.User.properties.password.examples", contains("bobSm37"));

        // Object properties
        vr.body("paths.'/user'.post.requestBody.content.'application/json'.schema.maxProperties", equalTo(1024));
        vr.body("paths.'/user'.post.requestBody.content.'application/json'.schema.minProperties", equalTo(1));
        vr.body("components.schemas.User.required", hasItems("id", "username", "password")); // requiredProperties
        vr.body("components.schemas.User", not(hasItem("undocumentedProperty"))); // hidden property
        vr.body("components.schemas.Gender.enum", hasItems("Male", "Female", "Other"));
        vr.body("components.schemas.User.dependentRequired", aMapWithSize(1));
        vr.body("components.schemas.User.dependentRequired.frequentFlyerNumber",
                contains("frequentFlyerProgrammeName", "frequentFlyerStartDate"));
        String photoPath = dereference(vr, "components.schemas.User.properties.photo");
        vr.body(photoPath + ".contentEncoding", equalTo("base64"));
        vr.body(photoPath + ".contentMediaType", equalTo("image/jpeg"));
        String humanPath = dereference(vr, "components.schemas.User.properties.human");
        vr.body(humanPath + ".const", equalTo(true));
        vr.body("components.schemas.User.properties.freeformNotes", equalTo(true));
        vr.body("components.schemas.User.dependentSchemas", aMapWithSize(1));
        vr.body("components.schemas.User.dependentSchemas.forbiddenField", equalTo(false));

        // Array properties
        String createSchema = "paths.'/user/createWithArray'.post.requestBody.content.'application/json'.schema";
        vr.body(createSchema + ".type", containsInAnyOrder("array", "null"));
        vr.body(createSchema + ".writeOnly", equalTo(true));
        vr.body(createSchema + ".maxItems", equalTo(20));
        vr.body(createSchema + ".minItems", equalTo(2));
        vr.body(createSchema + ".uniqueItems", equalTo(true));
    }

    @Test(dataProvider = "formatProvider")
    public void testSchemaProperty(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("components.schemas.User.properties", IsMapWithSize.aMapWithSize(16));
        vr.body("components.schemas.User.properties.phone.examples", contains("123-456-7891"));
        vr.body("components.schemas.User.properties.phone.description",
                equalTo("Telephone number to contact the user"));
        vr.body("components.schemas.User.properties.phone.x-schema-property", equalTo("test-schema-property"));
    }

    @Test(dataProvider = "formatProvider")
    public void testSchemaPropertyValuesOverrideClassPropertyValues(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("components.schemas.User.properties", IsMapWithSize.aMapWithSize(16));
        vr.body("components.schemas.User.properties.phone.examples", not(contains("123-456-7890")));
        vr.body("components.schemas.User.properties.phone.examples", contains("123-456-7891"));
    }

    @Test(dataProvider = "formatProvider")
    public void testExampleObject(String type) {
        ValidatableResponse vr = callEndpoint(type);
        // Example in Components
        vr.body("components.examples.review.summary", equalTo("External review example"));
        vr.body("components.examples.review.description", equalTo("This example exemplifies the content on our site."));
        vr.body("components.examples.review.externalValue", equalTo("http://foo.bar/examples/review-example.json"));
        vr.body("components.examples.review.x-example-object", equalTo("test-example-object"));

        // Example in Parameter Content
        vr.body("paths.'/reviews/users/{user}'.get.parameters.find{ it.name=='user'}.content.'*/*'.examples.example.value",
                equalTo("bsmith"));
    }

    @Test(dataProvider = "formatProvider")
    public void testContentExampleAttribute(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/reviews/{user}/{airlines}'.get.parameters.find{it.name=='airlines'}.content.'*/*'.example",
                equalTo("Acme Air"));
    }

    @Test(dataProvider = "formatProvider")
    public void testTagDeclarations(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String tagsPath = "tags.find { it.name == '";
        String desc = "' }.description";
        vr.body(tagsPath + "user" + desc, equalTo("Operations about user"));
        vr.body(tagsPath + "create" + desc, equalTo("Operations about create"));
        vr.body(tagsPath + "Airlines" + desc, equalTo("All the airlines methods"));
        vr.body(tagsPath + "Availability" + desc, equalTo("All the availability methods"));
        vr.body(tagsPath + "Get Flights" + desc, equalTo("method to retrieve all flights available"));
        vr.body(tagsPath + "Get Flights" + "' }.externalDocs.description",
                equalTo("A list of all the flights offered by the app"));
        vr.body(tagsPath + "Get Flights" + "' }.externalDocs.url", equalTo("http://airlinesratingapp.com/ourflights"));
        vr.body(tagsPath + "Bookings" + desc, equalTo("All the bookings methods"));
        vr.body(tagsPath + "Reservations" + desc, equalTo("All the reservation methods"));
        vr.body(tagsPath + "Reviews" + desc, equalTo("All the review methods"));
        vr.body(tagsPath + "Ratings" + desc, equalTo("All the ratings methods"));
        vr.body(tagsPath + "Bookings" + "' }.x-tag", equalTo("test-tag"));
    }

    @Test(dataProvider = "formatProvider")
    public void testTagsInOperations(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/availability'.get.tags", containsInAnyOrder("Get Flights", "Availability"));
        vr.body("paths.'/bookings'.get.tags", containsInAnyOrder("bookings"));
        vr.body("paths.'/bookings'.post.tags", containsInAnyOrder("Bookings", "Reservations"));
        vr.body("paths.'/bookings/{id}'.get.tags", containsInAnyOrder("Bookings", "Reservations"));
        vr.body("paths.'/bookings/{id}'.put.tags", containsInAnyOrder("Bookings", "Reservations"));
        // no tag - class Tag was overwritten with empty Tag
        vr.body("paths.'/bookings/{id}'.delete.tags", not(hasSize(greaterThan(0))));
        vr.body("paths.'/reviews'.get.tags", containsInAnyOrder("Reviews", "Ratings"));
        vr.body("paths.'/reviews'.post.tags", containsInAnyOrder("Reviews"));
        vr.body("paths.'/reviews/{id}'.get.tags", containsInAnyOrder("Reviews", "Ratings"));
        vr.body("paths.'/reviews/{id}'.delete.tags", containsInAnyOrder("Reviews", "Ratings"));
        vr.body("paths.'/reviews/users/{user}'.get.tags", containsInAnyOrder("Reviews", "Ratings"));
        vr.body("paths.'/reviews/airlines/{airline}'.get.tags", containsInAnyOrder("Reviews", "Ratings"));
        vr.body("paths.'/reviews/{user}/{airlines}'.get.tags", containsInAnyOrder("Reviews", "Ratings"));
        vr.body("paths.'/user'.post.tags", containsInAnyOrder("user", "create"));
        vr.body("paths.'/user/createWithArray'.post.tags", containsInAnyOrder("user", "create"));
        vr.body("paths.'/user/createWithList'.post.tags", containsInAnyOrder("user", "create"));
        vr.body("paths.'/user/username/{username}'.get.tags", containsInAnyOrder("user"));
        vr.body("paths.'/user/username/{username}'.put.tags", containsInAnyOrder("user"));
        vr.body("paths.'/user/username/{username}'.delete.tags", containsInAnyOrder("user"));
        vr.body("paths.'/user/id/{id}'.get.tags", containsInAnyOrder("user"));
        // empty tag was defined
        vr.body("paths.'/user/login'.get.tags", not(hasSize(greaterThan(0))));
        // no tag was defined
        vr.body("paths.'/user/logout'.get.tags", not(hasSize(greaterThan(0))));
        vr.body("paths.'/'.get.tags", containsInAnyOrder("Airlines"));
    }

    @Test(dataProvider = "formatProvider")
    public void testComponents(String type) {
        ValidatableResponse vr = callEndpoint(type);

        // Tests to ensure that the reusable items declared using the
        // @Components annotation (within @OpenAPIDefinition) exist.
        // Each of these item types are tested elsewhere, so no need to test the
        // content of them here.
        vr.body("components.schemas.Bookings", notNullValue());
        vr.body("components.schemas.Airlines", notNullValue());
        vr.body("components.schemas.AirlinesRef", notNullValue());
        vr.body("components.responses.FoundAirlines", notNullValue());
        vr.body("components.responses.FoundBookings", notNullValue());
        vr.body("components.parameters.departureDate", notNullValue());
        vr.body("components.parameters.username", notNullValue());
        vr.body("components.examples.review", notNullValue());
        vr.body("components.examples.user", notNullValue());
        vr.body("components.requestBodies.review", notNullValue());
        vr.body("components.headers.Max-Rate", notNullValue());
        vr.body("components.headers.Request-Limit", notNullValue());
        vr.body("components.securitySchemes.httpTestScheme", notNullValue());
        vr.body("components.links.UserName", notNullValue());
        vr.body("components.callbacks.GetBookings", notNullValue());
        vr.body("components.pathItems.UserEvent", notNullValue());

        // Test an extension on the components object itself
        vr.body("components.x-components", equalTo("test-components"));
    }

    @Test(dataProvider = "formatProvider")
    public void testHeaderInAPIResponse(String type) {
        ValidatableResponse vr = callEndpoint(type);

        // Headers within APIResponse
        String responseHeader1 = "paths.'/reviews/{id}'.get.responses.'200'.headers.responseHeader1";
        vr.body(responseHeader1, notNullValue());
        vr.body(responseHeader1 + ".description", equalTo("Max rate"));
        vr.body(responseHeader1 + ".required", equalTo(true));
        vr.body(responseHeader1 + ".deprecated", equalTo(true));
        vr.body(responseHeader1 + ".allowEmptyValue", equalTo(true));
        vr.body(responseHeader1 + ".style", equalTo("simple"));
        vr.body(responseHeader1 + ".schema.type", itemOrSingleton("integer"));

        String responseHeader2 = "paths.'/reviews/{id}'.get.responses.'200'.headers.responseHeader2";
        vr.body(responseHeader2, notNullValue());
        vr.body(responseHeader2 + ".description", equalTo("Input value"));
        vr.body(responseHeader2 + ".required", equalTo(true));
        vr.body(responseHeader2 + ".deprecated", equalTo(true));
        vr.body(responseHeader2 + ".allowEmptyValue", equalTo(true));
        vr.body(responseHeader2 + ".style", equalTo("simple"));
        vr.body(responseHeader2 + ".schema.type", itemOrSingleton("string"));
    }

    @Test(dataProvider = "formatProvider")
    public void testHeaderInEncoding(String type) {
        ValidatableResponse vr = callEndpoint(type);

        // Header within Encoding
        String testHeader =
                "paths.'/user'.post.requestBody.content.'application/json'.encoding.email.headers.testHeader";
        vr.body(testHeader, notNullValue());
        vr.body(testHeader + ".description", equalTo("Minimum rate"));
        vr.body(testHeader + ".required", equalTo(true));
        vr.body(testHeader + ".deprecated", equalTo(true));
        vr.body(testHeader + ".allowEmptyValue", equalTo(true));
        vr.body(testHeader + ".style", equalTo("simple"));
        vr.body(testHeader + ".schema.type", itemOrSingleton("integer"));
    }

    @Test(dataProvider = "formatProvider")
    public void testRefHeaderInAPIResponse(String type) {
        ValidatableResponse vr = callEndpoint(type);

        // Reference to Header within APIResponse
        String responseRefHeader = "paths.'/reviews'.get.responses.'200'.headers.Request-Limit";
        vr.body(responseRefHeader, notNullValue());
        vr.body(responseRefHeader + ".$ref", equalTo("#/components/headers/Request-Limit"));
    }

    @Test(dataProvider = "formatProvider")
    public void testRefHeaderInEncoding(String type) {
        ValidatableResponse vr = callEndpoint(type);

        // Reference to Header within Encoding
        String encodingRefHeader =
                "paths.'/user/username/{username}'.put.responses.'200'.content.'application/json'.encoding.password.headers.Max-Rate";
        vr.body(encodingRefHeader, notNullValue());
        vr.body(encodingRefHeader + ".$ref", equalTo("#/components/headers/Max-Rate"));
    }

    @Test(dataProvider = "formatProvider")
    public void testHeaderInComponents(String type) {
        ValidatableResponse vr = callEndpoint(type);
        String maxRate = "components.headers.Max-Rate";
        vr.body(maxRate + ".description", equalTo("Maximum rate"));
        vr.body(maxRate + ".required", equalTo(true));
        vr.body(maxRate + ".deprecated", equalTo(true));
        vr.body(maxRate + ".allowEmptyValue", equalTo(true));
        vr.body(maxRate + ".style", equalTo("simple"));
        vr.body(maxRate + ".schema.type", itemOrSingleton("integer"));
        vr.body(maxRate + ".x-header", equalTo("test-header"));
    }

    @Test(dataProvider = "formatProvider")
    public void testContentInAPIResponse(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String content1 = "paths.'/availability'.get.responses.'200'.content.'application/json'";
        vr.body(content1, notNullValue());
        vr.body(content1 + ".schema.type", itemOrSingleton("array"));
        vr.body(content1 + ".schema.items", notNullValue());
        vr.body(content1 + ".x-content", equalTo("test-content"));

        String content2 = "paths.'/user/username/{username}'.put.responses.'200'.content";
        vr.body(content2, notNullValue());
        vr.body(content2 + ".'application/json'", notNullValue());
        vr.body(content2 + ".'application/json'.schema.$ref", equalTo("#/components/schemas/User"));
        vr.body(content2 + ".'application/json'.encoding.password", notNullValue());

        vr.body(content2, notNullValue());
        vr.body(content2 + ".'application/xml'", notNullValue());
        vr.body(content2 + ".'application/xml'.schema.$ref", equalTo("#/components/schemas/User"));
        vr.body(content2 + ".'application/xml'.encoding.password", notNullValue());
    }

    @Test(dataProvider = "formatProvider")
    public void testContentInRequestBody(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String contentJson = "paths.'/bookings'.post.requestBody.content.'application/json'";
        vr.body(contentJson, notNullValue());
        vr.body(contentJson + ".schema.$ref", equalTo("#/components/schemas/Booking"));
        vr.body(contentJson + ".examples.booking.summary", equalTo("External booking example"));
    }

    @Test(dataProvider = "formatProvider")
    public void testContentInParameter(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String content = "paths.'/reviews/users/{user}'.get.parameters.find{ it.name == 'user' }.content";
        vr.body(content, notNullValue());
        vr.body(content + ".'*/*'", notNullValue());
        vr.body(content + ".'*/*'.schema.type", itemOrSingleton("string"));
    }

    @Test(dataProvider = "formatProvider")
    public void testDefaultParameterRequirement(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String params = "paths.'/reviews/users/{user}'.get.parameters";
        vr.body(params, notNullValue());

        vr.body(params + ".find{ it.name == 'user' }", hasEntry(equalTo("in"), equalTo("path")));
        vr.body(params + ".find{ it.name == 'user' }", hasEntry(equalTo("required"), equalTo(true)));

        vr.body(params + ".find{ it.name == 'minRating' }", hasEntry(equalTo("in"), equalTo("query")));
        vr.body(params + ".find{ it.name == 'minRating' }", either(not(hasKey("required")))
                .or(hasEntry(equalTo("required"), equalTo(false))));

        vr.body(params + ".find{ it.name == 'If-Match' }", hasEntry(equalTo("in"), equalTo("header")));
        vr.body(params + ".find{ it.name == 'If-Match' }", either(not(hasKey("required")))
                .or(hasEntry(equalTo("required"), equalTo(false))));

        vr.body(params + ".find{ it.name == 'trackme' }", hasEntry(equalTo("in"), equalTo("cookie")));
        vr.body(params + ".find{ it.name == 'trackme' }", either(not(hasKey("required")))
                .or(hasEntry(equalTo("required"), equalTo(false))));
    }

    @Test(dataProvider = "formatProvider")
    public void testStaticFileDefinitions(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/streams'.post.description", equalTo("subscribes a client to receive out-of-band data"));

        final String parametersPath = "paths.'/streams'.post.parameters";
        vr.body(parametersPath, hasSize(1));
        vr.body(parametersPath + ".find{ it.name == 'callbackUrl' }.in", equalTo("query"));
        vr.body(parametersPath + ".find{ it.name == 'callbackUrl' }.required", equalTo(true));
        vr.body(parametersPath + ".find{ it.name == 'callbackUrl' }.description",
                containsString("the location where data will be sent."));
        vr.body(parametersPath + ".find{ it.name == 'callbackUrl' }.schema.type", itemOrSingleton("string"));
        vr.body(parametersPath + ".find{ it.name == 'callbackUrl' }.schema.format", equalTo("uri"));
        vr.body(parametersPath + ".find{ it.name == 'callbackUrl' }.schema.examples",
                contains("https://tonys-server.com"));

        final String responsePath = "paths.'/streams'.post.responses";
        vr.body(responsePath, aMapWithSize(1));

        final String response201Path = responsePath + ".'201'";
        vr.body(response201Path + ".description", equalTo("subscription successfully created"));
        vr.body(response201Path + ".content.'application/json'.schema.description",
                equalTo("subscription information"));
        vr.body(response201Path + ".content.'application/json'.schema.required",
                both(hasSize(1)).and(contains("subscriptionId")));
        vr.body(response201Path + ".content.'application/json'.schema.properties.subscriptionId.description",
                equalTo("this unique identifier allows management of the subscription"));
        vr.body(response201Path + ".content.'application/json'.schema.properties.subscriptionId.type",
                itemOrSingleton("string"));
        vr.body(response201Path + ".content.'application/json'.schema.properties.subscriptionId.examples",
                contains("2531329f-fb09-4ef7-887e-84e648214436"));

        final String callbacksPath = "paths.'/streams'.post.callbacks.onData.'{$request.query.callbackUrl}/data'.post";
        vr.body(callbacksPath + ".requestBody.description", equalTo("subscription payload"));
        vr.body(callbacksPath + ".requestBody.content.'application/json'.schema.properties.timestamp.type",
                itemOrSingleton("string"));
        vr.body(callbacksPath + ".requestBody.content.'application/json'.schema.properties.timestamp.format",
                equalTo("date-time"));
        vr.body(callbacksPath + ".requestBody.content.'application/json'.schema.properties.userData.type",
                itemOrSingleton("string"));

        vr.body(callbacksPath + ".responses", aMapWithSize(2));
        vr.body(callbacksPath + ".responses.'202'.description",
                both(containsString("Your server implementation should return this HTTP status code"))
                        .and(containsString("if the data was received successfully")));
        vr.body(callbacksPath + ".responses.'204'.description",
                both(containsString("Your server should return this HTTP status code if no longer interested"))
                        .and(containsString("in further updates")));

        // Test an operation with no responses
        String noResponsePath = "paths.'/streams'.get";
        vr.body(noResponsePath + ".description", equalTo("An operation without a response"));
        vr.body(noResponsePath + ".parameters[0].name", equalTo("callbackUrl"));
        vr.body(noResponsePath + ".parameters[0].description",
                equalTo("the location where data will be sent.  Must be network accessible\n"
                        + "by the source server\n"));
    }

    @Test(dataProvider = "formatProvider")
    public void testExtensionParsing(String type) {
        ValidatableResponse vr = callEndpoint(type);

        vr.body("paths.'/'.get.'x-string-property'", equalTo("string-value"));
        vr.body("paths.'/'.get.'x-boolean-property'", equalTo(Boolean.TRUE));
        vr.body("paths.'/'.get.'x-number-property'", equalTo(117));
        vr.body("paths.'/'.get.'x-object-property'.'property-1'", equalTo("value-1"));
        vr.body("paths.'/'.get.'x-object-property'.'property-3'.'prop-3-1'", equalTo(17));
        vr.body("paths.'/'.get.'x-string-array-property'[1]", equalTo("two"));
        vr.body("paths.'/'.get.'x-object-array-property'[1].name", equalTo("item-2"));
    }

    @Test(dataProvider = "formatProvider")
    public void testExceptionMappers(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("paths.'/user/id/{id}'.get.responses.'404'.description", equalTo("Not Found"));
        vr.body("paths.'/user/username/{username}'.get.responses.'404'.description", equalTo("Not Found"));

        vr.body("paths.'/user/id/{id}'.get.responses.'404'.content.'application/json'.schema", notNullValue());

        vr.body("paths.'/reviews'.post.responses.'400'.description", equalTo("The review was rejected"));
        vr.body("paths.'/reviews'.post.responses.'400'.content.'application/json'.schema", notNullValue());

        String rejectedReviewSchema =
                dereference(vr, "paths.'/reviews'.post.responses.'400'.content.'application/json'.schema");
        vr.body(rejectedReviewSchema + ".type", itemOrSingleton("object"));
        vr.body(rejectedReviewSchema + ".properties", hasKey("reason"));
    }

    @Test(dataProvider = "formatProvider")
    public void testAdditionalPropertiesDefault(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String responseSchema =
                dereference(vr, "paths.'/bookings/{id}'.get.responses.'200'", "content.'application/json'.schema");
        vr.body(responseSchema, notNullValue());
        vr.body(responseSchema, not(hasKey("additionalProperties")));
    }

    @Test(dataProvider = "formatProvider")
    public void testAdditionalPropertiesFalse(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String responseSchema =
                dereference(vr, "paths.'/bookings/{id}'.get.responses.'200'", "content.'application/json'.schema");
        vr.body(responseSchema, notNullValue());
        String ccSchema = dereference(vr, responseSchema, "properties.creditCard");

        vr.body(ccSchema + ".additionalProperties", equalTo(false));

    }

    @Test(dataProvider = "formatProvider")
    public void testAdditionalPropertiesTrue(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String responseSchema =
                dereference(vr, "paths.'/bookings/{id}'.get.responses.'200'", "content.'application/json'.schema");

        vr.body(responseSchema, notNullValue());

        String airlineSchema = dereference(vr, responseSchema, "properties.returningFlight", "properties.airline");
        vr.body(airlineSchema, hasEntry(equalTo("additionalProperties"), equalTo(true)));
    }

    @Test(dataProvider = "formatProvider")
    public void testAdditionalPropertiesTypeString(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String responseSchema =
                dereference(vr, "paths.'/bookings/{id}'.get.responses.'200'", "content.'application/json'.schema");
        vr.body(responseSchema, notNullValue());
        String flightSchema = dereference(vr, responseSchema, "properties.returningFlight");

        vr.body(flightSchema + ".additionalProperties.type", itemOrSingleton("string"));
    }

    @Test(dataProvider = "formatProvider")
    public void testOpenAPIDefinitionExtension(String type) {
        ValidatableResponse vr = callEndpoint(type);
        vr.body("x-openapi-definition", equalTo("test-openapi-definition"));
    }

    @Test(dataProvider = "formatProvider")
    public void testRef(String type) {
        ValidatableResponse vr = callEndpoint(type);

        vr.body("components.responses.FoundBookingsARef.$ref", equalTo("#/components/responses/FoundBookings"));
        vr.body("components.responses.FoundBookingsARef.description", equalTo("Found Bookings Reference"));

        vr.body("components.parameters.usernameARef.$ref", equalTo("#/components/parameters/username"));
        vr.body("components.parameters.usernameARef.description", equalTo("username reference"));

        vr.body("components.examples.userARef.$ref", equalTo("#/components/examples/user"));
        vr.body("components.examples.userARef.description", equalTo("User reference"));
        vr.body("components.examples.userARef.summary", equalTo("Referenced example"));

        vr.body("components.requestBodies.reviewARef.$ref", equalTo("#/components/requestBodies/review"));
        vr.body("components.requestBodies.reviewARef.description", equalTo("Review reference"));

        vr.body("components.headers.Request-Limit-ARef.$ref", equalTo("#/components/headers/Request-Limit"));
        vr.body("components.headers.Request-Limit-ARef.description", equalTo("Request-Limit reference"));

        vr.body("components.securitySchemes.httpTestSchemeARef.$ref",
                equalTo("#/components/securitySchemes/httpTestScheme"));
        vr.body("components.securitySchemes.httpTestSchemeARef.description", equalTo("httpTestScheme reference"));

        vr.body("components.links.UserNameARef.$ref", equalTo("#/components/links/UserName"));
        vr.body("components.links.UserNameARef.description", equalTo("UserName reference"));

        vr.body("components.callbacks.GetBookingsARef.$ref",
                equalTo("#/components/callbacks/GetBookings"));

        vr.body("components.pathItems.UserEventARef.$ref", equalTo("#/components/pathItems/UserEvent"));
        vr.body("components.pathItems.UserEventARef.description", equalTo("UserEvent reference"));
        vr.body("components.pathItems.UserEventARef.summary", equalTo("Referenced PathItem"));
    }

    @Test(dataProvider = "formatProvider")
    public void testPathItem(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String pathItem = "components.pathItems.UserEvent";
        vr.body(pathItem + ".description", equalTo("Standard definition for receiving events about users"));
        vr.body(pathItem + ".summary", equalTo("User Event reception API"));
        vr.body(pathItem + ".put", notNullValue());
        vr.body(pathItem + ".delete", notNullValue());

        pathItem = "components.pathItems.PathItemTest";
        vr.body(pathItem + ".servers[0].url", equalTo("http://example.com"));
        vr.body(pathItem + ".parameters[0].name", equalTo("id"));
        vr.body(pathItem + ".x-pathItem", equalTo("test path item"));

        pathItem = "components.pathItems.UserEventARef";
        vr.body(pathItem + ".$ref", equalTo("#/components/pathItems/UserEvent"));
        vr.body(pathItem + ".post", notNullValue());
        vr.body(pathItem + ".post.summary", equalTo("User updated event"));
    }

    @Test(dataProvider = "formatProvider")
    public void testPathItemOperation(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String op = "components.pathItems.UserEvent.put";
        vr.body(op, notNullValue());
        vr.body(op + ".summary", equalTo("User added event"));
        vr.body(op + ".description", equalTo("A user was added"));
        vr.body(op + ".externalDocs.url", equalTo("http://example.com/docs"));
        vr.body(op + ".operationId", equalTo("userAddedEvent"));
        vr.body(op + ".parameters[0].name", equalTo("authenticated"));
        vr.body(op + ".requestBody.description", equalTo("The added user"));
        vr.body(op + ".responses.'200'.description", equalTo("Event received"));
        vr.body(op + ".responses.'429'.description", containsString("Server is too busy"));

        op = "components.pathItems.CallbackPathItem.post";
        vr.body(op, notNullValue());
        vr.body(op + ".callbacks.getBookings.$ref", equalTo("#/components/callbacks/GetBookings"));

        op = "components.pathItems.OperationTest.post";
        vr.body(op, notNullValue());
        vr.body(op + ".tags", containsInAnyOrder("create", "pathItemTest"));
        vr.body(op + ".deprecated", equalTo(true));
        vr.body(op + ".security", hasSize(2));
        vr.body(op + ".security", hasItem(anEmptyMap()));
        // JsonPath syntax sucks - this expects security to contain two items, one of which
        // maps "testScheme1" to an empty list and the other of which doesn't have a "testScheme1" entry.
        vr.body(op + ".security.testScheme1", containsInAnyOrder(emptyIterable(), nullValue()));
        vr.body(op + ".servers[0].url", equalTo("http://old.example.com/api"));
        vr.body(op + ".x-operation", equalTo("test operation"));

        // Check the new tag was created
        vr.body("tags.findAll { it.name == 'pathItemTest'}.description", contains("part of the path item tests"));
    }

    @Test(dataProvider = "formatProvider")
    public void testWebhooks(String type) {
        ValidatableResponse vr = callEndpoint(type);

        String webhook = "webhooks.bookingEvent";
        vr.body(webhook, notNullValue());
        vr.body(webhook + ".description", equalTo("Notifies about booking creation and deletion"));
        vr.body(webhook + ".summary", equalTo("Booking Events"));
        vr.body(webhook + ".put", notNullValue());
        vr.body(webhook + ".delete", notNullValue());
        vr.body(webhook + ".x-webhook", equalTo("test-webhook"));

        webhook = "webhooks.userEvent";
        vr.body(webhook, notNullValue());
        vr.body(webhook + ".$ref", equalTo("#/components/pathItems/UserEvent"));
    }
}
