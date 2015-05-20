# p2km.nl
p2km.nl

P2000 monitor webapp. Polls messages from Multimon and send them using a websocket to multiple clients.
 

Elasticsearch index settings:
PUT /p2000/_mapping/message
```javascript
{
    "message" : {
        "properties" : {
            "metadata": {
                "properties": {
                    "geodata": {
                        "properties": {
                            "location": { "type": "geo_point" }
                        }
                    },
                    "emergency": {
                        "properties": {
                            "urgency": { "type" : "string", "index" : "not_analyzed" },
                            "service": { "type" : "string", "index" : "not_analyzed" }
                        }
                    }
                }
            }
        }
    }
}
```