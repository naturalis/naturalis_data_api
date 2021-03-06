{
    "index": {
            "number_of_shards": 1,
            "number_of_replicas": 0
    },
    "analysis": {
    	"analyzer": {
    		"case_insensitive_analyzer": {
    			"tokenizer": "keyword",
    			"filter": "lowercase"
    		},
    		"like_analyzer": {
    			"tokenizer": "like_tokenizer",
    			"filter": "lowercase"
    		}
    	},
    	"tokenizer": {
			"like_tokenizer": {
				"type": "nGram",
				"min_gram": "3",
				"max_gram": "10",
				"token_chars": ["letter", "digit"]
			}
		}
	},
	"mappings": {
		"_default_": {
			"_all": {
				"enabled": false
			}
		}
	}
}