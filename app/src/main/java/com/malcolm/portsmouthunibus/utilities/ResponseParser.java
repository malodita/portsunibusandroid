package com.malcolm.portsmouthunibus.utilities;

import com.malcolm.portsmouthunibus.models.ResponseSchema;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * The retrofit interface used to send the API request.
 */

public interface ResponseParser {

    /**
     * This is the method used to obtain a response from the directions API.
     * @param origin The start point for the request (AKA the current or last location)
     * @param destination The end point for the request
     * @param mode The mode of transport tor eport for (Only use walking)
     * @param key The directions API key
     * @return A {@link ResponseSchema ResponseSchema} object with the returned information that is
     * then parsed by the models
     */
    @GET("/maps/api/directions/json?")
    Call<ResponseSchema> getResponse(@Query("origin") String origin,
                                     @Query("destination") String destination,
                                     @Query("mode") String mode,
                                     @Query("key") String key);
}
