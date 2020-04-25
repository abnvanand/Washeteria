package github.abnvanand.washeteria.ui.dashboard;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
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
import github.abnvanand.washeteria.models.pojo.Resource;
import github.abnvanand.washeteria.models.pojo.Status;
import github.abnvanand.washeteria.ui.assistant.AssistantActivity;
import github.abnvanand.washeteria.ui.events.EventsForMachineActivity;
import github.abnvanand.washeteria.ui.events.ViewSlotsActivity;
import github.abnvanand.washeteria.ui.login.LoginActivity;
import github.abnvanand.washeteria.utils.ItemClickSupport;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, FabSpeedDial.MenuListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    public static final String EXTRA_SELECTED_LOCATION_ID = "EXTRA_CURR_LOC_ID";

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

        mViewModel = new ViewModelProvider(this)
                .get(MainViewModel.class);

        setupListeners();
        initRecyclerView(binding.contentMain.recyclerView);
        initViewModel();
    }

    private void setupListeners() {
        binding.fabSpeedDial.setMenuListener(this);
//        binding.locationWidget.locationSelector.setOnItemClickListener(this);
        binding.locationWidget.locationSelector.setOnItemSelectedListener(this);
        binding.contentMain.pullToRefresh.setOnRefreshListener(this);

        binding.locationWidget.btnReload.setOnClickListener(v -> mViewModel.refreshLocations());
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
                    intent.putExtra(EventsForMachineActivity.EXTRA_SELECTED_MACHINE_ID,
                            machineAdapter.getItem(position).getId());
                    startActivity(intent);
                });
    }

    private void initViewModel() {

        mViewModel.getLocationListObservable().observe(this, new Observer<Resource<List<Location>>>() {
            @Override
            public void onChanged(Resource<List<Location>> listResource) {
                setLocationProgress(listResource.getStatus());

                List<Location> locations = listResource.getData();
                if (locations != null)
                    MainActivity.this.fillLocations(locations);
            }
        });

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

    private void setLocationProgress(Status status) {
        if (status == Status.ERROR) {
            binding.locationProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));
            binding.locationProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.RED));
            binding.locationProgress.setIndeterminate(false);
            binding.locationProgress.setProgress(0);
            binding.locationWidget.btnReload.setVisibility(View.VISIBLE);
        } else if (status == Status.SUCCESS) {
            binding.locationProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            binding.locationProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            binding.locationProgress.setIndeterminate(false);
            binding.locationProgress.setProgress(100);
        } else if (status == Status.LOADING) {
            binding.locationProgress.setIndeterminate(true);
            binding.locationProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            binding.locationProgress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            binding.locationProgress.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
            binding.locationWidget.btnReload.setVisibility(View.GONE);
        }
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        currentLocation = (Location) parent.getItemAtPosition(position);
        Timber.d("onItemSelected location: %s", currentLocation);
        mViewModel.getData(currentLocation.getId());
        binding.contentMain.pullToRefresh.setRefreshing(true);
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
        if (currentLocation == null) {
            Toast.makeText(this, "You must select a location.", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_calendar) {
            Intent intent = new Intent(MainActivity.this, ViewSlotsActivity.class);
            intent.putExtra(EXTRA_SELECTED_LOCATION_ID, currentLocation.getId());
            startActivity(intent);
        } else if (R.id.action_assistant == id) {
            Intent intent = new Intent(this, AssistantActivity.class);
            intent.putExtra(EXTRA_SELECTED_LOCATION_ID, currentLocation.getId());
            startActivity(intent);
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
