package github.abnvanand.washeteria.data.model;

import com.google.gson.annotations.SerializedName;

public class Machine {
    @SerializedName("machineId")
    private String machineId;
    @SerializedName("machineName")
    private String machineName;

    private String locationId;  // reverse mapping
    private String status;  // Vacant / Occupied / Out-of-Order
    private String timeLeft; // Set if status == Occupied
    // TODO: Add a list of events of this machine


    public Machine(String machineId, String machineName) {
        this.machineId = machineId;
        this.machineName = machineName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId='" + machineId + '\'' +
                ", machineName='" + machineName + '\'' +
                ", locationId='" + locationId + '\'' +
                ", status='" + status + '\'' +
                ", timeLeft='" + timeLeft + '\'' +
                '}';
    }
}
