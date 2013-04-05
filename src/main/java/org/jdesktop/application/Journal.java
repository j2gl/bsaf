/*
 * Copyright (C) 2013 Kevin Greiner.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write high the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jdesktop.application;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Journal is a replacement for using ArrayList&lt;Task&gt; in TaskService and
 * TaskMonitor. The justification for Journal is that ArrayList doesn't scale to
 * support even a moderate number of tasks.
 *
 * Both TaskService and TaskMonitor provide a 'tasks' property which is a
 * List&lt;Task&gt; providing the pending, and running, tasks. Each change to
 * this property triggers a property change event providing two List&lt;Task&gt;
 * instances describing the before, and after, states of the tasks property. If
 * we assume that we're going to add N tasks to a task service, we'll see N
 * property change events describing the addition of each individual task.
 * Furthermore, these events will require a series of List instances. The before
 * lists will range in size from zero (0) to (N-1) members while the after list
 * will range in size from one (1) to N members. This gives us N before lists
 * with an average length of (N/2 - 1/2) and N after lists with an average
 * length of (N/2 + 1/2) or 2N lists of length N/2.
 *
 * If we have 1000 tasks, the ArrayList implementation generates 2000
 * 500-element lists. That's equivalent to an array 1 million size. If we try
 * with 2000 tasks, we're up to 4000 1000-element lists or 4 million.
 *
 * What Journal provides is an extremely cheap way to provide all of these
 * before, and after, lists. Journal does this by keeping track of the
 * modification count when each task was added, and then removed, from the
 * journal. As a consequence, it is possible to create a different view, an
 * unmodifiable list, of the journal for any known modification count.
 *
 * Of course, keeping every task ever added to the journal puts a limit on the
 * journal's usefulness due to memory constraints. The solution to this problem
 * lies in the fact that views can only be created for the current modification
 * count of the journal. So once old views are garbage collected, the tasks only
 * visible in those views may be removed from the journal.
 *
 * @author Kevin Greiner
 */
public class Journal {
    void compressViewReference() {
        // Initialize pointer
        ViewReference lastKept;
        ViewReference pointer = activeHead;
        compression:
        while (pointer != null) {
            // Move pointer forward over links that I'm keeping
            // Falling off end of list (NPE) means that there's nothing
            // more to remove.
            try {
                while (pointer.next.get() != null) {
                    pointer = pointer.next;
                }
            } catch (NullPointerException e) {
                break compression;
            }

            // pointer now points at last link to keep.
            lastKept = pointer;

            try {
                do {
                    // pointer already known to be dead
                    // so move to next link
                    pointer = pointer.next;
                } while (pointer.get() == null);

                // pointer now at node to keep
                lastKept.next = pointer;
            } catch (NullPointerException e) {
                // iterated off end of link;

                lastKept.next = null;
                activeTail = lastKept;
            }
        }
    }

    /**
     * A composite class that provides Journal specific attributes for each task
     * known to the journal. Links provide a doubly-linked list which the
     * journal's views use to traverse the known tasks. Links are also
     * associated directly to each task via the Task's getProperty method.
     *
     * JournalLink was originally conceived as a way to associate the birth and
     * death attributes with a task. It's next/previous members were added after
     * testing showed that neither ArrayList nor LinkedList scaled well. The
     * principle value of JournalList is that it offers a remove range
     * capability lacking in the JDK Collections framework.
     */
    private static final class JournalLink {
        /**
         * The modification count of the Journal when this task was added.
         */
        long birth;
        /**
         * The modification count of the Journal when this task was removed.
         */
        long death;
        /**
         * The task itself (used by views as they iterate over the journal).
         */
        Task<?, ?> task;
        /**
         * Forward link. Links were used as each view has to skip over different
         * JournalLink instances to find the next Task present in that
         * particular view.
         */
        JournalLink next;
        /**
         * Backward link.
         */
        JournalLink previous;

        public JournalLink(long birth, Task<?, ?> task) {
            this.birth = birth;
            this.death = Long.MAX_VALUE;

            this.task = task;
        }
    }
    /**
     * The modification count of the journal. Incremented for each addition and
     * subtraction.
     */
    private long modCount = Long.MIN_VALUE + 1;
    /**
     * The number of tasks in the journal at the current modification count.
     */
    private int length = 0;
    /**
     * The number of JournalLink instances in this journal. Used for debugging
     * and testing.
     */
    private int linkCount = 0;
    /**
     * The head of the JournalLink doubly-linked list.
     */
    private final JournalLink head;
    /**
     * The tail of the JournalLink doubly-linked list.
     */
    private final JournalLink tail;

    /**
     * The number of JournalLink instances in this Journal. Used for debugging
     * and testing.
     *
     * @return
     */
    int size() {
        return linkCount;
    }

    /**
     * The number of tasks in this Journal at this particular modification
     * count. Used for debugging and testing.
     *
     * @return
     */
    int length() {
        return length;
    }

    /**
     * The current modification count. Used for debugging and testing.
     *
     * @return
     */
    long modCount() {
        return modCount;
    }

