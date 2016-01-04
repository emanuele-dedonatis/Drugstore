package it.dedonatis.emanuele.drugstore.rest;

import it.dedonatis.emanuele.drugstore.models.Pharmacy;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface PharmacyRestService {

    @GET("/v1/datacatalog/Farmacie/?$")
    public Call<Pharmacy> getPharmacy(@Query("filter") String filter, @Query("format") String format);


    @GET("/?")
    public Call<Pharmacy> getMovie(@Query("i") String imdbId);
}
