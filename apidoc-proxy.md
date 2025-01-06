# Rest API Documentation for Premium Quotes

All 4xx and 5xx responses contain a problem detail according to [RFC9457](https://www.ietf.org/rfc/rfc9457.html#name-the-problem-details-json-ob).

### Base path: `/api/v1`





## Get region by ID

Returns a region with given ID.

#### Request

Path: `GET` **/region/{id}**

URL Parameters:
* `id`: UUID string

Query Parameters: `none`

Body: `none`

#### Responses

##### 200 OK

Content-Type: `application/json`

Body:
```json
{
  "type": "object",
  "properties": {
    "id": {
      "type": "string"
    },
    "postcode": {
      "type": "integer"
    },
    "state": {
      "type": "string",
      "enum": ["BW", "BY", "BE", "BB", "HB", "HH", "HE", "MV", "NI", "NW", "RP", "SL", "SN", "ST", "SH", "TH"]
    },
    "district": {
      "type": "string"
    },
    "county": {
      "type": "string"
    },
    "city": {
      "type": "string"
    },
    "area": {
      "type": "string"
    }
  }
}
```

Example:
```json
{
  "id": "e0cd6dfb-cb88-379a-9bc4-79d4ecc5bf88",
  "postcode": 1558,
  "state": "SN",
  "district": "Dresden",
  "county": "Meißen",
  "city": "Großenhain",
  "area": ""
}
```

##### 400 BAD REQUEST

Description: Returned if an invalid id is used.

Content-Type: `application/problem+json`

Example:
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Failed to convert 'id' with value: 'abc'",
  "instance": "/regions/v1/region/abc"
}
```

##### 404 NOT FOUND

Description: Returned if no region matches given id.

Content-Type: `application/problem+json`

Example:
```json
{
  "type": "about:blank",
  "title": "Region not found",
  "status": 404,
  "detail": "Region with given id not found",
  "instance": "/regions/v1/region/e0cd6dfb-cb88-379a-9bc4-79d4ecc5bf89"
}
```





## Get regions by postcode

Returns regions with given postcode.

#### Request

Path: `GET` **/region**

URL Parameters: `none`

Query Parameters:
* `postcode`: int

Body: `none`

#### Responses

##### 200 OK

Content-Type: `application/json`

Body:
```json
{
  "type": "array",
  "items": {
    "type": "object",
    "properties": {
      "id": {
        "type": "string"
      },
      "postcode": {
        "type": "integer"
      },
      "state": {
        "type": "string",
        "enum": ["BW", "BY", "BE", "BB", "HB", "HH", "HE", "MV", "NI", "NW", "RP", "SL", "SN", "ST", "SH", "TH"]
      },
      "district": {
        "type": "string"
      },
      "county": {
        "type": "string"
      },
      "city": {
        "type": "string"
      },
      "area": {
        "type": "string"
      }
    }
  }
}

```

Example:
```json
[
  {
    "id": "bc334d0b-b3d7-31d2-b1ec-f1f0b3ee0d75",
    "postcode": 1108,
    "state": "SN",
    "district": "Dresden",
    "county": "Dresden",
    "city": "Dresden",
    "area": "Marsdorf"
  },
  {
    "id": "f819daff-6ceb-3698-99c2-485ae0ec7889",
    "postcode": 1108,
    "state": "SN",
    "district": "Dresden",
    "county": "Dresden",
    "city": "Dresden",
    "area": "Weixdorf"
  }
]
```

##### 400 BAD REQUEST

Description: Returned if an invalid postcode is used.

Content-Type: `application/problem+json`

Example:
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Failed to convert 'postcode' with value: 'abc'",
  "instance": "/regions/v1/region"
}
```

##### 404 NOT FOUND

Description: Returned if no region matches given postcode.

Content-Type: `application/problem+json`

Example:
```json
{
  "type": "about:blank",
  "title": "Region not found",
  "status": 404,
  "detail": "Region with given postcode not found",
  "instance": "/regions/v1/region"
}
```





## Create car insurance premium quotes

Create insurance premium quotes for cars.
Either a factors.regionId or factors.postcode must be specified. 

#### Request

Path: `POST` **/car/quote**

URL Parameters: `none`

Query Parameters: `none`

Body:
```json
{
  "type": "object",
  "required": ["factors"]
  "properties": {
    "policies": {
      "type": "array",
      "items": {
        "type": "string"
      }
    },
    "factors": {
      "type": "object",
      "required": ["mileage", "vehicle"]
      "properties": {
        "mileage": {
          "type": "integer",
          "minimum": 0
        },
        "regionId": {
          "type": "string"
        },
        "postcode": {
          "type": "integer"
        },
        "vehicle": {
          "type": "string",
          "enum": ["CABRIO", "COMBI", "COMPACT", "COUPE", "LIMOUSINE", "MINIVAN", "PICKUP", "ROADSTER", "SUV", "VAN"]
        }
      }
    }
  }
}
```

Example:
```json
{
  "policies": ["auto_flex", "mobil_komfort"],
  "factors": {
    "postcode": 1558,
    "mileage": 1000,
    "vehicle": "CABRIO"
  }
}
```

#### Responses

##### 200 OK

Content-Type: `application/json`

Body:
```json
{
  "type": "object",
  "properties": {
    "policies": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "name": {
            "type": "string"
          },
          "premium": {
            "type": "number"
          }
        }
      }
    }
  }
}
```

Example:
```json
{
  "policies": [
    {
      "name": "auto_flex",
      "premium": 283.5000
    },
    {
      "name": "mobil_komfort",
      "premium": 567.0000
    }
  ]
}
```

##### 400 BAD REQUEST

Description: Returned if any request data is invalid or postcode is ambiguous. For ambiguous postcodes a specific regionId must be used instead.

Content-Type: `application/problem+json`

Example:
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/premiums/v1/car/quote",
  "field_errors": {
    "vehicle": "Invalid value. Must be one of [CABRIO, COMBI, COMPACT, COUPE, LIMOUSINE, MINIVAN, PICKUP, ROADSTER, SUV, VAN]",
    "mileage": "must not be null"
  }
}
```
