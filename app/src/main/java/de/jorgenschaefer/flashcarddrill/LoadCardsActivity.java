package de.jorgenschaefer.flashcarddrill;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jorgenschaefer.flashcarddrill.db.Card;
import de.jorgenschaefer.flashcarddrill.db.CardsDbHelper;
import de.jorgenschaefer.flashcarddrill.sheets.GoogleAPIActivity;

public class LoadCardsActivity extends GoogleAPIActivity {

    private static final String[] SCOPES = {
            DriveScopes.DRIVE_METADATA_READONLY,
            SheetsScopes.SPREADSHEETS_READONLY
    };

    private RecyclerView sheetList;
    private ProgressDialog progressDialog;
    private SheetAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_cards);

        progressDialog = new ProgressDialog(this);

        sheetList = (RecyclerView) findViewById(R.id.sheet_list);
        sheetList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        sheetList.setLayoutManager(layoutManager);
        adapter = new SheetAdapter();
        sheetList.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectToGoogleAPI(
                Arrays.asList(SCOPES),
                new Runnable() {
                    @Override
                    public void run() {
                        new LoadCardsActivity.RetrieveSheetListTask(getCredential()).execute();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_load_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_load:
                new LoadCardsActivity.RetrieveSheetListTask(getCredential()).execute();
                return true;
            case R.id.action_switch_account:
                switchAccount();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.ViewHolder> {
        private List<File> files = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView item = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sheet_item, parent, false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setFile(files.get(position));
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        void addAll(List<File> files) {
            this.files.addAll(files);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView view;

            ViewHolder(TextView view) {
                super(view);
                this.view = view;
            }

            public void setFile(final File file) {
                view.setText(file.getName());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RetrieveSheetDataTask(getCredential(), file.getId()).execute();
                    }
                });
            }
        }
    }

    private class RetrieveSheetListTask extends AsyncTask<Void, Void, List<File>> {
        private com.google.api.services.drive.Drive service = null;
        private Exception lastError = null;

        RetrieveSheetListTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            service = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        @Override
        protected List<File> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                lastError = e;
                cancel(true);
                return null;
            }
        }

        private List<File> getDataFromApi() throws IOException {
            FileList response = service.files()
                    .list()
                    .setOrderBy("modifiedTime desc,name")
                    .setQ("mimeType = 'application/vnd.google-apps.spreadsheet'")
                    .setPageSize(100)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            return response.getFiles();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving sheet list ...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<File> result) {
            super.onPostExecute(result);
            progressDialog.hide();
            adapter.addAll(result);
        }

        @Override
        protected void onCancelled() {
            progressDialog.hide();
            LoadCardsActivity.this.onAPIError(lastError);
        }
    }

    private class RetrieveSheetDataTask extends AsyncTask<Void, Void, List<Card>> {
        private com.google.api.services.sheets.v4.Sheets service = null;
        private Exception lastError = null;
        private String spreadsheetId;

        RetrieveSheetDataTask(GoogleAccountCredential credential, String spreadsheetId) {
            this.spreadsheetId = spreadsheetId;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            service = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        @Override
        protected List<Card> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                lastError = e;
                cancel(true);
                return null;
            }
        }

        private List<Card> getDataFromApi() throws IOException {
            String range = "A1:B";
            List<Card> results = new ArrayList<>();
            ValueRange response = this.service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                int id = 1;
                for (List row : values) {
                    results.add(new Card(id, row.get(0).toString(), row.get(1).toString()));
                    id++;
                }
            }
            return results;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving sheet contents ...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final List<Card> cards) {
            progressDialog.hide();
            if (cards == null || cards.size() == 0) {
                Snackbar.make(sheetList, "No results returned.", Snackbar.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(LoadCardsActivity.this)
                        .setTitle("Import cards")
                        .setMessage("Do you really want to import " + cards.size() + " cards?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                CardsDbHelper dbHelper = new CardsDbHelper(getApplicationContext());
                                for (Card card : cards) {
                                    dbHelper.insertOrUpdateCard(card);
                                }
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.hide();
            LoadCardsActivity.this.onAPIError(lastError);
        }
    }
}