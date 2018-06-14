# Examples

## Example 1: Simple configuration
### JSON Config
*mailconfig.json*
```javascript
{
    "host": "mailserver.net",
    "user": "defuser@mailserver.net",
    "password": "defpassword"
 
    "bob": {
        "user": "bob@mailserver.net",
        "password": "bobpass"
    },
    "mark": {
        "user": "mark@mailserver.net",
        "password": "markpass"
    },
}
```

### Configuration Class Implementation
*MailConfig.java*
```java
@ConfigureMe(name = "mailconfig")
public class MailConfig {
 
    private static final MailConfig INSTANCE = new MailConfig();
 
    @Configure
    private String host;
    @Configure
    private String user;
    @Configure
    private String password;
 
    private MailConfig() {
        ConfigurationManager.INSTANCE.configure(this);
    }
 
    public static MailConfig getInstance() {
        return INSTANCE;
    }
 
    public String getHost() {
        return host;
    }
 
    public void setHost(String host) {
        this.host = host;
    }
 
    public String getUser() {
        return user;
    }
 
    public void setUser(String user) {
        this.user = user;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
}
```
### Usage
```java
String host = MailConfig.getInstance().getHost();
String user = MailConfig.getInstance().getUser();
String password = MailConfig.getInstance().getPassword();
```


## Example2: Using *@SetAll*
*@SetAll* annotation allows have every property-value pair in the configuration. Annotated target method have two required string parameters.

### JSON Config
*setall-example-config.json*
```json
{
    propertyA: 123,
    propertyB: "valueC",
    propertyC: "valueC",
}
```
### Configuration Class Implementation
*SetAllExampleConfig.java*
```java
@ConfigureMe(name = "setall-example-config")
public class SetAllExampleConfig {
 
    private static final SetAllExampleConfig INSTANCE = new SetAllExampleConfig();
 
    private Map<String, String> propertiesMap;
 
    private SetAllExampleConfig() {
        propertiesMap = new HashMap<String, String>();
        ConfigurationManager.INSTANCE.configure(this);
    }
 
    public static SetAllExampleConfig getInstance() {
        return INSTANCE;
    }
 
    @SetAll
    public void allPropertiesHandler(String name, String value) {
        propertiesMap.put(name, value);
    }
 
    public Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }
}
```
### Usage
```java
Map<String, String> allProperties = SetAllExampleConfig.getInstance().getPropertiesMap();
System.out.println(allProperties); // "{propertyC=valueC, propertyB=valueC, propertyA=123}"
```

## Example 3: Using @SetIf
This example shows how to use @SetIf annotation to configure lists and maps.
@SetIf calls the method with the name and the value of the configuration properties which match value and condition annotation parameters.
Value is a string parameter, while condition is one of the SetIfCondition enum values. There are currently 3 of them :
 * startsWith (does the key start with given annotation value)
 * contains (does the key contain given annotation value)
 * matches (does the key match given annotation value)
All of conditions are checked by calling the String methods of the same name on attribute name (so, the last condition supports regular expressions).

### JSON Config
*setif-example-config.json*
```javascript
{
    url.1 = "http://buy.server.com",
    url.2 = "http://fun.server.com",
    url.3 = "http://dev.server.com",
 
    country.UK = "United Kingdom",
    country.US = "USA",
    country.IT = "Italy",
}
```

### Configuration Class Implementation
*SetIfExampleConfig.java*
```java
@ConfigureMe(name = "setif-example-config")
public class SetIfExampleConfig {
 
    private static final String URLS_LIST_PREFIX = "url.";
    private static final String COUNTRIES_PREFIX = "country.";
 
    private static final SetIfExampleConfig INSTANCE = new SetIfExampleConfig();
 
    private List<String> urls;
    private Map<String, String> countryCodesToName;
 
    private SetIfExampleConfig() {
        urls = new ArrayList<String>();
        countryCodesToName = new HashMap<String, String>();
        ConfigurationManager.INSTANCE.configure(this);
    }
 
    public static SetIfExampleConfig getInstance() {
        return INSTANCE;
    }
 
    /**
     * Add urls list next value.
     */
    @SetIf(condition = SetIf.SetIfCondition.startsWith, value = URLS_LIST_PREFIX)
    public void putUrlsListItem(final String name, final String value) {
        urls.add(value);
    }
 
    /**
     * Add next country name by code value to map.
     */
    @SetIf(condition = SetIf.SetIfCondition.startsWith, value = COUNTRIES_PREFIX)
    public void putCountryCodesToNameValue(final String name, final String value) {
        countryCodesToName.put(name.replaceAll(COUNTRIES_PREFIX, ""), value);
    }
 
    public List<String> getUrls() {
        return urls;
    }
 
    public Map<String, String> getCountryCodesToName() {
        return countryCodesToName;
    }
}
```

### Usage 
```java
List<String> urls = SetIfExampleConfig.getInstance().getUrls();
System.out.println(urls); // "[http://dev.server.com, http://buy.server.com, http://fun.server.com]"
 
Map<String, String> countryCodeToName = SetIfExampleConfig.getInstance().getCountryCodesToName();
System.out.println(countryCodeToName); // "{US=USA, UK=United Kingdom, IT=Italy}"
```


