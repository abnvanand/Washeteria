package github.abnvanand.washeteria.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.internal.NavigationMenu;

import java.util.ArrayList;
import java.util.List;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.adapters.MachineAdapter;
import github.abnvanand.washeteria.databinding.ActivityMainBinding;
import github.abnvanand.washeteria.models.Location;
import github.abnvanand.washeteria.models.Machine;
import github.abnvanand.washeteria.ui.assistant.AssistantActivity;
import github.abnvanand.washeteria.ui.events.EventsForMachineActivity;
import github.abnvanand.washeteria.ui.events.ViewSlotsActivity;
import github.abnvanand.washeteria.ui.login.LoginActivity;
import github.abnvanand.washeteria.utils.ItemClickSupport;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, FabSpeedDial.MenuListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_SELECTED_LOCATION_ID = "EXTRA_CURR_LOC_ID";
    public static final String EXTRA_SELECTED_MACHINE_ID = "EXTRA_SELECTED_MACHINE";

    private ActivityMainBinding binding;

    private MainViewModel mViewModel;

    private MachineAdapter machineAdapter;
    private List<Machine> machines = new ArrayList<>();
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarInclude.toolbar);

        setupListeners();
        initRecyclerView(binding.contentMain.recyclerView);
        initViewModel();
    }

    private void setupListeners() {
        binding.fabSpeedDial.setMenuListener(this);
        binding.locationWidget.locationSelector.setOnItemClickListener(this);
        binding.contentMain.pullToRefresh.setOnRefreshListener(this);
    }

    private void initRecyclerView(RecyclerView mRecyclerView) {
        mRecyclerView.setHasFixedSize(true); // Each item of same height save sore re-measurements
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(
                mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);

        ItemClickSupport.addTo(mRecyclerView)
                .setOnItemClickListener((view, position, v) -> {
                    Intent intent = new Intent(MainActivity.this, EventsForMachineActivity.class);
                    intent.putExtra(EXTRA_SELECTED_MACHINE_ID,
                            machineAdapter.getItem(position).getId());
                    startActivity(intent);
                });
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this)
                .get(MainViewModel.class);

        mViewModel.getLocationListObservable().observe(this,
                this::fillLocations);

        mViewModel.getMachinesListObservable().observe(this,
                machineEntities -> {
                    machines.clear();
                    if (machineEntities != null)
                        machines.addAll(machineEntities);

                    if (machineAdapter == null) {
                        machineAdapter = new MachineAdapter(
                                MainActivity.this,
                                machines);
                        binding.contentMain.recyclerView.setAdapter(machineAdapter);
                    } else {
                        machineAdapter.notifyDataSetChanged();
                    }

                    if (binding.contentMain.pullToRefresh.isRefreshing())
                        binding.contentMain.pullToRefresh.setRefreshing(false);
                });
    }

    private void fillLocations(List<Location> locations) {
        ArrayAdapter<Location> locationArrayAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        locations);

        locationArrayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.locationWidget.locationSelector.setAdapter(locationArrayAdapter);
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
                            LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentLocation = (Location) parent.getItemAtPosition(position);
        Timber.d("onItemClick location: %s", currentLocation);
        mViewModel.getData(currentLocation.getId());
        binding.contentMain.pullToRefresh.setRefreshing(true);
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
            Intent intent = new Intent(MainActivity.this, ViewSlotsActivity.class);
            intent.putExtra(EXTRA_SELECTED_LOCATION_ID, currentLocation.getId());
            startActivity(intent);
        } else if (R.id.action_assistant == id) {
            startActivity(new Intent(this, AssistantActivity.class));
        }
        return false;
    }

    @Override
    public void onMenuClosed() {

    }

    @Override
    public void onRefresh() {
        mViewModel.getData(currentLocation.getId());
    }
}
