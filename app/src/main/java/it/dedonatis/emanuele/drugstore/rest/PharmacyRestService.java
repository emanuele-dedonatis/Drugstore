package it.dedonatis.emanuele.drugstore.rest;

import java.util.List;

import it.dedonatis.emanuele.drugstore.models.Pharmacies;
import it.dedonatis.emanuele.drugstore.models.Pharmacy;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface PharmacyRestService {

    @GET("/v1/datacatalog/Farmacie/?")
    public Call<Pharmacies> getPharmacies(@Query("$filter") String filter, @Query("format") String format);

}
