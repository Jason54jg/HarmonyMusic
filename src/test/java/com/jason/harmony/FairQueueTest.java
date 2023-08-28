package com.jason.harmony;

import com.jason.harmony.queue.FairQueue;
import com.jason.harmony.queue.Queueable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FairQueueTest {
    @Test
    public void differentIdentifierSize() {
        FairQueue<Q> queue = new FairQueue<>();
        int size = 100;
        for (int i = 0; i < size; i++)
            queue.add(new Q(i));
        assertEquals(queue.size(), size);
    }

    @Test
    public void sameIdentifierSize() {
        FairQueue<Q> queue = new FairQueue<>();
        int size = 100;
        for (int i = 0; i < size; i++)
            queue.add(new Q(0));
        assertEquals(queue.size(), size);
    }

    private class Q implements Queueable {
        private final long identifier;

        private Q(long identifier) {
            this.identifier = identifier;
        }

        @Override
        public long getIdentifier() {
            return identifier;
        }
    }
}
