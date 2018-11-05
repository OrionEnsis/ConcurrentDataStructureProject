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
        Node prev;
        T data;

        public Node(T data){
            this.data = data;
            next = null;
            size = 0;
        }

    }

    public ConcurrentQueue(){
        head = null;
        tail = null;
        lock = new ReentrantLock();
    }

    @Override
    public synchronized int size() {
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
        lock.lock();
        try{
            Node n = head;
            if(head == null){
                head = new Node(o);
                tail = head;
            }
            else{
                while(n.next != null){
                    n = n.next;
                }
                n.next = new Node(o);
                tail = n.next;
                changeSize(1);
            }
        } finally{
            lock.unlock();
        }
        return false;
    }
    private synchronized void changeSize(int i){
        size+= i;
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

    //ready for testing
    @Override
    public boolean offer(T o) {
        boolean result = false;
        lock.lock();
        try {

            Node last = tail;
            tail = new Node(o);
            if(head == null)
                head = tail;
            else
                last.next = tail;
            result = true;
            changeSize(1);
        } finally{
            lock.unlock();
        }
        return result;
    }

    //not ready for testing
    @Override
    public T remove() throws NoSuchElementException {
        T object = null;
        lock.lock();
        try{

            if (head == null){
                throw new NoSuchElementException();
            }
            object = head.data;
            head = head.next;
            changeSize(-1);

        } finally{
            lock.unlock();
        }
        return object;
    }


    @Override
    public T poll() {
        T x = null;
        lock.lock();
        try {

            //get the data
            if(head != null) {
                x = head.data;
                head = head.next;
                changeSize(-1);
            }

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
