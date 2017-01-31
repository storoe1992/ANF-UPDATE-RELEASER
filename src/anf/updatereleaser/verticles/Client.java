/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.verticles;

import anf.updatereleaser.utils.GeneralUtils;
import anf.updatereleaser.utils.JsonMessage;
import anf.updatereleaser.utils.PropertiesUtil;
import anf.updatereleaser.utils.SSLManager;
import com.sun.deploy.util.GeneralUtil;
import io.netty.handler.codec.http.HttpUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JdkSSLEngineOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.SSLEngineOptions;
import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Sergio
 */
public class Client extends AbstractVerticle{
    
    private EventBus bus;
    private HttpClientOptions options;
    private HttpClient client;
    
    
  
    
    @Override
    public void start(){
        bus = vertx.eventBus();
        consumerCloseConnection();
        //Registering handlers EventBus
        String ip = PropertiesUtil.getInstance().getProperties().getProperty("client-ip-for-server-test");
        int port = Integer.valueOf(PropertiesUtil.getInstance().getProperties().getProperty("client-port-for-server-test"));
        
        options = new HttpClientOptions()
                                          .setDefaultHost(ip)
                                          .setDefaultPort(port)
                //.setSsl(true)
                /*.setTrustAll(true)
                .setPemTrustOptions(new PemTrustOptions()
                        .addCertValue(SSLManager.getANFRootCACertAsBuffer())
                        .addCertValue(SSLManager.getANFIntermediateCACertAsBuffer())
                )*/
                ;
        
        
        client = vertx.createHttpClient(options);
            
        requestVerifServStatus();
        requestGetProjectsInfo();
        requestSendProjectVersionToRelease();
        requestActivateLoopGetStatusTrasnfer();
        /*vertx.eventBus().send("verticles.client.server_status","Hola");
        vertx.eventBus().send("verticles.client.get_projects_info", "Hola");
        vertx.eventBus().send("verticles.client.get_projects_info", "Hola");*/
        
   }
    
    
    
    
    private void requestVerifServStatus(){
        MessageConsumer <String> consumer = bus.consumer("verticles.client.server_status");
        consumer.handler(mess ->{
            if(client!=null){
               HttpClientRequest request =  client.get("stupidURI", response ->{
                   
                    //System.out.println("ServerStatusOk: " + response.statusCode());
                    int statusCode = response.statusCode();
                    switch (statusCode){
                        case 200: mess.reply(true);break;
                        default:  System.err.println("verticles: client: server_error: " + statusCode);break;
                }
                    
                    
                });
               
               request.exceptionHandler(ex->{GeneralUtils.errorMessage(JsonMessage.jsonNoFurtherInformation());});
               request.end();
            }
        });
    }
    
    
    private void requestGetProjectsInfo(){
        MessageConsumer <JsonObject> consumer = bus.consumer("verticles.client.get_projects_info");
        consumer.handler(mess ->{
            if(client!=null){
                HttpClientRequest request = client.get("stupidURI",response->{
                    
                    if(response.statusCode() == 200){
                        response.bodyHandler(buffer ->{
                            if(buffer.length()!=0){
                        
                        //    System.out.println("Tamaño jsonArrayProjects: " + buffer.length());
                          //  System.out.println("Client: requestGetProjectsInfo: buffer:" + buffer);
                            
                            JsonArray json = new JsonArray();
                            json.readFromBuffer(0, buffer);
                            
                            //System.out.println("Client: requestGetProjectsInfo: json: Lenght: " +json.getList() );
                            mess.reply(json);
                            }
                        });
                        
                       
                    }
                } );
                request.exceptionHandler(res->{GeneralUtils.errorMessage(JsonMessage.jsonNoFurtherInformation());});
                Buffer jsonBuffer = Buffer.buffer();  
                JsonMessage.messageGetProjects().writeToBuffer(jsonBuffer);
                long size = jsonBuffer.length();
                //System.out.println("Este es le tamaño del Json " + size);
                request.headers().add("content-length", String.valueOf(size));
                request.write(jsonBuffer);
                request.end();
                
            }
            
           
            
            
        });
    }
    
