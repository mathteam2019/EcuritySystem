package com.nuctech.ecuritycheckitem.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RedisUtil
 *
 * @author PiaoCangGe
 * @version v1.0
 * @since 2019-11-27
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean aquirePessimisticLockWithTimeout(String lockName, String identifier, int lockTimeout) {
        if (StringUtils.isBlank(lockName) || lockTimeout <= 0)
            return false;
        final String lockKey = lockName;


            // try to acquire the lock
        if (redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.setNX(
                        redisTemplate.getStringSerializer().serialize(lockKey), redisTemplate.getStringSerializer().serialize(identifier));
            }
        })) { 	// successfully acquired the lock, set expiration of the lock
            redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    return connection.expire(redisTemplate
                                    .getStringSerializer().serialize(lockKey),
                            lockTimeout);
                }
            });
            return true;
        } else { // fail to acquire the lock
            // set expiration of the lock in case ttl is not set yet.
            if (null == redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    return connection.ttl(redisTemplate
                            .getStringSerializer().serialize(lockKey));
                }
            })) {
                // set expiration of the lock
                redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connection)
                            throws DataAccessException {
                        return connection.expire(redisTemplate
                                        .getStringSerializer().serialize(lockKey),
                                lockTimeout);
                    }
                });
            }
        }
        return false;
    }

    public void releasePessimisticLockWithTimeout(String lockName, String identifier) {
        if (StringUtils.isBlank(lockName) || StringUtils.isBlank(identifier))
            return;
        final String lockKey = lockName;

        redisTemplate.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] ctn = connection.get(redisTemplate
                        .getStringSerializer().serialize(lockKey));
                if(ctn!=null && identifier.equals(redisTemplate.getStringSerializer().deserialize(ctn)))
                    connection.del(redisTemplate.getStringSerializer().serialize(lockKey));
                return null;
            }
        });
    }

    public boolean expire(String key, long time) {
//        RedisAtomicLong counter =
//                new RedisAtomicLong("UNIQUE_COUNTER_NAME", redisTemplate.getConnectionFactory());
//        Long myCounter = counter.incrementAndGet();    // return the incremented value

        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public boolean setHash(String hashKey, String identity, Object object) {
        try {
            redisTemplate.opsForHash().put(hashKey, identity, object);
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public Object getHash(String hashKey, String identity) {
        try {
            return redisTemplate.opsForHash().get(hashKey, identity);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<Object, Object> getEntities(String hashKey) {
        try {
            return redisTemplate.opsForHash().entries(hashKey);
        } catch (Exception e) {
            return null;
        }
    }

}
