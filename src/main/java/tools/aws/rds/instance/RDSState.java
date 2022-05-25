package tools.aws.rds.instance;

import java.util.Arrays;

public class RDSState {
    // States where the RDS instance has failed to create
    static String[] failedStates = new String[] {
            "deleting",
            "failed",
            "incompatible-network",
            "incompatible-parameters",
            "insufficient-capacity",
            "incompatible-restore",
            "inaccessible-encryption-credentials",
            "restore-error",
            "storage-full",
            "stopped",
            "stopping"
    };

    // Get enum state of RDS instance
    public static RDSStatus getStatus(String curState) {
        if (curState.equals("available")) {
            return RDSStatus.AVAILABLE;
        }

        if (Arrays.asList(failedStates).contains(curState)) {
            return RDSStatus.FAILED;
        }

        return RDSStatus.STARTING;
    }
}