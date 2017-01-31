/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.verticles;

import anf.updatereleaser.logic.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import javafx.collections.FXCollections;

/**
 *
 * @author Sergio
 */
public class Projects extends AbstractVerticle{
    
    private EventBus bus;
    private boolean isConected;
    
    public void start(){
        bus = vertx.eventBus();
        connectionConsumer();
    }
    
    
     
    
     
    private void connectionConsumer(){
        MessageConsumer <JsonObject> consumer = bus.consumer("isConnected");
        consumer.handler(res -> {
            JsonObject jsonIsConnected = res.body();
            isConected = jsonIsConnected.getBoolean("connected");
           
            
            
        });
    }
}
