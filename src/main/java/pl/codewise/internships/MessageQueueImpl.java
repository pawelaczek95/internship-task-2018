package pl.codewise.internships;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MessageQueueImpl implements MessageQueue {

    public static final int QUEUE_SIZE = 100;
    private LinkedList<Message> messages;
    private long erroredMessages;

    public MessageQueueImpl(){
        this.messages = new LinkedList<Message>();
        this.erroredMessages = 0;
    }

    @Override
    public void add(Message message) {
        synchronized (this) {
            int i=0;
            Iterator<Message> iterator = messages.iterator();
            while (i < messages.size() && iterator.next().getTimeStamp().isBefore(message.getTimeStamp())) {
                ++i;
            }
            if (i < messages.size()) {
                messages.add(i, message);
            } else {
                messages.addLast(message);
            }
            if (message.getErrorCode() >= 400 && message.getErrorCode() <= 599) {
                ++erroredMessages;
            }
            if (messages.size() > QUEUE_SIZE) {
                int code = messages.peek().getErrorCode();
                if (code >= 400 && code <= 599) {
                    --erroredMessages;
                }
                messages.remove(0);
            }
        }
    }

    @Override
    public Snapshot snapshot() {
        List<Message> toReturnMessages = new LinkedList<Message>();
        removeOldMessages();
        int i=0;
        Iterator<Message> iterator = messages.iterator();
        while (i < messages.size()) {
            toReturnMessages.add(iterator.next().clone());
            ++i;
        }
        return new Snapshot(toReturnMessages);
    }

    @Override
    public long numberOfErrorMessages() {
        long toReturn;
        synchronized (this) {
            toReturn = erroredMessages;
        }
        return toReturn;
    }

    private void removeOldMessages(){
        synchronized (this) {
            LocalDateTime removerTime = LocalDateTime.now().minusMinutes(5);
            int i=0;
            Iterator<Message> iterator = messages.iterator();
            while (i < messages.size() && iterator.next().getTimeStamp().isBefore(removerTime)) {
                ++i;
            }
            while (i > 0) {
                if (messages.peekFirst().getErrorCode() >= 400 && messages.peekFirst().getErrorCode() <= 599) {
                    --erroredMessages;
                }
                messages.remove(0);
                --i;
            }
        }
    }
}
