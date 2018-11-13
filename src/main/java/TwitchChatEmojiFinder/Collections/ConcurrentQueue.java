package TwitchChatEmojiFinder.Collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentQueue<T> implements Queue<T> {

    private AtomicInteger size;
    private Node head;
    private Node tail;
    private Semaphore lock;

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
        lock = new Semaphore(1);
        size = new AtomicInteger();
    }

    @Override
    public synchronized int size() {
        System.out.println("mqueue: "+size.get());
        return size.get();
    }

    @Override
    public boolean isEmpty() {
        return size.get() == 0;
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

    @Override
    public boolean add(T o) {

        try{
            lock.acquire();
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
                size.addAndGet(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            lock.release();
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

    //ready for testing
    @Override
    public boolean offer(T o) {
        boolean result = false;

        try {
            lock.acquire();
            Node last = tail;
            tail = new Node(o);
            if(head == null)
                head = tail;
            else
                last.next = tail;
            result = true;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            size.addAndGet(1);
            //System.out.println("offer" + size.get());
            lock.release();
        }
        return result;
    }

    //not ready for testing
    @Override
    public T remove() throws NoSuchElementException {
        T object = null;

        try{
            lock.acquire();
            if (head == null){
                throw new NoSuchElementException();
            }
            object = head.data;
            head = head.next;
            size.addAndGet(-1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            lock.release();
        }
        return object;
    }


    @Override
    public T poll() {
        T x = null;

        try {
            lock.acquire();
            //get the data
            if(head != null) {
                x = head.data;
                head = head.next;
                if(x != null)
                    size.addAndGet(-1);
            }
            //System.out.println("poll");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            lock.release();
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
