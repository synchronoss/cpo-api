package org.synchronoss.cpo.core;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.testng.annotations.Test;

/** Unit tests for the Node tree structure. */
public class NodeTest {

  private static final int CHILD_NODE = 0;
  private static final int PARENT_NODE = 1;

  /** A node with a stable ordering value for the sorted-add tests. */
  private static class ValueNode extends Node {
    private final int value;

    ValueNode(int value) {
      this.value = value;
    }

    int getValue() {
      return value;
    }

    @Override
    public int compareTo(Node o) {
      return Integer.compare(value, ((ValueNode) o).value);
    }
  }

  /** Records the traversal order of a depth-first visit. */
  private static class RecordingVisitor implements NodeVisitor {
    final List<String> calls = new ArrayList<>();
    private final String cancelAt;

    RecordingVisitor(String cancelAt) {
      this.cancelAt = cancelAt;
    }

    private boolean record(String call) {
      calls.add(call);
      return !call.equals(cancelAt);
    }

    @Override
    public boolean visitBegin(Node node) {
      return record("begin:" + name(node));
    }

    @Override
    public boolean visitMiddle(Node node) {
      return record("middle:" + name(node));
    }

    @Override
    public boolean visitEnd(Node node) {
      return record("end:" + name(node));
    }

    @Override
    public boolean visit(Node node) {
      return record("leaf:" + name(node));
    }

    private String name(Node node) {
      return node instanceof ValueNode ? String.valueOf(((ValueNode) node).getValue()) : "root";
    }
  }

  @Test
  public void testCreateNodeTypes() throws Exception {
    Node parent = Node.createNode(PARENT_NODE);
    assertTrue(parent.getAllowChildren(), "non-child node should allow children");

    Node child = Node.createNode(CHILD_NODE);
    assertFalse(child.getAllowChildren(), "child node should not allow children");

    expectThrows(ChildNodeException.class, () -> child.addChild(Node.createNode(PARENT_NODE)));
    expectThrows(ChildNodeException.class, () -> child.addChildSort(Node.createNode(PARENT_NODE)));
    expectThrows(ChildNodeException.class, () -> child.setFirstChild(Node.createNode(PARENT_NODE)));
    expectThrows(ChildNodeException.class, () -> child.removeChild(Node.createNode(PARENT_NODE)));
  }

  @Test
  public void testAddChildAndCounts() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    assertTrue(root.isLeaf(), "a fresh node is a leaf");
    assertEquals(root.getChildCount(), 0);
    assertTrue(root.getChildList().isEmpty());

    ValueNode a = new ValueNode(1);
    ValueNode b = new ValueNode(2);
    ValueNode c = new ValueNode(3);
    root.addChild(a);
    root.addChild(b);
    root.addChild(c);
    root.addChild(null); // no-op

    assertFalse(root.isLeaf());
    assertEquals(root.getChildCount(), 3);
    assertEquals(root.getChildList(), List.of(a, b, c));
    assertSame(root.getFirstChild(), a);
    assertSame(a.getParentNode(), root);
    assertTrue(a.hasParent());
    assertFalse(root.hasParent());

