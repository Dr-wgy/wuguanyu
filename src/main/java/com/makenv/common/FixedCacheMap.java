package com.makenv.common;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wgy on 2016/12/2.
 */
public class FixedCacheMap<K,V> extends LinkedHashMap {

    private int MAX_VALUE;

    private final ReentrantLock lock;

    public FixedCacheMap(int max_value){

        super(max_value,0.75f);

        this.MAX_VALUE = max_value;

        this.lock = new ReentrantLock();

    }

    public V get( Object key) {

        V v = null;

        lock.lock();

        try {

            v = (V)super.get(key);

        }
        catch (Exception e) {

            e.printStackTrace();

        }finally {

            lock.unlock();
        }

        return v;
    }


    @Override
    public Object remove(Object key) {

        System.out.println("不允许删除元素");

        throw new RuntimeException("you can't remove element");

    }

    ;

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {

        return size() > MAX_VALUE;
    }


    @Override
    public Object put(Object key, Object value) {

        V v = null;

        lock.lock();

        try {

            v = (V) super.put(key,value);

        }
        catch (Exception e) {

            e.printStackTrace();

        }finally {

            lock.unlock();
        }

        return v;

    }

    public Map.Entry<K,V> getYougestEntry() {

        Map.Entry<K,V> entry = null;

        Iterator<Map.Entry<K,V>> iterator = this.entrySet().iterator();

        while(iterator.hasNext()) {

            entry = iterator.next();
        }

        return entry;
    }

    public Map.Entry<K,V> getEldestEntry(){

        return (Map.Entry<K, V>) this.entrySet().iterator().next();
    }


}
