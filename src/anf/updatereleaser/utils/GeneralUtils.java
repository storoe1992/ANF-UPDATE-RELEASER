/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.utils;

import anf.updatereleaser.controller.VertxController;
import anf.updatereleaser.fxml.model.ProjectFXML;
import anf.updatereleaser.logic.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Sergio
 */
public class GeneralUtils {
    
    public static ObservableList <ProjectFXML> logicTofxml(JsonArray jsonArray){
        ObservableList <ProjectFXML> projectData = FXCollections.observableArrayList();
        try{
                   JsonArray jsonListProjects = jsonArray;
                   //System.out.println("VertxController: getProjects: jsonListProjects: Lenght: " + jsonListProjects.size());
                   List <String> object = jsonListProjects.getList();
                   for(String jsonString : object){
                       ObjectMapper mapper = new ObjectMapper();
                       Project project = mapper.readValue(jsonString, Project.class);
                       ProjectFXML projectFXML =new ProjectFXML(project);
                       projectData.add(projectFXML);
                      // System.out.println(project.name);
                       //System.out.println(project.id);
                       //System.out.println(projectData);
                       
                   }
                   }catch(Exception e){
                       e.printStackTrace();
                   }
        return projectData;
    }
    
    public static void errorMessage(JsonObject json){
        VertxController.getInstance().getVertx().eventBus().send("main.anfupdatereleasermain.info_dialog",json);
    }
    
}
