package com.sn.webcrawler.concurrency;

/**
 * Message payload for exchanging messages between producer and consumer
 * @author snayak
 *
 */
public class Message {
    
	private String content;
    
    public Message(String content){
        this.content=content;
    }

    public String getContent() {
        return content;
    }

}