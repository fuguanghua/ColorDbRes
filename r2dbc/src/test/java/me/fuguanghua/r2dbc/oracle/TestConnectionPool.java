package me.fuguanghua.r2dbc.oracle;

import io.r2dbc.spi.IsolationLevel;
import me.fuguanghua.r2dbc.oracle.notspringboot.DatabaseConfig;
import org.junit.Test;
import reactor.core.publisher.*;

public class TestConnectionPool {
    @Test
    void TestOracleConnetionPool(){
        Mono.from(DatabaseConfig.getPooledConnection())
                .flatMapMany(connection ->
                        Mono.from(connection.createStatement(
                                "SELECT * FROM SWS_BUSINESS.MENU")
                                .execute())
                                .flatMapMany(result ->
                                        result.map((row, metadata) -> row.get(0, String.class)))
                                .concatWith(Mono.from(connection.setTransactionIsolationLevel(IsolationLevel.READ_COMMITTED)).cast(String.class))
                                .concatWith(Mono.from(connection.close()).cast(String.class)))
                .toStream()
                .forEach(System.out::println);
    }

    public static String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }
}