## Example 4: Using Arrays
Supported array types: String[], boolean[], short[], int[], long[], byte[], float[], double[]
### JSON Config
*arrays-example-config.json*
```json
{
    stringArrayValue    : "str1,str2,str3",
    stringArray : ["sa1.1 " , "sa2.1,sa2.2" , "sa3"],
    floatArrayValue : "1.2,2.3, 3",
    floatArray  : [2.3, 3],
    booleanArray    : [true, false, true],
}
```

### Configuration Class Implementation
*ArraysExampleConfig.java*
```java
@ConfigureMe(name="arrays-example-config")
public class ArraysExampleConfig {
 
    private static final ArraysExampleConfig INSTANCE = new ArraysExampleConfig();
 
    private ArraysExampleConfig() {
        ConfigurationManager.INSTANCE.configure(this);
    }
 
    public static ArraysExampleConfig getInstance() {
        return INSTANCE;
    }
 
    @Configure
    private String[] stringArrayValue;
    @Configure
    private String[] stringArray;
    @Configure
    private float[] floatArrayValue;
    @Configure
    private float[] floatArray;
    @Configure
    private boolean[] booleanArray;
 
    ...
 
}
```
### Usage
```
System.out.println(Arrays.toString(ArraysExampleConfig.getInstance().getStringArrayValue()));
System.out.println(Arrays.toString(ArraysExampleConfig.getInstance().getStringArray()));
System.out.println(Arrays.toString(ArraysExampleConfig.getInstance().getFloatArrayValue()));
System.out.println(Arrays.toString(ArraysExampleConfig.getInstance().getFloatArray()));
System.out.println(Arrays.toString(ArraysExampleConfig.getInstance().getBooleanArray()));
 
// [str1, str2, str3]
// [sa1.1 , sa2.1,sa2.2, sa3]
// [1.2, 2.3, 3.0]
// [2.3, 3.0]
// [true, false, true]

```

## Example 5: Using variables
Shows the opportunity to use environment (system) property in config file
### JSON Config
*variables.json*
```json
{
    "live":{
        "variable": "${testVariable}"
    },
    "test":{
        "variable": "simple value"
    },
}
```
### Configuration Class Implementation 
*VariableConfig.class*
```
@ConfigureMe(name = "variables")
public class VariableConfig {
    @Configure
    private String variable;
   ...
}
```
### Usage
```java
System.setProperty("testVariable", "environment value") ;
System.out.println(variableConfig.getVariable());
//environment value
```

## Example 6: Using include
Shows the opportunity to include one configure file to the another one. In order to be able to use repeated parts in one file and include it to the files that need them
## Main JSON Config
*include.json*
```json
{
    live:{
        $<includedfile1>
    },
    test:{
        country: "Ukraine",
        city: "Kyiv",
    },
}
First included JSON Config
includefile1.json
```json{
    country: "Spain",
    $<includedfile2>
}
```
Second included JSON Config
includefile2.json
```json{
    city: "Barcelona",
}
```

### Configuration Class Implementation
*IncludeConfig.class*
```java
@ConfigureMe(name = "include")
public class IncludeConfig {
    @Configure
    private String country;
    @Configure
    private String city;
    ...
}
```

## Example7: Using links
Shows the opportunity to use links in file A to attribute, that localed in file B.
### Main JSON Config
*links.json*
```json
{
    inner:$<linkedattributes.innerOne>,
    live:{
        street: $<linkedattributes.street>,
        blockNumbers: $<linkedattributes.numbers>,
    },
    test:{
        street: $<linkedattributes.street>,
        blockNumbers: $<linkedattributes.numbers>,
    },
}
```

### JSON Config with linked attributes
*linkedattributes.json*
```json
{
    live:{
        street: "Live included street",
        numbers: [ 7, 6, 5],
        @innerOne:{
            innerString:"inner string Live",
        },
    },
    test:{
        street: "Test included street",
        numbers: [ 1, 2, 3],
    },
}
```

### Configuration Class Implementation
*LinksConfig.class*
```java
@ConfigureMe(name = "links")
public class LinksConfig {
    @Configure
    Inner inner;
    @Configure
    private String street;
    @Configure
    private int[] blockNumbers;
    ...
}
```

## Example8: Using ConfigureAlso attribute
Shows the opportunity to use config A in the config B, without visibly (formal) configuration of config A
## Main JSON Config
*configurealso.json*
```json
{
    simple: "global simple",
    live:{
        simple: "live simple"
    },
    test:{
        simple: "test simple"
    },
}
```

## JSON Config for internal config
*externalConfig.json*
```json
{
    externalAttribute: "external",
    live:{
        externalAttribute: "external live",
    },
    test: {
        externalAttribute: "external test",
    },
}
```
### Configuration Class Implementation
*ConfigureAlsoConfig.class*
```java
@ConfigureMe(name = "configurealso")
public class ConfigureAlsoConfig {
    @Configure
    private String simple;
    @ConfigureAlso
    private InnerConfig also;
    ...
}
```

