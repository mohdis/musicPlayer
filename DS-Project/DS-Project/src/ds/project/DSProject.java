/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.project;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Mahdi
 */
public class DSProject {

    public static Queue<Music> q; // queue for stock musics
    public static Stack<Music> stack; //stack for stock pre musics
    public static MyTreeNode<File> root; //my tree for all files and musics

    public static Music playingMusic = null;

    public static Clip clip = null;
    public static Scanner s = null;

    public static void main(String[] args) {
        // initialize q and stack
        q = new LinkedList<>();
        stack = new Stack<>();
        start();
        sortQueueByName();
        showPlayList();
        playMusicMenu();
    }

    public static void start() { //start app
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter music folder adress");
        String adress = sc.nextLine(); //get the parent folder

        File rootFolder = new File(adress);
        boolean exists = rootFolder.exists();
        if (exists) { // check is this foelder true or not
            root = new MyTreeNode<>(rootFolder);
            setChildernFromFile(rootFolder, root);
        } else {
            System.out.println("Wrong file adress");
            start();
        }

    }

    public static void showPlayList() {
        //show the music that will play
        System.out.println("Play list:");

        List<Music> listMusic = new ArrayList<>();
        int size = q.size();

        for (int i = 0; i < size; i++) {
            Music music = q.remove();
            q.add(music);
            System.out.println(music.track_name + " by " + music.singer_name);

        }
        System.out.println("");

    }

    /*
    in those two methodes we will check all files and folders and stocks musics in our q
     */
    public static void setChildernFromFile(File rootFolder, MyTreeNode<File> root) {

        ///add files in this folder
        File[] listOfFiles = rootFolder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                root.addChild(file);//here we add them to tree
                //check is music??
                if (getFileExtension(file.getName()).equals(".wav")) {
                    q.add(getMusicInfo(file));//here we add them to q
                }

            }
        }

