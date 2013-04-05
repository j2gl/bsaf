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
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jdesktop.application;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Kevin
 */
public class JournalTest {
    @Before
    public void methodSetup() {
        WaitForStartupApplication.launchAndWait(WaitForStartupApplication.class);
    }

    private static class IdentifiedTask extends Task<Void, Void> {
        private final int id;

        public IdentifiedTask(int id) {
            super(Application.getInstance(WaitForStartupApplication.class));

            this.id = id;
        }

        @Override
        public String toString() {
            return Integer.toString(id);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + this.id;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IdentifiedTask other = (IdentifiedTask) obj;
            if (this.id != other.id) {
                return false;
            }
            return true;
        }

        @Override
        protected Void doInBackground() throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Test
    public void sequentialAdds() {
        Journal j = new Journal();

        List v0 = j.getList();
        j.addLast(new IdentifiedTask(0));
        List v1 = j.getList();
        j.addLast(new IdentifiedTask(1));
        List v2 = j.getList();
        j.addLast(new IdentifiedTask(2));
        List v3 = j.getList();
        j.addLast(new IdentifiedTask(3));
        List v4 = j.getList();

        assertEquals("v0.size() == 0", 0, v0.size());
        assertEquals("v1.size() == 1", 1, v1.size());
        assertEquals("v2.size() == 2", 2, v2.size());
        assertEquals("v3.size() == 3", 3, v3.size());
        assertEquals("v4.size() == 4", 4, v4.size());

        assertEquals("v1.toString().equals(\"[]\")", "[0]", v1.toString());
        assertEquals("v2.toString().equals(\"[]\")", "[0, 1]", v2.toString());
        assertEquals("v3.toString().equals(\"[]\")", "[0, 1, 2]", v3.toString());
        assertEquals("v4.toString().equals(\"[]\")", "[0, 1, 2, 3]", v4.toString());
    }

    @Test
    public void sequentialRemoves() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);
        List view = j.getList();

        // Verify contents before remove
        assertEquals("view.size() == 4", 4, view.size());
        assertEquals("view.toString().equals(\"[0, 1, 2, 3]\")", "[0, 1, 2, 3]", view.toString());
        j.remove(t0);
        // Verify unchanged contents after remove
        assertEquals("view.size() == 4", 4, view.size());
        assertEquals("view.toString().equals(\"[0, 1, 2, 3]\")", "[0, 1, 2, 3]", view.toString());

        // Get a new view which should show the remove
        view = j.getList();
        assertEquals("view.size() == 3", 3, view.size());
        assertEquals("view.toString().equals(\"[1, 2, 3]\")", "[1, 2, 3]", view.toString());
        j.remove(t1);
        assertEquals("view.size() == 3", 3, view.size());
        assertEquals("view.toString().equals(\"[1, 2, 3]\")", "[1, 2, 3]", view.toString());

        view = j.getList();
        assertEquals("view.size() == 2", 2, view.size());
        assertEquals("view.toString().equals(\"[2, 3]\")", "[2, 3]", view.toString());
        j.remove(t2);
        assertEquals("view.size() == 2", 2, view.size());
        assertEquals("view.toString().equals(\"[2, 3]\")", "[2, 3]", view.toString());

        view = j.getList();
        assertEquals("view.size() == 1", 1, view.size());
        assertEquals("view.toString().equals(\"[3]\")", "[3]", view.toString());
        j.remove(t3);
        assertEquals("view.size() == 1", 1, view.size());
        assertEquals("view.toString().equals(\"[3]\")", "[3]", view.toString());

