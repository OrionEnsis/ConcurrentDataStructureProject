package TwitchChatEmojiFinder.Collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentQueue<T> implements Queue<T> {

    private volatile int size;
    private Node head;
    private Node tail;
    private ReentrantLock lock;

    private final class Node{
        Node next;
        T data;

        public Node(T data){
            this.data = data;
            next = null;
        }

    }

    public ConcurrentQueue(){
        head = null;
        tail = null;
        lock = new ReentrantLock();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    //TODO write this
    @Override
    public boolean add(T o) {
        try{
            lock.lock();
            Node n = head;
            if(head == null){
                head = new Node(o);
            }
            else{
                while(n.next != null){
                    n = n.next;
                }
                n.next = new Node(o);
                size++;
            }
        } finally{
            lock.unlock();
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean offer(T o) {
        boolean result = false;
        try {
            lock.lock();
            Node n = head;
            if(head == null){
                head = new Node(o);
            }
            else{
                while(n.next != null){
                    n = n.next;
                }
                n.next = new Node(o);
                size++;
                result = true;
            }
        } finally{
            lock.unlock();
        }
        return result;
    }

    @Override
    public T remove() throws NoSuchElementException {
        T object = null;
        try{
            lock.lock();
            if (head == null){
                throw new NoSuchElementException();
            }
            object = head.data;
            head = head.next;
            size--;

        } finally{
            lock.unlock();
        }
        return object;
    }


    @Override
    public T poll() {
        T x;
        try {
            lock.lock();
            x = head.data;
            head = head.next;
            size--;
        } finally{
            lock.unlock();
        }
        return x;
    }

    @Override
    public T element() {
        return null;
    }

    @Override
    public T peek() {
        return head.data;
    }
}