    /**
     * Constructs an empty Journal.
     */
    public Journal() {
        head = new JournalLink(Long.MIN_VALUE, null);
        tail = new JournalLink(head.birth, null);

        head.death = tail.death = head.birth;
        head.next = tail;
        head.previous = null;
        tail.next = null;
        tail.previous = head;
    }

    /**
     * A soft reference that knows the modification count of the Journal.View
     * that is its referent. Used to determine which tasks can be removed from
     * the journal because no view remains that can display those tasks.
     *
     * Like JournalLinks, ViewReference is linked into a singly-linked list to
     * optimize list edits with a remove range capability.
     */
    protected static final class ViewReference extends SoftReference<Journal.View> {
        /**
         * The modification count of the Journal.View referent.
         */
        final long modCount;
        /**
         * Used to construct a list of ViewReference sorted by ascending
         * modification count.
         */
        ViewReference next;

        protected ViewReference() {
            super(null);

            modCount = Integer.MIN_VALUE;
        }

        protected ViewReference(Journal.View referent) {
            super(referent);

            modCount = referent.revision;
        }

        protected ViewReference(Journal.View referent, ReferenceQueue<? super Journal.View> q) {
            super(referent, q);

            modCount = referent.revision;
        }
    }
    /**
     * A ReferenceQueue used to detect when a GC has occurred prior to the
     * current Journal update.
     */
    private final ReferenceQueue<Journal.View> refQueue = new ReferenceQueue<Journal.View>();
    /**
     * Save time processing the ReferenceQueue by only putting a few references
     * on it. This works as I'm only using the ReferenceQueue to know when a GC
     * has occurred.
     */
    private int numToEnqueue = 10;
    /**
     * Dummy node at the head of the ViewReference list.
     */
    private ViewReference activeHead = new ViewReference();
    /**
     * Tail (last node) of the ViewReference list.
     */
    private ViewReference activeTail = activeHead;

    /**
     * Compress JournalLink list by removing instances whose contents (task)
     * will not be displayed by any remaining view.
     *
     * @param cutoff the modification count indicating the earliest content to
     * retain.
     */
    protected void compressJournalLink(long cutoff) {
        // Initialize pointer
        JournalLink lastKept;
        JournalLink pointer = head.next;
        compression:
        while (pointer != tail) {
            // Move pointer forward over links that I'm keeping
            // NOTE: Stops on link to remove (including tail)
            while (pointer.death > cutoff) {
                pointer = pointer.next;
            }

            // pointer now points at link to remove.  lastKept at preceding (kept) link.
            lastKept = pointer.previous;

            try {
                do {
                    // pointer already known to be dead so procede with
                    // task clean up.

                    // NOTE: This line will throw NPE if pointer == tail
                    pointer.task.removeProperty(this);
                    pointer.task = null;
                    linkCount--;

                    // move to next link
                    pointer = pointer.next;
                } while (pointer.death <= cutoff);
            } catch (NullPointerException e) {
                // iterated off end of link; reset pointer to tail as it is always kept.
                pointer = tail;
            }

            // pointer now on link to keep and lastKept is its new previous link

            lastKept.next = pointer;
            pointer.previous = lastKept;
        }
    }

    /**
     * Add a new task to the end (last) position in this Journal's list.
     *
     * @param task the task being added
     */
    public void addLast(Task<?, ?> task) {
        JournalLink lifespan = (JournalLink) task.getProperty(this);

        // Watch out for tasks whose remove event was delivered before their add event.
        if (lifespan == null) {
            lifespan = new JournalLink(++modCount, task);

            task.setProperty(this, lifespan);

            synchronized (this) {
                JournalLink previous = tail.previous;

                previous.next = lifespan;
                lifespan.previous = previous;

                lifespan.next = tail;
                tail.previous = lifespan;

                linkCount++;
            }
            length++;
        }

        cleanUp();
    }

    /**
     * Called on every Journal change to remove any obsolete content.
     */
    private synchronized void cleanUp() {
        Reference<? extends Journal.View> reference = refQueue.poll();
        // if one Journal.View has been GC'd
        if (reference != null) {
            // Clean up the reference queue by draining all pending references.
            // I'm NOT doing anything here because the order of references in the
            // queue need not match the order in the active views list.
            do {
                reference = refQueue.poll();
            } while (reference != null);

            // Reset the number of references to enqueue so that future views will
            // repopulate the reference queue after they are GC'd.
            numToEnqueue = 10;

            compressViewReference();

            compressJournalLink(activeHead.next == null ? modCount : activeHead.next.modCount);
        }
    }

