package client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public final class RequestPut implements IRequest {
    @Override
    public Response send(RequestInfo requestInfo) {
        logRequest(requestInfo, "PUT", true);
        Response response = given()
                .header("Token", requestInfo.getTokenString())
                .contentType(JSON)
                .accept(JSON)
                .body(requestInfo.getBody())
                .when()
                .put(requestInfo.getUrl());
        logResponse(response);
        return response;
    }
}
