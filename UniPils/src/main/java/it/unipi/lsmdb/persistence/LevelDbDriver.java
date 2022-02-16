package it.unipi.lsmdb.persistence;

import com.google.common.collect.Lists;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDbDriver {

    private DB db = null;

    public LevelDbDriver() {
    }

    private void openDB() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            //factory.destroy(new File("unipils"), options);
            db = factory.open(new File("unipils"), options);
        } catch (IOException ioe) {
            closeDB();
        }
    }

    public void put(String key, String value) {
        openDB();
        db.put(bytes(key), bytes(value));
        closeDB();
    }

    public String getString(String key) {
        openDB();
        byte[] bytes = db.get(bytes(key));
        String quantity = (bytes == null ? null : asString(bytes));
        closeDB();
        return quantity;
    }

    public List<String> findKeysByPrefix(String name) {
        openDB();
        try (DBIterator iterator = db.iterator()) {
            List<String> keys = Lists.newArrayList();
            for (iterator.seek(bytes(name)); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                if (!key.contains(name)) {
                    break;
                }
                keys.add(key.substring(name.length()));
            }
            closeDB();
            return keys;
        } catch (IOException e) {
            closeDB();
            throw new RuntimeException(e);
        }
    }

    public void deleteValue(String key) {
        openDB();
        db.delete(bytes(key));
        closeDB();
    }

    public int splitKeys(String key) {
        String beer_id = key.split(":")[1];
        return Integer.parseInt(beer_id);
    }

    private void closeDB() {
        try {
            if (db != null) db.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}