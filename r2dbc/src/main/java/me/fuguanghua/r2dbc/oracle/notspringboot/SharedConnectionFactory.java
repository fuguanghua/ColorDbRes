package me.fuguanghua.r2dbc.oracle.notspringboot;

import io.r2dbc.spi.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

public class SharedConnectionFactory implements ConnectionFactory {

    private final ConnectionFactoryMetadata metadata;
    private final AtomicReference<CompletionStage<Connection>> borrowQueueTail;

    public SharedConnectionFactory(
            Publisher<? extends Connection> connectionPublisher,
            ConnectionFactoryMetadata metadata) {
        CompletableFuture<Connection> initialTail = new CompletableFuture<>();
        Mono.from(connectionPublisher)
                .doOnNext(initialTail::complete)
                .doOnError(initialTail::completeExceptionally)
                .subscribe();

        borrowQueueTail = new AtomicReference<>(initialTail);
        this.metadata = metadata;
    }

    @Override
    public Publisher<? extends Connection> create() {
        CompletableFuture<Connection> nextTail = new CompletableFuture<>();
        CompletionStage<Connection> currentTail =
                borrowQueueTail.getAndSet(nextTail);

        return Mono.fromCompletionStage(
                currentTail.thenApply(connection ->
                        new BorrowedConnection(connection, nextTail)));
    }

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        return metadata;
    }

    private final class BorrowedConnection implements Connection {

        private final Connection connection;

        private final CompletableFuture<Connection> returnedFuture;

        private volatile boolean isClosed = false;
        private IsolationLevel initialIsolationLevel;

        private BorrowedConnection(
                Connection connection,
                CompletableFuture<Connection> returnedFuture) {
            this.connection = connection;
            this.returnedFuture = returnedFuture;
            this.initialIsolationLevel = connection.getTransactionIsolationLevel();
        }

        @Override
        public Publisher<Void> close() {
            if (isClosed)
                return Mono.empty();

            if (connection.isAutoCommit()) {
                returnedFuture.complete(connection);
            }
            else {
                Mono.from(connection.rollbackTransaction())
                        .concatWith(connection.setAutoCommit(true))
                        .concatWith(connection.setTransactionIsolationLevel(
                                initialIsolationLevel))
                        .doOnTerminate(() -> returnedFuture.complete(connection))
                        .subscribe();
            }

            return Mono.empty();
        }

        @Override
        public Publisher<Void> beginTransaction() {
            requireNotClosed();
            return connection.beginTransaction();
        }

        @Override
        public Publisher<Void> commitTransaction() {
            requireNotClosed();
            return connection.commitTransaction();
        }

        @Override
        public Batch createBatch() {
            requireNotClosed();
            return connection.createBatch();
        }

        @Override
        public Publisher<Void> createSavepoint(String name) {
            requireNotClosed();
            return connection.createSavepoint(name);
        }

        @Override
        public Statement createStatement(String sql) {
            requireNotClosed();
            return connection.createStatement(sql);
        }

        @Override
        public boolean isAutoCommit() {
            requireNotClosed();
            return connection.isAutoCommit();
        }

        @Override
        public ConnectionMetadata getMetadata() {
            requireNotClosed();
            return connection.getMetadata();
        }

        @Override
        public IsolationLevel getTransactionIsolationLevel() {
            requireNotClosed();
            return connection.getTransactionIsolationLevel();
        }

        @Override
        public Publisher<Void> releaseSavepoint(String name) {
            requireNotClosed();
            return connection.releaseSavepoint(name);
        }

        @Override
        public Publisher<Void> rollbackTransaction() {
            requireNotClosed();
            return connection.rollbackTransaction();
        }

        @Override
        public Publisher<Void> rollbackTransactionToSavepoint(String name) {
            requireNotClosed();
            return connection.rollbackTransactionToSavepoint(name);
        }

        @Override
        public Publisher<Void> setAutoCommit(boolean autoCommit) {
            requireNotClosed();
            return connection.setAutoCommit(autoCommit);
        }

        @Override
        public Publisher<Void> setTransactionIsolationLevel(
                IsolationLevel isolationLevel) {
            requireNotClosed();
            return connection.setTransactionIsolationLevel(isolationLevel);
        }

        @Override
        public Publisher<Boolean> validate(ValidationDepth depth) {
            requireNotClosed();
            return connection.validate(depth);
        }

        private void requireNotClosed() {
            if (isClosed)
                throw new IllegalStateException("Connection is closed");
        }
    }
}
