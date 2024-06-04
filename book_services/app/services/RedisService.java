package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.Book;
import models.Publisher;
import play.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;
import play.Configuration;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class RedisService {

    private static JedisPool jedisPool = null;
    public static ObjectMapper objectMapper;
    public BookService bookService;

    @Inject
    public RedisService(Configuration config,ObjectMapper objectMapper,BookService bookService) {
        // Load Redis configuration from application.conf
        String host = config.getString("redis.host");
        int port = config.getInt("redis.port");
        String user=config.getString("redis.user");
        String password = config.getString("redis.password");

        // Create and configure JedisPoolConfig
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getInt("redis.pool.maxTotal"));
        poolConfig.setMaxIdle(config.getInt("redis.pool.maxIdle"));
        poolConfig.setMinIdle(config.getInt("redis.pool.minIdle"));
        poolConfig.setMaxWaitMillis(config.getLong("redis.pool.maxWaitMillis"));
        poolConfig.setTestOnBorrow(config.getBoolean("redis.pool.testOnBorrow"));
        poolConfig.setTestOnReturn(config.getBoolean("redis.pool.testOnReturn"));
        poolConfig.setTestWhileIdle(config.getBoolean("redis.pool.testWhileIdle"));

        // Create the JedisPool
        jedisPool = new JedisPool(poolConfig,host ,port, 180000, user,password);
        this.objectMapper=objectMapper;
        this.bookService=bookService;
    }

    public static void updateBook(Book ob, Book nb) throws JsonProcessingException {

       try(Jedis jedis = jedisPool.getResource()){ // Serialize the old book to JSON and store it in Redis
        String newBookJson = objectMapper.writeValueAsString(nb);
        jedis.hset("books", ob.name, newBookJson);
    } catch (Exception e) {
        e.printStackTrace();
    }

    }

    // Close the pool when shutting down the application
    public void close() {
        jedisPool.close();
    }

    // Perform a GET operation
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }
    public Set<String> getKeys(){
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys("*");
        }
    }

    // Perform a SET operation
    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    // Perform a DELETE operation
    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    // Perform an UPDATE operation (SET)
    public void update(String key, String value) {
        set(key, value);
    }

    public static void storeBook(Book book) {
        try (Jedis jedis = jedisPool.getResource()) {
            String bookJson = objectMapper.writeValueAsString(book);
            jedis.hset("books",book.name, bookJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error serializing book object", e);
        }
    }

    // Retrieve a Book object from Redis
    public Book getBook(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String bookJson = jedis.hget("books",key);
            if(bookJson!=null)return objectMapper.readValue(bookJson, Book.class);
            else{
                Book b=MongoOperations.getBook(key);
                storeBook(b);
                return b;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deserializing book object", e);
        }
    }

    public Set<Book> getBooks() {
        Set<Book> books = new HashSet<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.hkeys("books");
            for (String key : keys) {
                String bookJson = jedis.hget("books",key);
                Book book = objectMapper.readValue(bookJson, Book.class);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deserializing book objects", e);
        }
        return books;
    }

    public static void deleteBook(String name) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel("books",name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deserializing book object", e);
        }
    }

//    public Publisher getPub(String name) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            String publisher=jedis.hget("publisher",name);
//            if(publisher!=null)return new ObjectMapper().readValue(publisher,Publisher.class);
//            else {
//                BookService.
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error deserializing book object", e);
//        }
//        public void storePub(String name) {
//            try (Jedis jedis = jedisPool.getResource()) {
//                String publisher=jedis.hget("publisher",name);
//                if(publisher!=null)return new ObjectMapper().readValue(publisher,Publisher.class);
//                return null;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException("Error deserializing book object", e);
//            }

    }

//    public void getAut(String name) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            String author=jedis.hget("author",name);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error deserializing book object", e);
//        }
//    }

