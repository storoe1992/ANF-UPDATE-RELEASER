/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.utils;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author Sergio
 */
public class JsonMessage {
    
    public JsonMessage(){
     }
    
    public static JsonObject messageGetProjects(){
        JsonObject json = new JsonObject();
        json.put("type", "get");
        json.put("what", "projects");
        return json;
    }
    
    public static JsonObject messageGetStatusServer(boolean statusServer){
        JsonObject json = new JsonObject();
        json
                .put("type", "get")
                .put("what", "statusServer")
                .put("status", statusServer);
        return json;
    }
    
    public static JsonObject messageReleaseVersion(String projectId,String versionToRelease,String projectName){
        JsonObject json = new JsonObject();
        json
                .put("type", "send")
                .put("information", "project")
                .put("projectId", projectId)
                .put("versionToRelease", versionToRelease)
                .put("projectName",projectName);
        return json;
    }
    
    public static JsonObject jsonActivateLoopStatusTransfer(String projectId){
        JsonObject json = new JsonObject();
        json
                .put("type", "activateLoop")
                .put("projectId",projectId);
        return json;
    }
    
    public static JsonObject jsonGetStatusTransfer(String projectId){
        JsonObject json = new JsonObject();
        json
                .put("type", "status")
                .put("projectId",projectId);
        return json;
    }
    
    public static JsonObject jsonUiTableHaveNotItemSelected(){
        JsonObject json = new JsonObject();
        json
                .put("type", "ui")
                .put("message", "Seleccione un proyecto en la tabla");
        return json;
    }
    
    public static JsonObject jsonUiComboHaveNotVersionSelected(){
        JsonObject json = new JsonObject();
        json
                .put("type", "ui")
                .put("message","Seleccione alguna versión disponible");
        return json;
    }
    
    public static JsonObject jsonNoFurtherInformation(){
        JsonObject json = new JsonObject();
        json
                .put("type", "ui")
                .put("message","No se ha encontrado conexión con el servidor de prueba. Compruebe su conexión a internet");
        return json;
    }
    
    public static JsonObject jsonNoFutherInformationServerExploitation(){
        JsonObject json = new JsonObject();
        json
                .put("type","ui")
                .put("message","El servidor de prueba no puede establecer conexión con el servidor de explotación");
        return json;
    }
    
    public static JsonObject jsonShutdownConnnection(){
        JsonObject json = new JsonObject();
        json
                .put("type", "shutdownConnection");
        return json;
    }
}
