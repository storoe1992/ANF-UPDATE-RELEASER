/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.fxml.model;

import anf.updatereleaser.logic.Developer;
import anf.updatereleaser.logic.Project;
import java.util.LinkedList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Sergio
 */
public class ProjectFXML {
    
        private StringProperty name;
        private  StringProperty id;
        private ObservableList<DeveloperFXML> developers = FXCollections.observableArrayList();
        private ObservableList<String> versions = FXCollections.observableArrayList();
        
        public ProjectFXML(Project project){
            name = new SimpleStringProperty(project.name);
            id = new SimpleStringProperty(project.id);
            for(Developer dev : project.developers){
                DeveloperFXML devList = new DeveloperFXML(dev);
                developers.add(devList);
            }
            
            for(String versionsOnList :project.getVersions()){
               // StringProperty version = new SimpleStringProperty(versionsOnList);
                versions.add(versionsOnList);
                
            }
        }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getId() {
        return id;
    }

    public ObservableList<DeveloperFXML> getDevelopers() {
        return developers;
    }

    public ObservableList<String> getVersions() {
        return versions;
    }
    
    
        
        
    
}
