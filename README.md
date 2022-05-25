## Java AWS RDS Deploy Wizard

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
