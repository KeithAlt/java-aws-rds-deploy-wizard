## Java AWS RDS Deploy Wizard
✨ _Deploy AWS RDS instances & asynchronously follow-up_ ✨
_________________

**Deploy an RDS database with specific details:**
```java
  RDSInstance db = new RDSInstance.Builder()
    .setDBUsername("<DB-USER-NAME>")
    .setDBPassword("<DB-USER-PASSWORD>")
    .setDBName("<DB-NAME>")
    .setDBStorage(5)
    .setDBClass("<RDS-CLASS>")
    .setDBEngine("<DATABASE-TYPE>")
    .setClient(
        "<ACCESS-KEY>",
        "<SECRET-KEY>",
        "<REGION>"
    )
    .build()
    .createDB()
    .asyncAwaitAvailable()
    .thenApply(newDB -> {
        System.out.println(newDB.getInstanceID() + "(" + newDB.getARN() + ") " + "is now available");
        // ...
        return newDB;
    }).join();
```

**Deploy an RDS database with configured / AWS SDK defaults:**
```java
  RDSInstance db = new RDSInstance.Builder()
    .useConfigDefaults()
    .useDefaultClient()
    .build()
    .createOrConnectDB()
    .asyncAwaitAvailable()
    .thenApply(newDB -> {
        System.out.println(newDB.getInstanceID() + "(" + newDB.getARN() + ") " + "is now available");
        // ...
        return newDB;
    }).join();
```

## Dependencies
- [AWS Java SDK](https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk "AWS Java SDK")
- [Snake Yaml](https://mvnrepository.com/artifact/org.yaml/snakeyaml "SnakeYaml")
- Junit 4 _(optional testing)_

## Setup

> ### Option 1. (Recommended)
  - Login to the AWS CLI on the desired machine _(this is typically automatic on an EC2)_ & use the ``.useDefaultClient()`` builder for client authentication
  - Either configure the ``rds-config.yml`` file or specify manually the required builder fields _(as shown above)_ to build your desired AWS RDS instance

> ### Option 2.
  - Authenticate your AWS Client manually with the required builder fields as seen in ``.setClient()``
  - Either configure the ``rds-config.yml`` file or specify manually the required builder fields _(as shown above)_ to build your desired AWS RDS instance

> ### Option 3.
Configure the ``rds-config.yml`` file with all the desired AWS client credentials & RDS instance build information
```yaml
AWS_ACCESS_KEY: UNSET
AWS_SECRET_KEY: UNSET
AWS_REGION: UNSET

RDS_ID: UNSET
RDS_USERNAME: UNSET
RDS_PASSWORD: UNSET
RDS_TYPE: UNSET
RDS_ENGINE: UNSET
RDS_STORAGE: UNSET
```

**EXAMPLE CONFIG:**
```yaml
AWS_ACCESS_KEY: AKIATMGJSDAGSAHV
AWS_SECRET_KEY: J9YFIxOhEYfnmlFNLJdfsfbfasbf
AWS_REGION: us-west-1

RDS_ID: newdatabase
RDS_USERNAME: operator
RDS_PASSWORD: ohfsadjbviabfdi8hf128
RDS_TYPE: db.t2.micro
RDS_ENGINE: postgres
RDS_STORAGE: 5
```
Configuring the above file is optional **if** you intend on using the build-setter methods shown in the first above code example to authenticate into AWS.

