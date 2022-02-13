package it.unipi.lsmdb.persistence;

import com.google.common.collect.Lists;
import org.iq80.leveldb.*;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.IOException;

// id_utente:id_birra:quantity = quantity

public class LevelDbDriver {

    private DB db = null;

    private void openDB()
    {
        Options options = new Options();
        options.createIfMissing(true);
        try{
            factory.destroy(new File("unipils"), options);
            db = factory.open(new File("unipils"), options);
        }
        catch (IOException ioe) { closeDB(); }
    }

    public void put(String key, String value)
    {
        db.put(bytes(key), bytes(value));
    }

    public void put(String key, int value)
    {
        db.put(bytes(key), bytes(String.valueOf(value)));
    }

    public String getString(String key)
    {
        byte[] bytes = db.get(bytes(key));
        return (bytes == null ? null : asString(bytes));
    }

    public int getInt(String key)
    {
        byte[] bytes = db.get(bytes(key));
        return (bytes == null ? null : ByteBuffer.wrap(bytes).getInt());
    }

    public List<String> findKeysByPrefix(String prefix)
    {
        try (DBIterator iterator = db.iterator()) {
            List<String> keys = Lists.newArrayList();
            for (iterator.seek(bytes(prefix)); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                if (!key.startsWith(prefix)) {
                    break;
                }
                keys.add(key.substring(prefix.length()));
            }
            return keys;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> findValuesByPrefix(String prefix)
    {
        try (DBIterator iterator = db.iterator()) {
            List<String> values = Lists.newArrayList();
            for (iterator.seek(bytes(prefix)); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                if (!key.startsWith(prefix)) {
                    break;
                }
                values.add(asString(iterator.peekNext().getValue()));
            }
            return values;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, String> findByPrefix(String prefix)
    {
        try (DBIterator iterator = db.iterator()) {
            HashMap<String, String> entries = new HashMap<>();
            for (iterator.seek(bytes(prefix)); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                if (!key.startsWith(prefix)) {
                    break;
                }
                entries.put(key.substring(prefix.length()), value);
            }
            return entries;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteValue(String key)
    {
        db.delete(bytes(key));
    }

    private void closeDB()
    {
        try {
            if( db != null) db.close();
        }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }
    
}