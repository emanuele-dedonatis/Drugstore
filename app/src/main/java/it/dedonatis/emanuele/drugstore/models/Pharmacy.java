package it.dedonatis.emanuele.drugstore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pharmacy {

    @Expose
    @SerializedName("indirizzo")
    private String indirizzo = "Piazzale Tricalle, 14";

    @Expose
    @SerializedName("descrizionefarmacia")
    private String descrizione = "COMUNALE N.3";

    @Expose
    @SerializedName("cap")
    private int cap = 66100;

    @Expose
    @SerializedName("descrizionecomune")
    private String comune = "CHIETI";

    @Expose
    @SerializedName("siglaprovincia")
    private String siglaProvincia = "CH";

    @Expose
    @SerializedName("descrizioneprovincia")
    private String provincia = "CHIETI";

    @Expose
    @SerializedName("descrizioneregione")
    private String regione = "ABRUZZO";

    @Expose
    @SerializedName("latitudine")
    private String lat = "42,3286083100631";

    @Expose
    @SerializedName("longitudine")
    private String lon = "14,0803173884522";

    public Pharmacy() {}

    @Override
    public String toString() {
        return descrizione + " - " + comune + " ("+ siglaProvincia + ")" + " " + indirizzo;
    }
}
