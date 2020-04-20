package github.abnvanand.washeteria.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.models.Machine;
import timber.log.Timber;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.CustomViewHolder> {

    private List<Machine> dataList;
    private Context context;

    public MachineAdapter(Context context, List<Machine> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public Machine getItem(int position) {
        return dataList.get(position);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        TextView machineName;
        TextView nextAvailableAt;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            machineName = mView.findViewById(R.id.textViewMachineName);
            nextAvailableAt = mView.findViewById(R.id.textViewMachineStatus);
        }
    }

    @NonNull
    @Override
    public MachineAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_live_status, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineAdapter.CustomViewHolder holder, int position) {
        holder.machineName.setText(dataList.get(position).getName());
        long nextAvailableAtMillis = dataList.get(position).getNextAvailableAtMillis();
        Timber.d("nextAvailableAtMillis: %s , instant now %s",
                nextAvailableAtMillis,
                Instant.now().toEpochMilli());
        Timber.d("nextAvailableAtMillis:< instant now %s",
                nextAvailableAtMillis <
                        Instant.now().toEpochMilli());
        if (nextAvailableAtMillis == 0
                || nextAvailableAtMillis < Instant.now().toEpochMilli()) {
            holder.nextAvailableAt.setText("NOW");
        } else {
            Date date = new Date(nextAvailableAtMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
            holder.nextAvailableAt.setText(sdf.format(date));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
