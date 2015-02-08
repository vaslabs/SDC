package com.vaslabs.sdc.utils;

public class DynamicQueue<T>{
    Node<T> head;
    Node<T> tail;
    int size;
    public DynamicQueue() {
        
    }
    
    public synchronized void append(T obj) {
        if (head == null) {
            head = new Node<T>(obj);
        } else if (tail == null) {
            tail = new Node<T>(obj);
            head.setNext(tail);
        } else {
            Node<T> temp = new Node<T>(obj);
            Node<T> prevTail = tail;
            prevTail.setNext( temp );
            tail = temp;
        }
        
        size++;
    }
    
    public synchronized T pop() {
        if (head == null)
            return null;
        Node<T> newHead = head.getNext();
        Node<T> oldHead = head;
        head = newHead;
        size--;
        return oldHead.getContent();
    }

    public int size() {
        
        return size;
    }
}

class Node<T> {
    private final T object;
    private Node<T> next;
    
    public Node(T obj) {
        this.object = obj;
    }
    
    public Node<T> getNext() {
        return next;
    }

    public T getContent() {
        return object;
    }
    
    public void setNext(Node<T> next) {
        this.next = next;
    }
    
}
