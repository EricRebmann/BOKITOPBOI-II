package org.example.juegofinalsupremo;

import org.example.juegofinalsupremo.data.MyList;
import org.example.juegofinalsupremo.data.MyQueue;
import org.example.juegofinalsupremo.data.MyStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataStructuresTest {
    @Test
    void listAddsFindsAndRemoves() {
        MyList<String> list = new MyList<String>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals(3, list.size());
        assertEquals(1, list.indexOf("b"));
        assertTrue(list.remove("b"));
        assertEquals("c", list.get(1));
        assertFalse(list.contains("b"));
    }

    @Test
    void queueRespectsFifoOrder() {
        MyQueue<Integer> queue = new MyQueue<Integer>();
        queue.enqueue(1);
        queue.enqueue(2);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void stackRespectsLifoOrder() {
        MyStack<Integer> stack = new MyStack<Integer>();
        stack.push(1);
        stack.push(2);

        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }
}
