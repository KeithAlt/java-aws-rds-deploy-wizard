package tools.aws.rds.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.AmazonRDSException;
import tools.aws.rds.utils.PropertiesHandler;

public class RDSClient {
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private AmazonRDS client;

    public RDSClient(Builder builder) {
        this.accessKey = builder.accessKey;
        this.secretKey = builder.secretKey;
        this.region = builder.region;
        this.client = builder.client;
        if (this.client == null) {
            this.authenticate();
        }
    }

    public AmazonRDS getClient() { return this.client; }
    public String getAccessKey() { return this.accessKey; }
    public String getSecretKey() { return this.secretKey; }
    public String getRegion() { return this.region; }

    // Authenticate our AWS client
    public AmazonRDS authenticate() throws AmazonRDSException {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);
        AmazonRDS rdsClient = AmazonRDSClientBuilder.standard()
                .withRegion(this.region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        this.client = rdsClient;
        return rdsClient;
    }

    public static class Builder {
        private String accessKey;
        private String secretKey;
        private String region;
        private AmazonRDS client;

        public Builder setAccessKey(String arg) {
            this.accessKey = arg;
            return this;
        }

        public Builder setSecretKey(String arg) {
            this.secretKey = arg;
            return this;
        }

        public Builder setRegion(String arg) {
            this.region = arg;
            return this;
        }

        // Use the configured values within the properties handler
        public Builder useConfigDefault() {
            this.accessKey = PropertiesHandler.getKey("AWS_ACCESS_KEY");
            this.secretKey = PropertiesHandler.getKey("AWS_SECRET_KEY");
            this.region = PropertiesHandler.getKey("AWS_REGION");
            return this;
        }

        // Use default AWS client on environment
        public Builder useDefault() throws AmazonRDSException {
            this.client = AmazonRDSClientBuilder.defaultClient();
            return this;
        }

        public RDSClient build() {
            return new RDSClient(this);
        }
    }
}
