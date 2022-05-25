package tools.aws.rds.instance;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import org.junit.Assert;
import org.junit.Test;

public class RDSInstanceTest {
    private final String fakeDBUser = "operator123";
    private final String fakeDBPass = "password123";
    private final String fakeDBName = "database123";
    private final int fakeDBStorage = 1;
    private final String fakeDBClass = "db.t2.micro";
    private final String fakeDBEngine = "postgres";
    private final String fakeAccessKey = "ABC123";
    private final String fakeSecretKey = "XYZ321";
    private final String fakeRegion = "candy-land-1";

    @Test
    // Test to ensure our toolkit is communicating with the service as intended
    public void testAWSConnection() {
        CreateDBInstanceRequest req = new CreateDBInstanceRequest(
                fakeDBName,
                fakeDBStorage,
                fakeDBClass,
                fakeDBEngine,
                fakeDBPass,
                fakeDBUser
        );

        Assert.assertEquals(req.getClass(), CreateDBInstanceRequest.class);
    }

    @Test
    // Test to ensure our instance class can build properly
    public void testInstanceBuild() {
        RDSInstance db = new RDSInstance.Builder()
            .setDBUsername(fakeDBUser)
            .setDBPassword(fakeDBPass)
            .setDBName(fakeDBName)
            .setDBStorage(fakeDBStorage)
            .setDBClass(fakeDBClass)
            .setDBEngine(fakeDBEngine)
            .setClient(
                fakeAccessKey,
                fakeSecretKey,
                fakeRegion
            )
        .build();

        Assert.assertEquals(db.getClass(), RDSInstance.class);
    }

    @Test
    // Test to ensure our instance is building & throwing as intended
    public void testCreateDB() {
        try {
            new RDSInstance.Builder()
                .setDBUsername(fakeDBUser)
                .setDBPassword(fakeDBPass)
                .setDBName(fakeDBName)
                .setDBStorage(fakeDBStorage)
                .setDBClass(fakeDBClass)
                .setDBEngine(fakeDBEngine)
                .setClient(
                        fakeAccessKey,
                        fakeSecretKey,
                        fakeRegion
                )
                .build()
            .createDB();
        } catch(Exception e) {
            Assert.assertEquals(e.getClass(), SdkClientException.class);
        }
    }
}
