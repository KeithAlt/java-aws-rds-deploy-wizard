package tools.aws.rds.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest {
    private final String fakeAccessKey = "ABC123";
    private final String fakeSecretKey = "XYZ321";
    private final String fakeRegion = "candy-land-1";

    @Test
    // Test to ensure our default client class is matching AWS SDK
    public void testClientDefault() {
        RDSClient testClient = new RDSClient.Builder()
                .useDefault().build();
        AmazonRDS actualClient = AmazonRDSClientBuilder.defaultClient();
        Assert.assertEquals(testClient.getClient().getClass(), actualClient.getClass());
    }

    @Test
    // Test to ensure our client is building & matching AWS SDK
    public void testClientBuild() {
        RDSClient testClient = new RDSClient.Builder()
                .setAccessKey(fakeAccessKey)
                .setSecretKey(fakeSecretKey)
                .setRegion(fakeRegion)
        .build();

        BasicAWSCredentials fakeCreds = new BasicAWSCredentials(fakeAccessKey, fakeSecretKey);
        AmazonRDS realClient = AmazonRDSClientBuilder.standard()
                .withRegion(fakeRegion)
                .withCredentials(new AWSStaticCredentialsProvider(fakeCreds))
        .build();

        Assert.assertEquals(testClient.getClient().getClass(), realClient.getClass());
    }

    @Test
    // Test to ensure our config client is building & matching AWS SDK
    public void testConfigClientDefault() {
        try {
            RDSClient testClient = new RDSClient.Builder()
                    .useConfigDefault().build();

            BasicAWSCredentials fakeCreds = new BasicAWSCredentials(fakeAccessKey, fakeSecretKey);
            AmazonRDS realClient = AmazonRDSClientBuilder.standard()
                    .withRegion(fakeRegion)
                    .withCredentials(new AWSStaticCredentialsProvider(fakeCreds))
                    .build();

            Assert.assertEquals(testClient.getClient().getClass(), realClient.getClass());
        } catch(Exception e) {
            Assert.assertTrue(e
                    .getMessage()
                    .contains("you have neither a system property or an rds-config value for")
            );
        }
    }
}
