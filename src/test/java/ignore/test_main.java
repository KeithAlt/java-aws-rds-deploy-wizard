package ignore;

import tools.aws.rds.instance.RDSInstance;
import tools.aws.rds.utils.PropertiesHandler;

public class test_main {

    public static void main(String[] args) throws Exception {
        RDSInstance db = new RDSInstance.Builder()
            .useConfigClient()
            .useConfigDefaults()
        .build()
        .createOrConnectDB()
        .asyncAwaitAvailable()
        .thenApply(newDB -> {
            System.out.println(newDB);
            // ...
            return newDB;
        }).join();
    }
}
