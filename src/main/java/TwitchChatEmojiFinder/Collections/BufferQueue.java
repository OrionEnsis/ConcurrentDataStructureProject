package TwitchChatEmojiFinder.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;
@SuppressWarnings("CatchAndPrintStackTrace")
public class BufferQueue<T> implements Queue<T>{

    private final class Node{
        ConcurrentQueue<T> queue;
        Semaphore offerLock;
        Semaphore pollLock;

        public Node(){
            queue = new ConcurrentQueue<>();
            offerLock = new Semaphore(1);
            pollLock = new Semaphore(1);
        }
    }

    private LongAdder adder;
    private ArrayList<Node> queues;
    private ThreadLocalRandom random;

    public BufferQueue(){
        int size = Runtime.getRuntime().availableProcessors();
        random = ThreadLocalRandom.current();
        queues = new ArrayList<>();
        adder = new LongAdder();
        for (int i = 0; i < size; i++) {
            queues.add( new Node());
        }

    }

    @Override
    public int size() {
        return adder.intValue();
    }

    @Override
    public boolean isEmpty() {
        return 0 == size();
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
    public boolean add(T t) {
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
    public boolean offer(T t) {
        int startIndex = random.nextInt(queues.size());
        boolean offered = false;
        for(int i = startIndex;!offered;i++){
            Node n = queues.get(i % queues.size());
            if(n.offerLock.tryAcquire()){
                try{
                    n.queue.offer(t);
                }
                finally{
                    adder.add(1);
                    //System.out.println(adder.intValue() + " offer");
                    offered = true;
                    n.offerLock.release();

                }
            }
        }
        return offered;
    }

    @Override
    public T remove() {
        return null;
    }

    @Override
    public T poll() {
        int startIndex = random.nextInt(queues.size());
        T t = null;
        while(size() != 0 && t == null){
            Node n = queues.get(startIndex % queues.size());
            if(n.pollLock.tryAcquire()){
                try {
                    t = n.queue.poll();
                }
                finally {
                    if(t != null)
                        adder.decrement();
                    //System.out.println(adder.intValue() + " poll");
                    n.pollLock.release();
                }
            }
            startIndex++;
        }
        if(size() == 0) {
            System.out.println("Empty");
            System.out.println(t);
        }
        return t;
    }

    @Override
    public T element() {
        return null;
    }

    @Override
    public T peek() {
        return null;
    }
}
