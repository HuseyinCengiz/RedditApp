package com.example.huseyincengiz.redditapp.model.entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by HuseyinCengiz on 29.08.2017.
 */

@Root(name = "entry", strict = false)
public class Entry implements Serializable {

    @Element(name = "id")
    private String id;
    @Element(name = "content")
    private String content;
    @Element(name = "author",required = false)
    private Author author;
    @Element(name = "title")
    private String title;
    @Element(name = "updated")
    private String updated;

    public Entry() {

    }

    public Entry(String id, String content, Author author, String title, String updated) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.title = title;
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "\nEntry{\n" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", author=" + author.toString() +
                ", title='" + title + '\'' +
                ", updated='" + updated + '\'' +
                '}' + "\n " +
                "-------------------------------------------------------------------------------\n";
    }
}