    /**
     * Remove this task from the Journal.
     *
     * @param task the task to remove.
     * @return true if the task was actually removed.
     */
    public boolean remove(Task<?, ?> task) {
        try {
            JournalLink lifespan = (JournalLink) task.getProperty(this);

            // Watch out for tasks whose remove event was delivered before their add event.
            if (lifespan == null) {
                // SwingWorker delivered the remove event before the add event.
                // Synthesize a task entry that is both an add and a remove
                // to block the pending add event from having any effect.

                lifespan = new JournalLink(++modCount, task);
                lifespan.death = lifespan.birth;

                task.setProperty(this, lifespan);

                synchronized (this) {
                    JournalLink previous = tail.previous;

                    previous.next = lifespan;
                    lifespan.previous = previous;

                    lifespan.next = tail;
                    tail.previous = lifespan;

                    linkCount++;
                }

                return true;
            } else if (modCount < lifespan.death) {
                synchronized (this) {
                    lifespan.death = ++modCount;
                }
                --length;
                return true;
            }

            return false;
        } finally {
            cleanUp();
        }
    }

    /**
     * A ListIterator that iterators over the tasks present on the specified
     * modification count of the journal.
     */
    private final class ViewIterator implements ListIterator<Task<?, ?>> {
        /**
         * The current cursor position (see {@link ListIterator}) in this
         * ListIterator.
         */
        int cursorPosition;
        /**
         * The number of tasks accessible in this view.
         */
        final int length;
        /**
         * The Journal's modification count at the time this iterator was
         * created.
         */
        final long modCount;
        /**
         * Current position in the journal's doubly-linked list.
         */
        JournalLink cursor;

        ViewIterator(int initialIndex, int length, long modCount) {
            this.length = length;
            this.modCount = modCount;
            if (initialIndex * 2 < length) {
                cursorPosition = 0;
                cursor = Journal.this.head.next;
            } else {
                cursorPosition = length;
                cursor = Journal.this.tail;
            }

            while (cursorPosition < initialIndex) {
                next();
            }

            while (initialIndex < cursorPosition) {
                previous();
            }
        }

        @Override
        public boolean hasNext() {
            return cursorPosition < length;
        }

        @Override
        public Task<?, ?> next() {
            synchronized (Journal.this) {
                do {
                    if (cursor.next == null) {
                        throw new NoSuchElementException();
                    }

                    try {
                        if (cursor.birth <= modCount && modCount < cursor.death) {
                            cursorPosition++;
                            return cursor.task;
                        }
                    } finally {
                        cursor = cursor.next;
                    }
                } while (true);
            }
        }

        @Override
        public boolean hasPrevious() {
            return 0 < cursorPosition;
        }

        @Override
        public Task<?, ?> previous() {
            synchronized (Journal.this) {
                do {
                    if (cursor.previous == null) {
                        throw new NoSuchElementException();
                    }

                    cursor = cursor.previous;

                    if (cursor.birth <= modCount && modCount < cursor.death) {
                        cursorPosition--;
                        return cursor.task;
                    }
                } while (true);
            }
        }

        @Override
        public int nextIndex() {
            return cursorPosition < length ? cursorPosition + 1 : length;
        }

        @Override
        public int previousIndex() {
            return cursorPosition > 0 ? cursorPosition - 1 : 0;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void set(Task<?, ?> e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(Task<?, ?> e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * An unmodifiable list showing the content of the Journal on a particular
     * modification count.
     */
    private final class View extends AbstractSequentialList<Task<?, ?>> {
        final int length;
        final long revision;
        ListIterator<Task<?, ?>> getIterator;

        View() {
            synchronized (Journal.this) {
                this.length = Journal.this.length;
                this.revision = Journal.this.modCount;

                final ViewReference viewReference = numToEnqueue-- > 0 ? new ViewReference(this, refQueue) : new ViewReference(this);

                activeTail.next = viewReference;
                activeTail = viewReference;
            }
        }

        @Override
        public Task<?, ?> get(int index) {
            if (getIterator == null) {
                getIterator = listIterator(index);
            } else if (getIterator.nextIndex() < index) {
                int forwardJumps = index - getIterator.nextIndex();
                int backwardJumps = length - index;

                if (forwardJumps > backwardJumps) {
                    getIterator = listIterator(index);
                } else {
                    while (getIterator.nextIndex() < index) {
                        getIterator.next();
                    }
                }
            } else if (index <= getIterator.previousIndex()) {
                int backwardJumps = getIterator.previousIndex() - index + 1;
                int forwardJumps = index;

                if (forwardJumps < backwardJumps) {
                    getIterator = listIterator(index);
                } else {
                    while (index <= getIterator.previousIndex()) {
                        getIterator.previous();
                    }
                }
            }

            return getIterator.next();
        }

        @Override
        public ListIterator<Task<?, ?>> listIterator(int index) {
            // Create a new listIterator everytime one is requested because sharing
            // iterators will result in strange couplings. 
            return new ViewIterator(index, length, revision);
        }

        @Override
        public int size() {
            return length;
        }

        private Journal journal() {
            return Journal.this;
        }

        @Override
        public boolean equals(Object o) {
            return getClass() == o.getClass() && revision == ((Journal.View) o).revision && Journal.this == ((Journal.View) o).journal();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + (int) (this.revision & Integer.MAX_VALUE);
            return hash;
        }
    }

    public List<Task<?, ?>> getList() {
        return new Journal.View();
    }
}
