package github.abnvanand.washeteria;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.internal.NavigationMenu;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import github.abnvanand.washeteria.data.adapters.MachineAdapter;
import github.abnvanand.washeteria.data.model.Location;
import github.abnvanand.washeteria.data.model.Machine;
import github.abnvanand.washeteria.network.RetrofitSingleton;
import github.abnvanand.washeteria.network.WebService;
import github.abnvanand.washeteria.ui.signin.SigninActivity;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private SimpleMenuListenerAdapter menuListener;
    MachineAdapter machineAdapter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FabSpeedDial fab = findViewById(R.id.fabSpeedDial);
        menuListener = new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                // TODO: Do something with yout menu items, or return false if you don't want to show them
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_calendar) {
                    startActivity(new Intent(MainActivity.this, DayviewActivity.class));

                }
                return false;
            }
        };
        fab.setMenuListener(menuListener);

        spinner = (Spinner) findViewById(R.id.locationSelector);
        spinner.setOnItemSelectedListener(this);


        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        WebService webService = RetrofitSingleton.getRetrofitInstance()
                .create(WebService.class);
        Call<List<Location>> call = webService.getLocations();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(@NotNull Call<List<Location>> call,
                                   @NotNull Response<List<Location>> response) {
                progressDialog.dismiss();
                // Spinner Drop down elements
                List<Location> body = response.body();

                generateLocationList(body);
                Timber.d("body.toString()%s", body.toString());
            }

            @Override
            public void onFailure(@NotNull Call<List<Location>> call,
                                  @NotNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Err!!!", Toast.LENGTH_SHORT)
                        .show();
                Timber.d(t.getLocalizedMessage());
            }
        });
    }

    private void generateLocationList(List<Location> locations) {
        recyclerView = findViewById(R.id.recyclerView);
        // Creating adapter for spinner
        ArrayAdapter<Location> locationArrayAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        locations);

        // Drop down layout style - list view with radio button
        locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(locationArrayAdapter);

        Timber.d("locationArrayAdapter: %s", locationArrayAdapter);
    }

    private void generateMachineList(List<Machine> machines) {
        recyclerView = findViewById(R.id.recyclerView);
        machineAdapter = new MachineAdapter(this, machines);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        Timber.d("machineAdapter: %s", machineAdapter);
        recyclerView.setAdapter(machineAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(
                    new Intent(MainActivity.this,
                            SigninActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        Location item = (Location) parent.getItemAtPosition(position);

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        // Call to get machines of selected location
        WebService webService = RetrofitSingleton.getRetrofitInstance()
                .create(WebService.class);

        progressDialog.show();
        Call<List<Machine>> call = webService.getMachines(item.getLocationId());
        call.enqueue(new Callback<List<Machine>>() {
            @Override
            public void onResponse(@NotNull Call<List<Machine>> call,
                                   @NotNull Response<List<Machine>> response) {
                progressDialog.dismiss();
                List<Machine> body = response.body();
                generateMachineList(body);
                Timber.d("body.toString()%s", body.toString());
            }

            @Override
            public void onFailure(@NotNull Call<List<Machine>> call,
                                  @NotNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Err!!!", Toast.LENGTH_SHORT)
                        .show();
                Timber.d(t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
