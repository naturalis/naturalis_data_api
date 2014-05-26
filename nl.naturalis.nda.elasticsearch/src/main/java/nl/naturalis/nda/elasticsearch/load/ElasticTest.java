package nl.naturalis.nda.elasticsearch.load;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;

public class ElasticTest {

	private String dbname = "ElasticSearch";
	private String index = "indextest";
	private String type = "table";
	private Client client = null;
	private Node node = null;

	public ElasticTest(){
	    this.node = nodeBuilder().local(true).node();
	    this.client = node.client();

	    if(isIndexExist(index)){
	        deleteIndex(this.client, index);
	        createIndex(index);
	    }
	    else{
	        createIndex(index);
	    }

	    System.out.println("mapping structure before data insertion");
	    getMappings();
	    System.out.println("----------------------------------------");
	    createData();
	    System.out.println("mapping structure after data insertion");
	    getMappings();



	}

	public void getMappings() {
	    client.admin().indices().prepareCreate("");
//	    IndexMetaData inMetaData = clusterState.getMetaData().index(index);
//	    MappingMetaData metad = inMetaData.mapping(type);
//
//	    if (metad != null) {
//	        try {
//	            String structure = metad.getSourceAsMap().toString();
//	            System.out.println(structure);
//
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }

	}

	private void createIndex(String index) {
	    XContentBuilder typemapping = buildJsonMappings();
	    String mappingstring = null;
	    try {
	        mappingstring = buildJsonMappings().string();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }

	    client.admin().indices().create(new CreateIndexRequest(index)
	                  .mapping(type, typemapping)).actionGet();

	    //try put mapping after index creation
	    /*
	     * PutMappingResponse response = null; try { response =
	     * client.admin().indices() .preparePutMapping(index) .setType(type)
	     * .setSource(typemapping.string()) .execute().actionGet(); } catch
	     * (ElasticSearchException e) { e.printStackTrace(); } catch
	     * (IOException e) { e.printStackTrace(); }
	     */

	}

	private void deleteIndex(Client client, String index) {
	    try {
	        DeleteIndexResponse delete = client.admin().indices()
	                .delete(new DeleteIndexRequest(index)).actionGet();
	        if (!delete.isAcknowledged()) {
	        } else {
	        }
	    } catch (Exception e) {
	    }
	}

	private XContentBuilder buildJsonMappings(){
	    XContentBuilder builder = null; 
	    try {
	        builder = XContentFactory.jsonBuilder();
	        builder.startObject()
	        .startObject("properties")
	            .startObject("ATTR1")
	                .field("type", "string")
	                .field("store", "yes")
	                .field("index", "analyzed")
	             .endObject()
	           .endObject()
	        .endObject();           
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return builder;
	}

	private boolean isIndexExist(String index) {
	    ActionFuture<IndicesExistsResponse> exists = client.admin().indices()
	            .exists(new IndicesExistsRequest(index));
	    IndicesExistsResponse actionGet = exists.actionGet();

	    return actionGet.isExists();
	}

	private void createData(){
	    System.out.println("Data creation");
	    IndexResponse response=null;
	    for (int i=0;i<10;i++){
	        Map<String, Object> json = new HashMap<String, Object>();
	        json.put("ATTR1", "new value" + i);
	        response = this.client.prepareIndex(index, type)
	                .setSource(json)
	                .setOperationThreaded(false)
	                .execute()
	                .actionGet();
	    }
	    String _index = response.getIndex();
	    String _type = response.getType();
	    long _version = response.getVersion();
	    System.out.println("Index : "+_index+"   Type : "+_type+"   Version : "+_version);
	    System.out.println("----------------------------------");
	}

	public static void main(String[] args)
	{
	    new ElasticTest();
	}
	}