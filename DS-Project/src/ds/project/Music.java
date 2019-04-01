/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.project;

/**
 *
 * @author Mahdi
 */
public class Music {

   private String track_name;
   private String singer_name;
   private String path;

    public Music(String track_name, String singer_name, String path) {
        this.path = path;
        this.track_name = track_name;
        this.singer_name = singer_name;

    }
    
    public String get_track_name(){
    return track_name;
    }
    
    public String get_singer_name(){
    return singer_name;
    }
    
    public String get_path(){
    return path;
    }
    
    
}