    // circular sibling links
    assertSame(a.getNextSibling(), b);
    assertSame(b.getNextSibling(), c);
    assertSame(c.getNextSibling(), a);
    assertSame(a.getPrevSibling(), c);
  }

  @Test
  public void testAddChildSort() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    root.addChildSort(new ValueNode(20)); // first child
    root.addChildSort(new ValueNode(10)); // sorts to the head
    root.addChildSort(new ValueNode(30)); // sorts to the end
    root.addChildSort(new ValueNode(25)); // sorts into the middle
    root.addChildSort(null); // no-op

    List<Integer> order =
        root.getChildList().stream().map(n -> ((ValueNode) n).getValue()).toList();
    assertEquals(order, List.of(10, 20, 25, 30));
  }

  @Test
  public void testAddChildSortWithComparator() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    Comparator<Node> reverse =
        Comparator.comparingInt((Node n) -> ((ValueNode) n).getValue()).reversed();
    root.addChildSort(new ValueNode(10), reverse);
    root.addChildSort(new ValueNode(30), reverse);
    root.addChildSort(new ValueNode(20), reverse);

    List<Integer> order =
        root.getChildList().stream().map(n -> ((ValueNode) n).getValue()).toList();
    assertEquals(order, List.of(30, 20, 10));
  }

  @Test
  public void testDoCompare() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    assertEquals(root.doCompare(null, null, null), 0, "two nulls compare equal");
    ValueNode small = new ValueNode(1);
    ValueNode big = new ValueNode(2);
    assertTrue(root.doCompare(small, big, null) < 0);
    assertTrue(root.doCompare(small, big, Comparator.comparingInt(Node::hashCode).reversed()) != 0);
  }

  @Test
  public void testInsertSiblings() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    ValueNode a = new ValueNode(1);
    ValueNode c = new ValueNode(3);
    root.addChild(a);
    root.addChild(c);

    ValueNode b = new ValueNode(2);
    c.insertSiblingBefore(b);
    assertEquals(root.getChildList(), List.of(a, b, c));

    ValueNode d = new ValueNode(4);
    c.insertSiblingAfter(d);
    assertSame(c.getNextSibling(), d);
    assertSame(d.getPrevSibling(), c);

    // null inserts are no-ops
    c.insertSiblingBefore(null);
    c.insertSiblingAfter(null);
  }

  @Test
  public void testInsertParentBefore() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    ValueNode child = new ValueNode(1);
    root.addChild(child);

    Node newParent = Node.createNode(PARENT_NODE);
    child.insertParentBefore(newParent);

    assertSame(child.getParentNode(), newParent);
    assertSame(newParent.getParentNode(), root);

    // and with no pre-existing parent
    Node orphan = new ValueNode(9);
    Node adopter = Node.createNode(PARENT_NODE);
    orphan.insertParentBefore(adopter);
    assertSame(orphan.getParentNode(), adopter);

    orphan.insertParentBefore(null); // no-op
  }

  @Test
  public void testInsertParentAfter() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    ValueNode a = new ValueNode(1);
    ValueNode b = new ValueNode(2);
    root.addChild(a);
    root.addChild(b);

    Node inserted = Node.createNode(PARENT_NODE);
    root.insertParentAfter(inserted);

    assertEquals(root.getChildCount(), 1, "root should now have only the inserted node");
    assertSame(root.getFirstChild(), inserted);
    assertEquals(inserted.getChildList(), List.of(a, b));

    root.insertParentAfter(null); // no-op
  }

  @Test
  public void testRemoveChild() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    ValueNode a = new ValueNode(1);
    ValueNode b = new ValueNode(2);
    ValueNode c = new ValueNode(3);
    root.addChild(a);
    root.addChild(b);
    root.addChild(c);

    assertTrue(root.removeChild(b), "middle child should be removed");
    assertEquals(root.getChildList(), List.of(a, c));

    assertTrue(root.removeChild(a), "first child should be removed");
    assertEquals(root.getChildList(), List.of(c));

    assertFalse(root.removeChild(new ValueNode(9)), "non-child is not removed");
    assertFalse(root.removeChild(null), "null is not removed");

    assertTrue(root.removeChild(c), "only child should be removed");
    assertTrue(root.isLeaf());
    assertFalse(root.removeChild(c), "removing from a leaf returns false");
  }

  @Test
  public void testRemoveChildNodeReattachesChildren() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    Node middle = Node.createNode(PARENT_NODE);
    ValueNode grandChild = new ValueNode(1);
    root.addChild(middle);
    middle.addChild(grandChild);

    middle.removeChildNode();

    assertEquals(root.getChildList(), List.of(grandChild), "grandchild moves up to root");

    // removing a leaf or parentless node is a no-op
    grandChild.removeChildNode();
    Node loner = Node.createNode(PARENT_NODE);
    loner.removeChildNode();
  }

  @Test
  public void testRemoveAll() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    Node child = Node.createNode(PARENT_NODE);
    child.addChild(new ValueNode(1));
    root.addChild(child);

    child.removeAll();
    assertTrue(root.isLeaf(), "child subtree should be removed from root");

    Node orphan = Node.createNode(PARENT_NODE);
    orphan.removeAll(); // no parent, no-op
  }

  @Test
  public void testAcceptDFVisitorFullTraversal() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    ValueNode a = new ValueNode(1);
    ValueNode b = new ValueNode(2);
    root.addChild(a);
    root.addChild(b);

    RecordingVisitor visitor = new RecordingVisitor(null);
    assertTrue(root.acceptDFVisitor(visitor));
    assertEquals(
        visitor.calls, List.of("begin:root", "leaf:1", "middle:root", "leaf:2", "end:root"));

    // a lone leaf gets a plain visit
    RecordingVisitor leafVisitor = new RecordingVisitor(null);
    assertTrue(new ValueNode(9).acceptDFVisitor(leafVisitor));
    assertEquals(leafVisitor.calls, List.of("leaf:9"));

    // a null visitor is a no-op that continues
    assertTrue(root.acceptDFVisitor(null));
  }

  @Test
  public void testAcceptDFVisitorCancellation() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    root.addChild(new ValueNode(1));
    root.addChild(new ValueNode(2));

    assertFalse(root.acceptDFVisitor(new RecordingVisitor("begin:root")), "cancel at begin");
    assertFalse(root.acceptDFVisitor(new RecordingVisitor("leaf:1")), "cancel at first leaf");
    assertFalse(root.acceptDFVisitor(new RecordingVisitor("middle:root")), "cancel at middle");
    assertFalse(root.acceptDFVisitor(new RecordingVisitor("end:root")), "cancel at end");
  }

  @Test
  public void testClone() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    ValueNode a = new ValueNode(1);
    ValueNode b = new ValueNode(2);
    root.addChild(a);
    root.addChild(b);

    Node cloned = (Node) root.clone();
    assertNotSame(cloned, root);
    assertEquals(cloned.getChildCount(), 2);
    assertNotSame(cloned.getFirstChild(), a, "children should be cloned too");

    Node leafClone = (Node) new ValueNode(9).clone();
    assertTrue(leafClone.isLeaf());
  }

  @Test
  public void testCompareToAndEquals() {
    Node a = Node.createNode(PARENT_NODE);
    Node b = Node.createNode(PARENT_NODE);
    assertEquals(a.compareTo(a), 0, "a node compares equal to itself");
    assertEquals(a.compareTo(b), -b.compareTo(a), "comparisons should be antisymmetric");
    assertTrue(a.equals(a));
    assertFalse(a.equals(b));
  }

  @Test
  public void testRelease() throws Exception {
    Node root = Node.createNode(PARENT_NODE);
    Node parent = Node.createNode(PARENT_NODE);
    ValueNode child = new ValueNode(1);
    parent.addChild(child);
    root.addChild(parent);

    parent.release();
    assertTrue(parent.isLeaf());
    assertFalse(parent.hasParent());
    assertNull(parent.getNextSibling());
    assertNull(parent.getPrevSibling());
    assertTrue(parent.getAllowChildren());
  }
}
