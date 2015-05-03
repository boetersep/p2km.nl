# p2km.nl
p2km.nl

P2000 monitor webapp. Polls messages from Multimon and send them using a websocket to multiple clients.
 

Elasticsearch index settings:
```javascript
curl -XPUT localhost:9200/p2000/_mapping/message -d '
{
    "message" : {
        "properties" : {
            "metadata": {
                "properties": {
                    "geodata": {
                        "properties": {
                            "location": { type: "geo_point" }
                        }
                    }
                }
            }
        }
    }
}'
```