package tools.aws.rds.instance;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.AmazonRDSException;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import tools.aws.rds.utils.PropertiesHandler;

public class RDSInstance {
    private final String dbInstanceIdentifier;
    private final int allocatedStorage;
    private final String dbInstanceClass;
    private final String engine;
    private final String password;
    private final String username;
    private final AmazonRDS client;
    private DBInstance database;

    public RDSInstance(Builder builder) {
        this.dbInstanceIdentifier = builder.dbInstanceIdentifier;
        this.allocatedStorage = builder.allocatedStorage;
        this.dbInstanceClass = builder.dbInstanceClass;
        this.engine = builder.engine;
        this.password = builder.password;
        this.username = builder.username;
        this.client = builder.client;
    }

    // Refresh our connection
    private RDSInstance refresh() throws AmazonRDSException {
        List<DBInstance> instances = this.client.describeDBInstances().getDBInstances();
        for (DBInstance instance: instances) {
            if (instance.getDBInstanceArn().equals(database.getDBInstanceArn())) {
                this.database = instance;
            }
        }
        return this;
    }

    // Return instance ARN
    public String getARN() throws AmazonRDSException {
        return this.refresh().database.getDBInstanceArn();
    }

    // Return instance ID name
    public String getInstanceID() throws AmazonRDSException {
        return this.refresh().database.getDBInstanceIdentifier();
    }

    // Return the current state of the database
    public String getState() throws AmazonRDSException {
        return this.refresh().database.getDBInstanceStatus();
    }

    // Return if instance is available
    public boolean isAvailable() {
        RDSStatus state = RDSState.getStatus(this.getState());
        return switch (state) {
            case AVAILABLE -> true;
            case FAILED -> throw new IllegalStateException("database failed to create");
            case STARTING -> false;
        };
    }

    // Asynchronously wait until the instance is available
    public CompletableFuture<RDSInstance> asyncAwaitAvailable() throws AmazonRDSException {
        return CompletableFuture.supplyAsync(this::awaitAvailable);
    }

    // lock thread until instance is available
    public RDSInstance awaitAvailable() throws AmazonRDSException  {
        try {
            while(!this.isAvailable()) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    // Create new or connect to an existing database
    public RDSInstance createOrConnectDB() throws AmazonRDSException {
        try {
            return this.createDB();
        } catch(AmazonRDSException e) {
            List<DBInstance> instances = this.client.describeDBInstances().getDBInstances();
            for (DBInstance instance: instances) {
                if (instance.getDBInstanceIdentifier().equals(this.dbInstanceIdentifier)) {
                    this.database = instance;
                }
            }
            return this;
        }
    }

    // Create a new database
    public RDSInstance createDB() throws AmazonRDSException {
        CreateDBInstanceRequest req = new CreateDBInstanceRequest(
                this.dbInstanceIdentifier,
                this.allocatedStorage,
                this.dbInstanceClass,
                this.engine,
                this.password,
                this.username
        );

        this.database = this.client.createDBInstance(req);
        return this;
    }

    public static class Builder {
        private String dbInstanceIdentifier;
        private int allocatedStorage;
        private String dbInstanceClass;
        private String engine;
        private String password;
        private String username;
        private AmazonRDS client;

        // Use a specific AWS client
        public Builder setClient(String accessKey, String secretKey, String region) throws AmazonRDSException {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            this.client = AmazonRDSClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
            return this;
        }

        // Use the default AWS client on the machine
        public Builder useDefaultClient() throws AmazonRDSException {
            this.client = AmazonRDSClientBuilder.defaultClient();
            return this;
        }

        // Use the default an AWS client with the values within the config/system properties
        public Builder useConfigClient() throws AmazonRDSException {
            this.client = AmazonRDSClientBuilder.defaultClient();
            return this;
        }

        // Use the configured defaults within system properties or the config yaml for startup
        public Builder useConfigDefaults() {
            this.username = PropertiesHandler.getKey("RDS_USERNAME");
            this.password = PropertiesHandler.getKey("RDS_PASSWORD");
            this.dbInstanceIdentifier = PropertiesHandler.getKey("RDS_ID");
            this.allocatedStorage = Integer.parseInt(PropertiesHandler.getKey("RDS_STORAGE"));
            this.dbInstanceClass = PropertiesHandler.getKey("RDS_TYPE");
            this.engine = PropertiesHandler.getKey("RDS_ENGINE");
            return this;
        }

        public Builder setDBName(String arg) {
            this.dbInstanceIdentifier = arg;
            return this;
        }

        public Builder setDBStorage(int num) {
            this.allocatedStorage = num;
            return this;
        }

        public Builder setDBClass(String arg) {
            this.dbInstanceClass = arg;
            return this;
        }

        public Builder setDBEngine(String arg) {
            this.engine = arg;
            return this;
        }

        public Builder setDBPassword(String arg) {
            this.password = arg;
            return this;
        }

        public Builder setDBUsername(String arg) {
            this.username = arg;
            return this;
        }

        public RDSInstance build() {
            return new RDSInstance(this);
        }
    }
}
