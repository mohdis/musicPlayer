/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Mahdi
 */
public class MyTreeNode<T> {
//our customsed tree 
    //every node has parent and childerns
    private T data = null;
    private List<MyTreeNode> children = new ArrayList<>();
    private MyTreeNode parent = null;
    // this array list is searched files
    ArrayList<File> files = new ArrayList<>();

    public MyTreeNode(T data) {
        this.data = data;
    }

    public void addChild(MyTreeNode child) {
        child.setParent(this);
        this.children.add(child);
    }

    public MyTreeNode<T> addChild(T data) {
        MyTreeNode<T> newChild = new MyTreeNode<>(data);
        newChild.setParent(this);
        this.addChild(newChild);
        return newChild;
    }

    public void addChildren(List<MyTreeNode> children) {
        for (MyTreeNode t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<MyTreeNode> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(MyTreeNode parent) {
        this.parent = parent;
    }

    public MyTreeNode getParent() {
        return parent;
    }

    public void show(MyTreeNode<File> root) {
        String separator = "\\";

        System.out.println("parent: " + root.data);
        
        for (int i = 0; i < root.children.size(); i++) {
            MyTreeNode child = (MyTreeNode) root.children.get(i);
            File file = (File) child.data;
            if (DSProject.getFileExtension(file.getAbsolutePath()).equals(".wav")) {
                String[] parts = file.getAbsoluteFile().toString().split(Pattern.quote(separator));
                System.out.println("child: "+parts[parts.length - 1]);

            }

        }
        
        for (int j = 0; j < root.children.size(); j++) {
            if (root.children.get(j).children.size() > 0) {
                show(root.children.get(j));
            }
        }
        
    }

    public ArrayList<File> search(MyTreeNode<File> root, String searchKey) {

        String separator = "\\";
        for (int i = 0; i < root.children.size(); i++) {
            MyTreeNode child = (MyTreeNode) root.children.get(i);
            File file = (File) child.data;
            if (DSProject.getFileExtension(file.getAbsolutePath()).equals(".wav")) {//check music for add them files array list

                String[] parts = file.getAbsoluteFile().toString().split(Pattern.quote(separator));
                String musicName = parts[parts.length - 1];

                if (musicName.contains(searchKey)) {//add music file to files
                    files.add(file);
                }

            }
            List<MyTreeNode> children = child.getChildren();
            if (children.size() != 0) {
                search(child, searchKey); // search in childernes
            }

        }

        return files; //return searched musics

    }
}
