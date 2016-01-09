package it.dedonatis.emanuele.drugstore.utils;

/**
 * Created by mumu on 09/01/16.
 */
public class Commons {

    final static String API_BASE_URL = "http://opendatasalutedata.cloudapp.net";

        /*
    private void setupDrugsView() {
        final List<Drug> drugs = new ArrayList<Drug>();
        drugs.add(new Drug("Aspirina"));
        drugs.add(new Drug("Tachipirina"));
        drugs.add(new Drug("Montelukast"));
        drugs.add(new Drug("Revinty"));

        ListView drugsListView = (ListView)findViewById(R.id.drugs_listView);

        drugsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Drug drug = drugs.get(position);

                AlertDialog.Builder alertDialogBuilder =
                        new AlertDialog.Builder(DrugsActivity.this);

                CharSequence message = Html.fromHtml(
                        String.format(getResources().getString(R.string.click_on_movie),
                                drug.getName(),
                                drug.getPackages().size() + ""));

                alertDialogBuilder
                        .setTitle(R.string.hello_user)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();

            }
        });
        DrugArrayAdapters drugArrayAdapters = new DrugArrayAdapters(this, drugs);
        drugsListView.setAdapter(drugArrayAdapters);
    }


    private void setupPharmaciesView() {

        final ProgressDialog progressDialog =
                ProgressDialog.show(this,
                        getResources().getString(R.string.wait),
                        getResources().getString(R.string.loading_pharmacies), true, false);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


        PharmacyRestService restService = retrofit.create(PharmacyRestService.class);

        String descrizionecomune = "CHIETI";
        restService.getPharmacies("descrizionecomune eq '"+ descrizionecomune + "'","json").enqueue(
                new Callback<Pharmacies>() {

                        @Override
                        public void onResponse(Response<Pharmacies> response,
                                               Retrofit retrofit) {
                            if(response.isSuccess()) {
                                Pharmacies pharmacies = response.body();
                                Log.v(LOG_TAG, pharmacies.size() + "");
                                for(int i = 0; i < pharmacies.size(); i++)
                                    Log.v(LOG_TAG, i + ": " + pharmacies.get(i).toString());

                                progressDialog.dismiss();
                            }else {
                                Log.e(LOG_TAG, response.message());
                            }

                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.v(LOG_TAG, "FAIL: " + t.toString());
                        }
                    });

    }
    */
}
