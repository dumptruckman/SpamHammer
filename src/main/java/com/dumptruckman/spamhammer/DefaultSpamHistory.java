package com.dumptruckman.spamhammer;

import com.dumptruckman.spamhammer.api.Spam;
import com.dumptruckman.spamhammer.api.SpamHistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

class DefaultSpamHistory<S extends Spam> implements SpamHistory<S> {

    private final BlockingDeque<S> spamHistory;
    private final String playerName;

    DefaultSpamHistory(String playerName) {
        this(playerName, new LinkedBlockingDeque<S>());
    }

    DefaultSpamHistory(String playerName, BlockingDeque<S> backingDeque) {
        this.playerName = playerName;
        this.spamHistory = backingDeque;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public int countSequentialDuplicates(int spamLimit, long timeLimit) {
        final List<S> historyList = new ArrayList<S>(spamLimit);
        for (int i = 0; i < spamLimit && !spamHistory.isEmpty(); i++) {
            historyList.set(spamLimit - i - 1, spamHistory.pollLast());
        }
        final long mostRecentTime = historyList.get(historyList.size() - 1).getTime();
        S last = null;
        int count = 0;
        for (int i = historyList.size() - 1; i >= 0; i--) {
            final S current = historyList.get(i);
            if (last != null) {
                if (timeLimit != 0L && mostRecentTime - current.getTime() >= timeLimit) {
                    break;
                } else if (current.isDuplicate(last)) {
                    count++;
                } else {
                    break;
                }
            }
            last = current;
        }
        spamHistory.addAll(historyList);
        return count;
    }

    @Override
    public void addFirst(S s) {
        spamHistory.addFirst(s);
    }

    @Override
    public void addLast(S s) {
        spamHistory.addLast(s);
    }

    @Override
    public boolean offerFirst(S s) {
        return spamHistory.offerFirst(s);
    }

    @Override
    public boolean offerLast(S s) {
        return spamHistory.offerLast(s);
    }

    @Override
    public void putFirst(S s) throws InterruptedException {
        spamHistory.putFirst(s);
    }

    @Override
    public void putLast(S s) throws InterruptedException {
        spamHistory.putLast(s);
    }

    @Override
    public boolean offerFirst(S s, long timeout, TimeUnit unit) throws InterruptedException {
        return spamHistory.offerFirst(s, timeout, unit);
    }

    @Override
    public boolean offerLast(S s, long timeout, TimeUnit unit) throws InterruptedException {
        return spamHistory.offerLast(s, timeout, unit);
    }

    @Override
    public S takeFirst() throws InterruptedException {
        return spamHistory.takeFirst();
    }

    @Override
    public S takeLast() throws InterruptedException {
        return spamHistory.takeLast();
    }

    @Override
    public S pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return spamHistory.pollFirst(timeout, unit);
    }

    @Override
    public S pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        return spamHistory.pollLast(timeout, unit);
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return spamHistory.removeFirstOccurrence(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return spamHistory.removeLastOccurrence(o);
    }

    @Override
    public boolean add(S s) {
        return spamHistory.add(s);
    }

    @Override
    public boolean offer(S s) {
        return spamHistory.offer(s);
    }

    @Override
    public void put(S s) throws InterruptedException {
        spamHistory.put(s);
    }

    @Override
    public boolean offer(S s, long timeout, TimeUnit unit) throws InterruptedException {
        return spamHistory.offer(s, timeout, unit);
    }

    @Override
    public S remove() {
        return spamHistory.remove();
    }

    @Override
    public S poll() {
        return spamHistory.poll();
    }

    @Override
    public S take() throws InterruptedException {
        return spamHistory.take();
    }

    @Override
    public S poll(long timeout, TimeUnit unit) throws InterruptedException {
        return spamHistory.poll(timeout, unit);
    }

    @Override
    public S element() {
        return spamHistory.element();
    }

    @Override
    public S peek() {
        return spamHistory.peek();
    }

    @Override
    public boolean remove(Object o) {
        return spamHistory.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return spamHistory.contains(o);
    }

    @Override
    public int size() {
        return spamHistory.size();
    }

    @Override
    public Iterator<S> iterator() {
        return spamHistory.iterator();
    }

    public void push(S s) {
        spamHistory.push(s);
    }

    @Override
    public int remainingCapacity() {
        return spamHistory.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super S> c) {
        return spamHistory.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super S> c, int maxElements) {
        return spamHistory.drainTo(c, maxElements);
    }

    @Override
    public boolean isEmpty() {
        return spamHistory.isEmpty();
    }

    @Override
    public Object[] toArray() {
        return spamHistory.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return spamHistory.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return spamHistory.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends S> c) {
        return spamHistory.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return spamHistory.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return spamHistory.retainAll(c);
    }

    @Override
    public void clear() {
        spamHistory.clear();
    }

    @Override
    public boolean equals(Object o) {
        return spamHistory.equals(o);
    }

    @Override
    public int hashCode() {
        return spamHistory.hashCode();
    }

    @Override
    public S removeFirst() {
        return spamHistory.removeFirst();
    }

    @Override
    public S removeLast() {
        return spamHistory.removeLast();
    }

    @Override
    public S pollFirst() {
        return spamHistory.pollFirst();
    }

    @Override
    public S pollLast() {
        return spamHistory.pollLast();
    }

    @Override
    public S getFirst() {
        return spamHistory.getFirst();
    }

    @Override
    public S getLast() {
        return spamHistory.getLast();
    }

    @Override
    public S peekFirst() {
        return spamHistory.peekFirst();
    }

    @Override
    public S peekLast() {
        return spamHistory.peekLast();
    }

    @Override
    public S pop() {
        return spamHistory.pop();
    }

    @Override
    public Iterator<S> descendingIterator() {
        return spamHistory.descendingIterator();
    }
}
