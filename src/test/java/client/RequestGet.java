package client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public final class RequestGet implements IRequest {
    @Override
    public Response send(RequestInfo requestInfo) {
        logRequest(requestInfo, "GET", false);
        Response response;
        if (requestInfo.getTokenString() != null) {
            response = given()
                    .header("Token", requestInfo.getTokenString())
                    .accept(JSON)
                    .get(requestInfo.getUrl());
        } else {
            response = given()
                    .auth()
                    .preemptive()
                    .basic(requestInfo.getUsername(), requestInfo.getPassword())
                    .accept(JSON)
                    .get(requestInfo.getUrl());
        }
        logResponse(response);
        return response;
    }
}