//find subfolders and add to tree
        findSubFolders(rootFolder, root);

    }

    public static void findSubFolders(File rootFolder, MyTreeNode<File> root) {
//in this method we check all folders in parent folder and then search file on this folders
        MyTreeNode<File> newChild;

        String[] directories = rootFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        for (int i = 0; i < directories.length; i++) {

            File subFolder = new File(rootFolder.getAbsolutePath() + "\\" + directories[i].toString());
            newChild = root.addChild(subFolder);
            setChildernFromFile(subFolder, newChild);//search folders for files

        }

    }

    public static Music getMusicInfo(File file) {//get music format to make music instance
        int lastIndexOf = file.getName().lastIndexOf(".");
        int lastIndexName = file.getName().lastIndexOf("-");
        Music music = new Music(file.getName().substring(0, lastIndexName), file.getName().substring(lastIndexName + 1, lastIndexOf), file.getAbsolutePath());
        return music;
    }

    public static void sortQueueByName() {
//here we sort music by name with 2 arraylist
        List<Music> listMusic = new ArrayList<>();
        List<String> listMusicName = new ArrayList<>();
        int size = q.size();
        // System.out.println(size);

        for (int i = 0; i < size; i++) {
            Music music = q.remove();
            listMusic.add(music);
            listMusicName.add(music.track_name);
        }

        Collections.sort(listMusicName);// sort music in listMusicName  by alphabet

        for (int j = 0; j < listMusicName.size(); j++) {
            for (int k = 0; k < listMusic.size(); k++) {
                if (listMusicName.get(j).equals(listMusic.get(k).track_name)) {
                    q.add(listMusic.get(k)); //add music to q
                    listMusic.remove(k);

                    break;
                }
            }
        }
    }

    public static void sortQueueBySinger() {//like sort by name :)))))
        List<Music> listMusic = new ArrayList<>();
        List<String> listMusicSinger = new ArrayList<>();
        List<String> listMusicName = new ArrayList<>();

        int size = q.size();
        // System.out.println(size);

        for (int i = 0; i < size; i++) {
            Music music = q.remove();
            listMusic.add(music);
            listMusicSinger.add(music.singer_name);
            listMusicName.add(music.track_name);
        }

        Collections.sort(listMusicSinger);

        for (int j = 0; j < listMusicSinger.size(); j++) {
            for (int k = 0; k < listMusic.size(); k++) {
                if (listMusicSinger.get(j).equals(listMusic.get(k).singer_name)) {
                    q.add(listMusic.get(k));
                    listMusic.remove(k);
                    break;

                }
            }
        }
    }

    public static void sortQueueByShuffle() {
//shuffle music whth arraylist and random mehodes
        List<Music> listMusic = new ArrayList<>();
        int size = q.size();
        for (int i = 0; i < size; i++) {
            listMusic.add(q.remove());
        }
        int listSize = listMusic.size();

        for (int j = 0; j < listSize; j++) {
            Random rand = new Random();
            if (listMusic.size() > 2) {
                int n = rand.nextInt(listMusic.size() - 1);
                q.add(listMusic.remove(n));
            } else {
                q.add(listMusic.remove(0));

            }
        }
    }

    public static String getFileExtension(String name) { // extract Extension
        int lastIndexOf = name.lastIndexOf(".");

        if (lastIndexOf == -1) {
            return ""; // empty extension
        }

        return name.substring(lastIndexOf);
    }

    public static Music nextMusic() {//prepare music to play
        Music music = q.remove();
        q.add(music);
        stack.push(music);

        return music;

    }

    public static Clip playNextMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {//play next music
        firstPop = true;
        Music nextMusic = nextMusic();
        playingMusic = nextMusic;
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(nextMusic.path).getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
        return clip;

    }
    public static boolean firstPop = false;

    public static Clip playPreMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {//play pre  music
        Music preMusic;
        if (firstPop) {
            stack.pop();
            preMusic = stack.pop();
            playingMusic = preMusic;
            firstPop = false;
            
        } else {
            preMusic = stack.pop();
            playingMusic = preMusic;
        }

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(preMusic.path).getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();

        return clip;

    }

    public static void checkNextMusic() {// in this method we run thread for check music is end or not
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (clip.getMicrosecondLength() == clip.getMicrosecondPosition()) {// if music is ended go next music

                    try {
                        clip = playNextMusic();
                        System.out.println(playingMusic.track_name + " by " + playingMusic.singer_name + " is Playing");
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(DSProject.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DSProject.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(DSProject.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    public static void searchMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        root.files.clear();
        System.out.println("Enter what you want search :)))");
        Scanner sc = new Scanner(System.in);
        String searchKey = sc.nextLine();//get search key
        ArrayList<File> files = root.search(root, searchKey);//serach in tree

        if (files.isEmpty()) {

            System.out.println("Nothing found!!!!");

        } else {

            for (int i = 0; i < files.size(); i++) {
                System.out.println(i + 1 + ". " + files.get(i));
            }
            System.out.println("Do you want play this musics ?");
            System.out.println("1. Yes");
            System.out.println("2. no");
//ask do you wanna play these musics or not
            int ans = sc.nextInt();
            if (ans == 1) {
                q.removeAll(q);
                for (int j = 0; j < files.size(); j++) {
                    q.add(getMusicInfo(files.get(j)));
                }
                showPlayList();
                clip.close();
                clip = playNextMusic();
            } else {
                System.out.println("OK Countinue :)))))");

            }

        }
    }

    public static void playMusicMenu() {
//show menu to user
        int repeatStatus = 0;//0. play list  1. play list repeaty  2.repreat playing music 
        int counter = 0;
        System.out.println("1. Play");
        s = new Scanner(System.in);
        s.nextLine();

        try {
            clip = playNextMusic();
            counter++;
            int input;
            while (true) {
                if (counter == q.size()) {
                    if (repeatStatus == 1) {
                        counter = 0;
                    } else {
                        System.out.println("End of List");
                        return;

                    }
                }
                System.out.println("");
                System.out.println(playingMusic.track_name + " by " + playingMusic.singer_name + " is Playing");

                if (clip.isRunning()) {
                    System.out.println("1. Pause");
                } else {
                    System.out.println("1. Play");
                }

                System.out.println("2. Next");
                System.out.println("3. Pre");
                System.out.println("4. Cutomes");
                System.out.println("5. Search");
                System.out.println("6. Show tree");

                System.out.println("7. Exit");

                checkNextMusic();
                input = s.nextInt();

                if (input == 1) {
                    if (clip.isRunning()) {
                        clip.stop();
                    } else {
                        clip.start();
                    }
                } else if (input == 2) {
                    if (!q.isEmpty()) {
                        clip.close();
                        clip = playNextMusic();
                        counter++;

                    } else {
                        System.out.println("Q is empty");
                    }
                } else if (input == 3) {
                    if (!stack.isEmpty()) {
                        clip.close();
                        clip = playPreMusic();
                        counter++;
                    } else {
                        System.out.println("Stack is empty");
                    }
                } else if (input == 4) {
                    System.out.println("");

                    System.out.println("1. Name of music");
                    System.out.println("2. Singer of Music");
                    System.out.println("3. Shuffle");
                    System.out.println("4. Repeat");
                    input = s.nextInt();
                    if (input == 1) {
                        sortQueueByName();
                        showPlayList();
                        clip.close();
                        clip = playNextMusic();
                        counter = 0;
                    } else if (input == 2) {
                        sortQueueBySinger();
                        showPlayList();
                        clip.close();
                        clip = playNextMusic();
                        counter = 0;

                    } else if (input == 3) {
                        sortQueueByShuffle();
                        showPlayList();
                        clip.close();
                        clip = playNextMusic();
                        counter = 0;

                    } else if (input == 4) {
                        System.out.println("");

                        System.out.println("1. Play once list");
                        System.out.println("2. Repeat list");
                        System.out.println("3. Repeat playing music");
                        repeatStatus = s.nextInt() - 1;

                        if (repeatStatus == 2) {
                            clip.loop(Clip.LOOP_CONTINUOUSLY);
                        }

                        if (repeatStatus > 2 || repeatStatus < 0) {
                            System.out.println("Wrong Entry");
                            continue;
                        }
                    } else {
                        System.out.println("Wrong Entry");
                        continue;
                    }
                } else if (input == 5) {
                    searchMusic();
                } else if (input == 6) {

                    System.out.println("");
                    root.show(root);
                } else if (input == 7) {
                    System.exit(0);

                } else {
                    System.out.println("Wrong Entry");
                    continue;

                }

            }

        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }

    }

}
