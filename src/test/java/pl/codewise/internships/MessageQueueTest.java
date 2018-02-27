package pl.codewise.internships;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
//
public class MessageQueueTest {

    MessageQueue messageQueue;

    @Before
    public void initialize(){
        messageQueue = new MessageQueueImpl();
    }

    @Test
    public void testEmptyQueueSnapshot(){
        Snapshot snapshot = messageQueue.snapshot();
        assertTrue(snapshot.getMessages().isEmpty());
    }

    @Test
    public void testSimpleSnapshot(){
        final int MESSAGES_COUNT = 3;
        for (int i=0; i<MESSAGES_COUNT; ++i) {
            messageQueue.add(new Message("text" + i, 450));
        }
        assertTrue(messageQueue.snapshot().getMessages().size() == MESSAGES_COUNT);
    }

    @Test
    public void testEmptyQueueErroredMessagesCount(){
        assertTrue(messageQueue.numberOfErrorMessages() == 0);
    }

    @Test
    public void testIfErroredMessagesHandledCorrectly(){
        final long BEGIN_MESSAGES_COUNT = 3, ERRORED_COUNT = 2, LAST_COUNT = 1;
        for (long i=0; i<BEGIN_MESSAGES_COUNT; ++i) {
            messageQueue.add(new Message("text" + i, 200));
        }
        for (long i=0; i<ERRORED_COUNT; ++i) {
            messageQueue.add(new Message("foo" + i, 404));
        }
        for (long i=0; i<LAST_COUNT; ++i) {
            messageQueue.add(new Message("bar" + i, 200));
        }
        assertTrue(messageQueue.numberOfErrorMessages() == ERRORED_COUNT);
    }

    @Test
    public void testIfNumberOfMessagesCorrectIs() {
        final int MESSAGES_COUNT = 200;
        for (int i=0; i<MESSAGES_COUNT; ++i) {
            messageQueue.add(new Message("foo_" + MESSAGES_COUNT, 200));
        }
        Snapshot snapshot = messageQueue.snapshot();
        assertTrue(snapshot.getMessages().size() == MessageQueueImpl.QUEUE_SIZE);
    }

    @Test
    public void testIfTimeoutWorks(){
        final int GOOD_MESSAGES_COUNT = 5, TIMEOUTED_MESSAGES_COUNT = 3;
        LocalDateTime currentTime = LocalDateTime.now();
        for (int i=0; i<GOOD_MESSAGES_COUNT; ++i) {
            messageQueue.add(new Message("lol" + i, 200, currentTime.minusSeconds(i)));
        }
        for (int i=0; i<TIMEOUTED_MESSAGES_COUNT; ++i) {
            messageQueue.add(new Message("bar" + i, 200, currentTime.minusMinutes(6 + i)));
        }
        Snapshot snapshot = messageQueue.snapshot();
        assertTrue(snapshot.getMessages().size() == GOOD_MESSAGES_COUNT);
    }
}