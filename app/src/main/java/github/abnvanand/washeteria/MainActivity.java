package github.abnvanand.washeteria;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.internal.NavigationMenu;

import java.util.ArrayList;
import java.util.List;

import github.abnvanand.washeteria.adapters.MachineAdapter;
import github.abnvanand.washeteria.data.model.Location;
import github.abnvanand.washeteria.data.model.Machine;
import github.abnvanand.washeteria.ui.signin.SigninActivity;
import github.abnvanand.washeteria.viewmodel.MainViewModel;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, FabSpeedDial.MenuListener, SwipeRefreshLayout.OnRefreshListener {

    private List<Machine> machines = new ArrayList<>();
    private MachineAdapter machineAdapter;
    private MainViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private Spinner spinner;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FabSpeedDial fab = findViewById(R.id.fabSpeedDial);
        fab.setMenuListener(this);

        spinner = findViewById(R.id.locationSelector);
        spinner.setOnItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this);

        initRecyclerView();
        initViewModel();
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true); // Each item of same height save sore re-measurements
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(
                mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this)
                .get(MainViewModel.class);

        mViewModel.getLocationListObservable().observe(this,
                this::fillLocationSpinner);

        mViewModel.getMachinesListObservable().observe(this,
                machineEntities -> {
                    machines.clear();
                    if (machineEntities != null)
                        machines.addAll(machineEntities);

                    if (machineAdapter == null) {
                        machineAdapter = new MachineAdapter(
                                MainActivity.this,
                                machines);
                        mRecyclerView.setAdapter(machineAdapter);
                    } else {
                        machineAdapter.notifyDataSetChanged();
                    }

                    if (pullToRefresh.isRefreshing())
                        pullToRefresh.setRefreshing(false);
                });
    }

    private void fillLocationSpinner(List<Location> locations) {
        // Creating adapter for spinner
        ArrayAdapter<Location> locationArrayAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        locations);

        locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(locationArrayAdapter);

        Timber.d("locationArrayAdapter: %s", locationArrayAdapter);
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
        Location location = (Location) parent.getItemAtPosition(position);

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + location.getName(), Toast.LENGTH_LONG).show();

        mViewModel.getData(location.getId());
        pullToRefresh.setRefreshing(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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

    @Override
    public void onMenuClosed() {

    }

    @Override
    public void onRefresh() {
        Location location = (Location) spinner.getSelectedItem();
        mViewModel.getData(location.getId());
    }
}
