package it.dedonatis.emanuele.drugstore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pharmacy {

    @Expose
    @SerializedName("indirizzo")
    private String indirizzo = "indirizzo";

    @Expose
    @SerializedName("descrizionefarmacia")
    private String descrizione = "descrizione";

    @Expose
    @SerializedName("cap")
    private String cap = "00000";

    @Expose
    @SerializedName("descrizionecomune")
    private String comune = "comune";

    @Expose
    @SerializedName("siglaprovincia")
    private String siglaProvincia = "pv";

    @Expose
    @SerializedName("descrizioneprovincia")
    private String provincia = "provincia";

    @Expose
    @SerializedName("descrizioneregione")
    private String regione = "regione";

    @Expose
    @SerializedName("latitudine")
    private String lat = "0";

    @Expose
    @SerializedName("longitudine")
    private String lon = "0";

    public Pharmacy() {}

    @Override
    public String toString() {
        return descrizione + " - " + comune + " ("+ siglaProvincia + ")" + " " + indirizzo;
    }
}