    private void requestSendProjectVersionToRelease(){
        MessageConsumer<JsonObject>consumer = bus.consumer("verticles.client.send_project_version_to_release");
        consumer.handler(res->{
            JsonObject json = res.body();
            //System.out.println("verticles: Client: requestSendProjectsVersionToRelease: res.body(): " + json.encodePrettily());
            if(client!=null){
                HttpClientRequest request = client.get("stupidURI",response->{
                    int statusCode = response.statusCode();
                    switch (statusCode){
                        case 200:
                        //System.out.println("verticles: Client: requestSendProjectVersionToRelease: response.statusCode(): " + response.statusCode());
                        response.bodyHandler(totalBuffer ->{
                            JsonObject jsonBody = new JsonObject();
                            jsonBody.readFromBuffer(0, totalBuffer);
                            String type = jsonBody.getString("type");
                            switch (type){
                                case "versionProjectAlreadyReleased":functionVersionAlreadyReleased(jsonBody);break;
                                case "statusTransfer" :functionStatusTransfer(jsonBody);break;
                                case "initTransfer" : functionStatusTransfer(jsonBody);break;
                                case "onTransfer" : functionOnTransfer(jsonBody);break;
                                case "serverExploitationDown" : functionServerExploitationDown(jsonBody);break;
                            }
                            
                            
                        });
                       
                    }
            
            });
                request.exceptionHandler(ex->{GeneralUtils.errorMessage(JsonMessage.jsonNoFurtherInformation());});
                request.setChunked(true);
                Buffer buffer = Buffer.buffer();
                json.writeToBuffer(buffer);
                request.end(buffer);
                
               
            }
            
            
        });
        
    }
    
    private void functionVersionAlreadyReleased(JsonObject json){
        bus.send("controller.vertxcontroller.version_already_released", json);
    }
    
    
    private void functionStatusTransfer(JsonObject jsonBody) {
        bus.send("controllers.vertxcontroller.status_transfer", jsonBody);
    }
    
    private void requestActivateLoopGetStatusTrasnfer(){
        MessageConsumer<JsonObject>consumer = bus.consumer("verticles.client.activate_loop_get_status_transfer");
        consumer.handler(res->{
            
         vertx.setPeriodic(1000, id->{
             HttpClientRequest request = client.put("stupidURI",response->{
               
                 response.bodyHandler(body->{
                 JsonObject json = new JsonObject();
                 json.readFromBuffer(0, body);
                     String statusTransfer = json.getString("status");
                     switch (statusTransfer){
                         case "Transferencia satisfactoria":functionStatusTransfer(json); vertx.cancelTimer(id);break;
                             default:functionStatusTransfer(json);break;
                     }
                     
                 
                 });
             });
             request.exceptionHandler(ex->{GeneralUtils.errorMessage(JsonMessage.jsonNoFurtherInformation());});
             Buffer buffer = Buffer.buffer();
             JsonObject json = JsonMessage.jsonGetStatusTransfer(res.body().getString("projectId"));
             json.writeToBuffer(buffer);
             request.end(buffer);
             
             
         });
            
        });
    }
   
        
        
       public static void main(String [] args0){
        Vertx vertx = Vertx.vertx();
        Verticle tcp = new Client();
        vertx.deployVerticle(tcp);
        
        
        
        
                } 

    private void functionOnTransfer(JsonObject jsonBody) {
       bus.send("controllers.vertxcontroller.on_transfer",jsonBody);
        //System.out.println("Client: functionOnTransfer: jsonBody " + jsonBody.encodePrettily());
    }

    private void functionServerExploitationDown(JsonObject jsonBody) {
        String type = jsonBody.getString("type");
        switch (type){
            case "serverExploitationDown" :bus.send("controllers.vertxcontroller.server_exploitation_down", 
                                                        JsonMessage.jsonNoFutherInformationServerExploitation());break;
        }
    
    }
    private void consumerCloseConnection(){
        MessageConsumer<JsonObject>consumer = bus.consumer("verticles.client.shutdown_connection");
        consumer.handler(res->{
            JsonObject json = res.body();
            String type = json.getString("type");
            switch (type){
                case "shutdownConnection" : client.close();break;
                default : client.close();break;
            }
        });
    }
    
        
        
        

    
}



