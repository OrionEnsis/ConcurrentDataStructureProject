
package TwitchChatEmojiFinder.Collections;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FrequencyTable<T> implements Serializable{
    private final static int START_SIZE = 16;
    private int count;
    private Node[] table;
    private T highKey;
    private int highValue;
    
    static class Node<T> {
        T key;
        int value;
        Node next;
        
        Node(T k, int v){
            key = k;
            value = v;
        }
    }
    
    //Default constructor
    public FrequencyTable(){
        table = new Node[START_SIZE];
        count = 0;
    }

    public T getHighKey(){
        return highKey;
    }

    public int getHighValue(){
        return highValue;
    }
    
    public void put(T key, int value){
        int h = key.hashCode();
        int i = h &(table.length - 1);
        Node e = table[i];
        
        if(table[i] == null){
            table[i] = new Node<>(key, value);
            count++;
            if((float)count > (float)table.length*.75){
                    //make the table bigger!
                    resize();
            }
            return;
        }
        
        //FOREVER
        for(;;){
            //if null put it here
            if(e.next == null){
                //add the new Node to the list
                e.next = new Node<>(key,value);
                count++;
                
                //IF we are reaching birthday paradox
                if((float)count > (float)table.length*.75){
                    //make the table bigger!
                    resize();
                }
                //System.out.println(count + " " + key);
                //leave
                updateHighestKV(key);
                return;
            }
            //if its already here
            else if(e.key.equals(key)){
                //give weight to the value
                e.value += value;
                
                //leave
                updateHighestKV(key);
                return;
            }
            else{
                e= e.next;
            }
        }
    }
    private void updateHighestKV(T key){
        int value = getValue(key);
        if(key.equals(getHighKey()) && value < getHighValue()){
            //search the array for the highest k,v pair
            T[] keys = getAllKeys();
            T highestKey = null;
            int highestValue = 0;
            for (T k :
                    keys) {
                int v = getValue(k);
                if(v > highestValue){
                    highestKey = k;
                    highestValue = v;
                }
            }
            highKey = highestKey;
            highValue = highestValue;
        }
        else if(value > getHighValue()){
            highKey = key;
        }
    }
    public void remove(T key){
        int h = key.hashCode();
        int i = h &(table.length - 1);
        Node prev = null;
        Node e = table[i];
        
        //FOREVER
        for(;;){
            //IF null we're done
            if(e == null){
                return;
            }
            //ELSE IF we find the key...
            else if(e.key.equals(key)){
                //reduce the count
                e.value--;
                
                //IF no more occurances, remove the NODE
                if(e.value <= 0){
                    if(prev ==null){
                        table[i] = e.next;
                    }
                    else{
                        prev.next = e.next;
                    }
                }
                count--;
                updateHighestKV(key);
                return;
            }
            //ELSE got to the next node
            else{
                prev = e;
                e = e.next;
            }
        }
    }
    
    public int getValue(T key){
        int h = key.hashCode();
        int i = h &(table.length - 1);
        Node e = table[i];
        
        //FOREVER
        for(;;){
            //if null put it here
            if(e == null){
                return 0;
            }
            //if its already here
            else if(e.key.equals(key)){
                return e.value;
            }
            else{
                e= e.next;
            }
        }
    }
    
    public T[] getAllKeys(){
        ArrayList<Object> tempList = new ArrayList<>();
        for (Node aTable : table) {
            for (Node e = aTable; e != null; e = e.next) {
                tempList.add(e.key);
            }
        }

        final Object[] ts = new Object[tempList.size()];
        tempList.toArray(ts);
        return (T[])ts;
    }

    public double frequencyRating(FrequencyTable b){
        int vectorProduct = 0;
        float sumA = 0;
        float sumB = 0;
        double finalValue = 0;
        T[] masterArray = getMasterList(b);//GetList of all values for both
        int[] arrayA = new int[masterArray.length];
        int[] arrayB = new int[masterArray.length];
        
        //convert to arrays
        for(int i = 0; i < masterArray.length; i++){
            arrayA[i] = getValue(masterArray[i]);
            arrayB[i] = b.getValue(masterArray[i]);
            
            //prep for calculations
            vectorProduct += arrayA[i]*arrayB[i];
            sumA += arrayA[i] * arrayA[i];
            sumB += arrayB[i] * arrayB[i];
        }
        
        finalValue = vectorProduct/(Math.pow((double)sumA, .5) * Math.pow((double)sumB,.5));
        
        //calculate
        return finalValue;
    }
    
    private T[] getMasterList(FrequencyTable b){
        HashMap<T,Integer> map = new HashMap<>();
        T[] arrayA = getAllKeys();
        T[] arrayB = (T[]) b.getAllKeys();
        T[] tempArray;
        ArrayList<T> tempList = new ArrayList<>();//get list of all words of both lists so each is the same length
        
        //Add all the words to the list
        for(int i = 0; i < arrayA.length; i++){
            map.put(arrayA[i], i);
            tempList.add(arrayA[i]);
        }
        //add any words unique to b to the list
        for (T anArrayB : arrayB) {
            if (!map.containsKey(anArrayB)) {
                map.put(anArrayB, 1);
                tempList.add(anArrayB);
            }
        }
        final Object[] ts = new Object[tempList.size()];
        return (T[])tempList.toArray(ts);
    }
    
    //resize the hash array
    private void resize(){
        Node[] newTable = new Node[count*2];
        ArrayList<Node> tempList = new ArrayList<>();
        
        //System.out.println("resizing...");
        count = 0;
        //store every node in a temporary holding place
        for (Node aTable : table) {
            for (Node e = aTable; e != null; e = e.next) {
                tempList.add(e);
            }
        }
        
        //switch to the new table
        table = newTable;
        
        //Get all the information rehashed.
        for (Node<T> aTempList : tempList) {
            put(aTempList.key, aTempList.value);
        }
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException{
        //s.defaultWriteObject();
        s.writeInt(count);
        System.out.println(count);
        int j =0;
        //Write all elements to the file
        for (Node aTable : table) {
            for (Node e = aTable; e != null; e = e.next) {
                //System.out.println(e.key + " " + e.value + " " + j);
                s.writeObject(e.key);
                s.writeInt(e.value);
                j++;
            }
        }
    }
    
    private void readObject(ObjectInputStream s) throws IOException,ClassNotFoundException{
        //s.defaultReadObject();
        count = s.readInt();
        int temp = count;
        //System.out.println(count);
        table = new Node[START_SIZE];
        T key;
        int value;
        for(int i = 0; i < temp; i++){
            key =(T)s.readObject();
            value = s.readInt();
            put(key,value);
            //System.out.println(i + " "+ key);
        }
    }
    
    public void printAll(){
        int j =0;
        for (Node aTable : table) {
            for (Node e = aTable; e != null; e = e.next) {

                System.out.println(e.key + " " + e.value + " " + j);
                j++;
            }
        }
    }
}
