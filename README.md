# PowerPlant

# Description
This project aggregates and manages power source for virtual power plant.

# Tools Used
- Java 17
- Spring Boot
- Spring Data JPA
- H2 database
- Lombok
- Instancio
- JUnit
- Mockito

# Steps to run
- Install Java 17 (if not installed, can be installed via https://learn.microsoft.com/en-us/java/openjdk/install)
- Checkout the project locally
- Navigate to project root folder via termial
- Execute **./gradlew test** to run unit test from the terminal
- Execute **./gradlew bootRun** to run application from the terminal
- Use http://localhost:8080 as base url for accessing exposed rest api endpoints
- To setup project using IDE (preferred Intellij)
    - Open Intellij
    - Click Open and navigate to the folder where you checkout the project and click OK

# Sample curl
### Saving bulk power source (batteries)
```bash
curl --location 'localhost:8080/api/battery/bulk/load' \
--header 'Content-Type: application/json' \
--data '[
    {
        "name": "Midland",
        "postcode": "6057",
        "capacity": 50500
    },
    {
        "name": "Cannington",
        "postcode": "6107",
        "capacity": 13500
    },
    {
        "name": "Hay Street",
        "postcode": "6000",
        "capacity": 23500
    },
    {
        "name": "Mount Adams",
        "postcode": "6525",
        "capacity": 12000
    },
    {
        "name": "Koolan Island",
        "postcode": "6733",
        "capacity": 10000
    },
    {
        "name": "Armadale",
        "postcode": "6992",
        "capacity": 25000
    },
    {
        "name": "Lesmurdie",
        "postcode": "6076",
        "capacity": 13500
    },
    {
        "name": "Kalamunda",
        "postcode": "6076",
        "capacity": 13500
    },
    {
        "name": "Carmel",
        "postcode": "6076",
        "capacity": 36000
    },
    {
        "name": "Bentley",
        "postcode": "6102",
        "capacity": 85000
    },
    {
        "name": "",
        "postcode": "6102",
        "capacity": 85000
    }
]'
```

### Quering battery source using post code range
```bash
curl --location 'localhost:8080/api/battery?from_post_code=6000&to_post_code=6100'
```
#### Response: 
```json
{
    "body": {
        "data": [
            {
                "id": 9,
                "name": "Carmel",
                "postcode": "6076",
                "capacity": 36000
            },
            {
                "id": 3,
                "name": "Hay Street",
                "postcode": "6000",
                "capacity": 23500
            },
            {
                "id": 8,
                "name": "Kalamunda",
                "postcode": "6076",
                "capacity": 13500
            },
            {
                "id": 7,
                "name": "Lesmurdie",
                "postcode": "6076",
                "capacity": 13500
            },
            {
                "id": 1,
                "name": "Midland",
                "postcode": "6057",
                "capacity": 50500
            }
        ],
        "totalCapacity": 137000,
        "avgCapacity": 27400.0,
        "totalCount": 5
    },
    "message": "Fetched successfully",
    "timestamp": "2023-08-11T16:04:05.911479Z"
}
```
