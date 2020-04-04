package github.abnvanand.washeteria.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.abnvanand.washeteria.R;
import github.abnvanand.washeteria.data.model.Machine;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.CustomViewHolder> {

    private List<Machine> dataList;
    private Context context;

    public MachineAdapter(Context context, List<Machine> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        TextView machineName;
        TextView machineStatus;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            machineName = mView.findViewById(R.id.textViewMachineName);
            machineStatus = mView.findViewById(R.id.textViewMachineStatus);
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
        holder.machineStatus.setText(dataList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