        view = j.getList();
        assertEquals("view.toString().equals(\"[]\")", "[]", view.toString());
    }

    @Test
    public void listIterator() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);

        ListIterator<Task<?, ?>> itr0 = j.getList().listIterator(0);
        assertFalse("itr0.hasPrevious()", itr0.hasPrevious());
        assertTrue("!itr0.hasNext()", itr0.hasNext());
        assertEquals("itr0.next() == 0", "0", itr0.next().toString());
        assertEquals("itr0.next() == 1", "1", itr0.next().toString());
        assertEquals("itr0.next() == 2", "2", itr0.next().toString());
        assertEquals("itr0.next() == 3", "3", itr0.next().toString());
        try {
            itr0.next();
            fail("itr0.next() == null");
        } catch (NoSuchElementException e) {
        }

        ListIterator<Task<?, ?>> itr4 = j.getList().listIterator(4);
        assertTrue("!itr4.hasPrevious()", itr4.hasPrevious());
        assertFalse("itr4.hasNext()", itr4.hasNext());
        assertEquals("itr4.previous() == 3", "3", itr4.previous().toString());
        assertEquals("itr4.previous() == 2", "2", itr4.previous().toString());
        assertEquals("itr4.previous() == 1", "1", itr4.previous().toString());
        assertEquals("itr4.previous() == 0", "0", itr4.previous().toString());
        try {
            itr4.previous();
            fail("itr4.previous() == null");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void randomAccess() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);
        List view = j.getList();

        // Verify contents before remove
        assertEquals("view.get(3)", "3", view.get(3).toString());
        assertEquals("view.get(2)", "2", view.get(2).toString());
        assertEquals("view.get(1)", "1", view.get(1).toString());
        assertEquals("view.get(0)", "0", view.get(0).toString());

        assertEquals("view.get(1)", "1", view.get(1).toString());
        assertEquals("view.get(1)", "1", view.get(1).toString());

        assertEquals("view.get(2)", "2", view.get(2).toString());
        assertEquals("view.get(2)", "2", view.get(2).toString());
    }

    @Test
    public void compressHead() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);
        assertEquals("j.length()", 4, j.length());
        assertEquals("j.size()", 4, j.size());

        j.remove(t0);
        j.remove(t1);

        assertEquals("j.length()", 2, j.length());
        assertEquals("j.size()", 4, j.size());

        j.compressJournalLink(j.modCount());

        assertEquals("j.size()", 2, j.size());
        assertEquals("j.size()", 2, j.size());
        final List<Task<?, ?>> list = j.getList();
        assertEquals("view.get(0)", "2", list.get(0).toString());
        assertEquals("view.get(1)", "3", list.get(1).toString());
    }

    @Test
    public void compressMiddle() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);

        j.remove(t1);
        j.remove(t2);
        j.compressJournalLink(j.modCount());

        assertEquals("j.size()", 2, j.size());
        final List<Task<?, ?>> list = j.getList();
        assertEquals("view.get(0)", "0", list.get(0).toString());
        assertEquals("view.get(1)", "3", list.get(1).toString());
    }

    @Test
    public void compressTail() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);

        j.remove(t2);
        j.remove(t3);
        j.compressJournalLink(j.modCount());

        assertEquals("j.size()", 2, j.size());
        final List<Task<?, ?>> list = j.getList();
        assertEquals("view.get(0)", "0", list.get(0).toString());
        assertEquals("view.get(1)", "1", list.get(1).toString());
    }

    @Test
    public void compressMultipleSpans() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);
        final IdentifiedTask t4 = new IdentifiedTask(4);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);
        j.addLast(t4);

        j.remove(t0);
        j.remove(t2);
        j.remove(t4);
        j.compressJournalLink(j.modCount());

        assertEquals("j.size()", 2, j.size());
        final List<Task<?, ?>> list = j.getList();
        assertEquals("view.get(0)", "1", list.get(0).toString());
        assertEquals("view.get(1)", "3", list.get(1).toString());
    }

    @Test
    public void addAfterRemove() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(0);
        final IdentifiedTask t1 = new IdentifiedTask(1);
        final IdentifiedTask t2 = new IdentifiedTask(2);
        final IdentifiedTask t3 = new IdentifiedTask(3);
        final IdentifiedTask t4 = new IdentifiedTask(4);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.addLast(t3);
        List view = j.getList();

        // Verify contents before remove
        assertEquals("view.size() == 4", 4, view.size());
        assertEquals("view.toString().equals(\"[0, 1, 2, 3]\")", "[0, 1, 2, 3]", view.toString());
        j.remove(t1);
        j.remove(t2);
        // Verify unchanged contents after remove
        assertEquals("view.size() == 4", 4, view.size());
        assertEquals("view.toString().equals(\"[0, 1, 2, 3]\")", "[0, 1, 2, 3]", view.toString());

        // Get a new view which should show the removes
        view = j.getList();
        assertEquals("view.size() == 2", 2, view.size());
        assertEquals("view.toString().equals(\"[0, 3]\")", "[0, 3]", view.toString());
        j.addLast(t4);
        view = j.getList();
        assertEquals("view.size() == 3", 3, view.size());
        assertEquals("view.toString().equals(\"[0, 3, 4]\")", "[0, 3, 4]", view.toString());
    }

    @Test
    public void addDuplicates() {
        Journal j = new Journal();

        j.addLast(new IdentifiedTask(2));
        j.addLast(new IdentifiedTask(2));
        j.addLast(new IdentifiedTask(2));
        List view = j.getList();

        assertEquals("view.size() == 3", 3, view.size());
        assertEquals("view.toString().equals(\"[2, 2, 2]\")", "[2, 2, 2]", view.toString());
    }

    @Test
    public void removeDuplicates() {
        Journal j = new Journal();
        final IdentifiedTask t0 = new IdentifiedTask(2);
        final IdentifiedTask t1 = new IdentifiedTask(2);
        final IdentifiedTask t2 = new IdentifiedTask(2);

        j.addLast(t0);
        j.addLast(t1);
        j.addLast(t2);
        j.remove(t0);
        List view = j.getList();

        assertEquals("view.size() == 2", 2, view.size());
        assertEquals("view.toString().equals(\"[2, 2]\")", "[2, 2]", view.toString());

        j.remove(t1);
        view = j.getList();

        assertEquals("view.size() == 1", 1, view.size());
        assertEquals("view.toString().equals(\"[2]\")", "[2]", view.toString());

        j.remove(t2);
        view = j.getList();

        assertEquals("view.size() == 0", 0, view.size());
        assertEquals("view.toString().equals(\"[]\")", "[]", view.toString());
    }
}
