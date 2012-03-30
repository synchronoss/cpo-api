/**
 *
 *  Copyright (C) 2006  David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 * 
 */
 
/**
 * This Object is the basis for the CompositePattern. It can be both a composite 
 * or a component node. The isLeaf flag determines how it treats itself. It is important
 * for the inheriting classes to call setLeaf() to  tell the Node how to act.
*/

package org.synchronoss.cpo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This is a general Node class to be used to build
 * different types of trees. There are very few rules
 * in this class as they should be implemented by users
 * of this class.
 * 
 * @author David E. Berry
 * @version 1.0
 */
public class Node implements Serializable, Cloneable, Comparable<Node> {
    /** Version Id for this class. */
    private static final long serialVersionUID=1L;

    private static final int CHILD_NODE = 0;
//    private static final int PARENT_NODE = 1;
    
    /**
     * The parent node for this Node
     */
    private Node parent;

    /**
     * The first child in the linked list of children
     */
    private Node firstChild;

    /**
     * The previous sibling for this node
     */
    private Node prevSibling;

    /**
     * The next sibling for this node
     */
    private Node nextSibling;

    /**
     * Whether this node is allowed to have chidren.
     */
    private boolean allowChildren;

    /**
     * This is the default constructor for the Node class.
     * By default, it creates a Composite Node, that is,
     * a Node that can have children nodes.
     */
    protected Node() {
        // init all the attributes.
        release();
    }

    /**
     * This constructor allows you to create a composite or
     * component node based on the node_type.
     * 
     * @param nodeType nodeType can be one of two values:
     *                 
     *                 Node.ParentNode
     *                 Node.ChildNode
     */
    protected Node(int nodeType) {
        // init all the attributes.
        release();
        if(nodeType==CHILD_NODE)
            allowChildren=false;
    }

    /**
     * This is the factory method for creating Node objects.
     * 
     * @param nodeType nodeType can be one of two values:
     *                 
     *                 Node.ParentNode
     *                 Node.ChildNode
     *                 
     * @return an Instance of an Node
     */
    static public Node createNode(int nodeType) {
        return new Node(nodeType);
    }

    /**
    * Resets all the attributes to their default state. 
    *
    */
    public void release() {

        parent=null;
        firstChild=null;
        prevSibling=null;
        nextSibling=null;
        allowChildren=true;
    }

    protected void setAllowChildren(boolean ac) {
        allowChildren = ac;
    }

    public boolean getAllowChildren() {
        return this.allowChildren;
    }

    /**
     * Sets the Parent Node for this Node.
     * 
     * @param node   The node that will become the parent.
     * @see Node
     * @see Node
     * @see Node
     */
    public void setParent(Node node) {
        this.parent = node;
    }

    /**
     * Sets the PrevSibling for this node. It also sets
     * the NextSibling of the previous node to insure that
     * the doubly-linked list is maintained.
     * 
     * @param node   The node that will become the previous Sibling
     */
    public void setPrevSibling(Node node) {
        this.prevSibling = node;
        node.nextSibling=this;
    }

    /**
     * Sets the NextSibling for this node. It also sets
     * the PrevSibling of the next node to insure that
     * the doubly-linked list is maintained.
     * 
     * @param node   The node that will become the next Sibling
     */
    public void setNextSibling(Node node) {
        this.nextSibling = node;
        node.prevSibling=this;
    }

    /**
     * Gets the parent node for this node
     * 
     * @return an Node representing the parent of this node or
     *         null if no parent exists.
     */
    public Node getParentNode() {
        return(Node) this.parent;
    }

    /**
     * Gets the previous sibling for this node in the 
     * linked list of Nodes.
     * 
     * @return an Node that represents the previous sibling or 
     *         null if no sibling exists.
     */
    public Node getPrevSibling() {
        return this.prevSibling;
    }

    /**
     * Gets the next sibling for this node in the 
     * linked list of Nodes.
     * 
     * @return an Node that represents the next sibling or 
     *         null if no sibling exists.
     */
    public Node getNextSibling() {
        return this.nextSibling;
    }

    /**
     * Checks to see if this node has a parent.
     * 
     * @return boolean indicating true if this node has a parent,
     *         false if it has no parent.
     */
    public boolean hasParent() {
        return this.parent!=null;
    }

    /**
     * Checks to see if this node is a leaf node, that is, 
     * if it has no children.
     * 
     * @return boolean indicating true if it is a leafNode, 
     *         false if not
     */
    public boolean isLeaf() {
        return getFirstChild()==null;
    }

    /**
     * This function adds a child to the linked-list of 
     * children for this node. It adds the child to the 
     * end of the list.
     * 
     * @param node   Node that is the node to be added as a child of
     *               this Node.
     * @exception NodeException
     */
    public void addChild(Node node) throws ChildNodeException
    {
        Node lastChild = null;

        if(node != null) {
            if(!allowChildren) {
                throw new ChildNodeException();
            }

            if(getFirstChild()==null) {
                setFirstChild(node);
                getFirstChild().setPrevSibling(firstChild);
                getFirstChild().setNextSibling(firstChild);
            } else {    // Add it to the end of the list
                lastChild = getFirstChild().getPrevSibling();
                if(lastChild!=null) {
                    lastChild.setNextSibling(node);
                }
                node.setNextSibling(getFirstChild());
            }
            node.setParent((Node)this);
        }
    }

    /**
     * This function adds a child to the linked-list of 
     * children for this node. It adds the child to the 
     * end of the list.
     * 
     * @param node   Node that is the node to be added as a child of
     *               this Node.
     * @exception NodeException
     */
    public void addChildSort(Node node) throws ChildNodeException
    {
        addChildSort(node, null);
    }

    public void addChildSort(Node node, Comparator<Node> c) throws ChildNodeException
    {
        Node lastChild = null;
        Node currNode=null;

        if(node != null) {
            if(!allowChildren) {
                throw new ChildNodeException();
            }

            if(isLeaf()) {
                setFirstChild(node);
                getFirstChild().setPrevSibling(node);
                getFirstChild().setNextSibling(node);
            } else {    // Add it in sorted order
                boolean added = false;
                currNode = getFirstChild();
                do {
                    if(doCompare(node,currNode, c)<0) {
                        node.setPrevSibling(currNode.getPrevSibling());
                        node.setNextSibling(currNode);
                        if(currNode==getFirstChild())
                            setFirstChild(node);
                        added = true;
                        break;
                    }

                    currNode = currNode.getNextSibling();               
                } while(currNode != getFirstChild() );

                if(!added) { // add to the end of the list.
                    lastChild = getFirstChild().getPrevSibling();
                    if(lastChild!=null) {
                        lastChild.setNextSibling(node);
                    }
                    node.setNextSibling(getFirstChild());
                }
            }
            node.setParent((Node)this);
        }
    }

    protected int doCompare(Node n1, Node n2, Comparator<Node> c) {
        int rc;

        if(c!=null)
            rc = c.compare(n1, n2);
        else if(n1 == null && n1==n2)
            rc =0;
        else
            rc = n1.compareTo(n2);

        return rc;
    }

    /**
     * Inserts a Sibling into the linked list just prior 
     * to this Node
     * 
     * @param node   Node to be made the prevSibling
     */
    public void insertSiblingBefore(Node node) throws ChildNodeException {
        if(node!=null) {
            node.setPrevSibling(getPrevSibling());
            node.setNextSibling(this);
        }
    }

    /**
     * Adds a Sibling immediately following this Node.
     * 
     * @param node   Node to be made the next sibling
     */
    public void insertSiblingAfter(Node node) {
        if(node!=null) {
            node.setNextSibling(getNextSibling());
            node.setPrevSibling(this);
        }
    }

    /**
     * Inserts a new Parent into the tree structure and
     * adds this node as its child.
     * 
     * @param node   Node that will become this nodes new Parent.
     */
    public void insertParentBefore(Node node) throws ChildNodeException{
        if(node != null) {
            if(hasParent()) {
                getParentNode().addChild(node);
                getParentNode().removeChild(this);
            }
            node.addChild(this);
        }
    }

    /**
     * Inserts a new Parent Node as a child of this node
     * and moves all pre-existing children to be children of 
     * the new Parent Node.
     * 
     * @param node   Node to become a child of this node and parent to
     *               all pre-existing children of this node.
     */
    public void insertParentAfter(Node node) throws ChildNodeException {
        if(node != null) {
            // give this node my children
            node.setFirstChild(getFirstChild());

            setFirstChild(null); // clear our list
            addChild(node);      // make our child
        }
    }

    /**
     * Searches for an immediate child node and if found removes 
     * it from the linked-list of children. 
     * 
     * @param node   Node to be searched for and removed if found.
     */
    public boolean removeChild(Node node) throws ChildNodeException{
        Node currNode = getFirstChild();
        boolean rc = false;

        if(!allowChildren) {
            throw new ChildNodeException();
        }

        if(node!=null && !isLeaf()) {
            // Is this the first and only child
            if(node == currNode && node == currNode.getNextSibling()) {
                setFirstChild(null);
                rc = true;
            } else {
                // Verify that this Node is a child here
                do {
                    if(currNode == node) {
                        // Remove this child
                        if(node.getPrevSibling()!=null) {
                            node.getPrevSibling().setNextSibling(node.getNextSibling());
                        }
                        if(node == getFirstChild())
                            setFirstChild(node.getNextSibling());

                        rc = true;

                        // do not release. Item.resolve expects the 
                        // pointers to be intact after a remove
                        //node.release();
                        break;
                    }
                    currNode = currNode.getNextSibling();
                } while(currNode != getFirstChild()  );
            }
        }

        return rc;
    }

    /**
     * Remove just this node from the tree. The children of this
     * node get attached to the parent.
     */
    public void removeChildNode() throws ChildNodeException{
        Node parentLast;
        Node thisLast;

        // Add this nodes children to the end of the Parents children list
        if(!isLeaf() && hasParent()) {
            parentLast = getParentNode().getFirstChild().getPrevSibling();
            thisLast = getFirstChild().getPrevSibling();

            // add the first child to the end of the parent list
            parentLast.setNextSibling(getFirstChild());

            //point the last child to the start of the parent list
            thisLast.setNextSibling(getParentNode().getFirstChild());

            // Now remove self
            getParentNode().removeChild(this);
        }
    }

    /**
     * Remove this node and all its children from the tree.
     */
    public void removeAll() throws ChildNodeException{
        if(hasParent()) {
            getParentNode().removeChild(this);
        }
    }

    /**
     * Gets the first child node in the linked-list of 
     * children.
     * 
     * @return Node reference to the first child node in the 
     *         linked-list of children
     */
    public Node getFirstChild() {
        return this.firstChild;
    }

    /**
     * Sets the first child node in the linked-list of 
     * children.
     * 
     * @param node   Node which will be made the first child node in
     *               the linked-list of children.
     */
    public void setFirstChild(Node node) throws ChildNodeException{
        if(!allowChildren) {
            throw new ChildNodeException();
        }
        this.firstChild=node;
    }

    /**
     * Implements the visitor pattern. This is a Depth-based
     * traversal that will call the INodeVisitor
     * visitBegin(), visitMiddle(), and
     * visitEnd() for parent nodes and will call
     * visit() for leaf nodes.
     * 
     * @param nv     INodeVisitor to call upon reaching a node
     *               when traversing the tree.
     * @see INodeVisitor
     */
    public boolean acceptDFVisitor(INodeVisitor nv) throws Exception {
        Node currNode;
        boolean continueVisit=true;

        if(nv != null) {
            if(isLeaf())
                continueVisit = nv.visit(this);
            else {
                continueVisit = nv.visitBegin(this);
                if(continueVisit) {
                    currNode = getFirstChild();
                    do {
                        continueVisit = currNode.acceptDFVisitor(nv);
                        if(continueVisit) {
                            currNode = currNode.getNextSibling();
                            if( currNode == getFirstChild())
                                break;
                            continueVisit = nv.visitMiddle(this);
                        }
                    } while(continueVisit);
                    if(continueVisit)
                        continueVisit = nv.visitEnd(this);
                }
            }
        }
        return continueVisit;
    }

    /**
     * Implements the visitor pattern. This is a Breadth-based
     * traversal that will call the INodeVisitor.visit() for 
     * all nodes.
     * 
     * @param nv     INodeVisitor to call upon reaching a node
     *               when traversing the tree.
     * @see INodeVisitor
     */
    public boolean acceptBFVisitor(INodeVisitor nv) throws Exception {
        Queue queue = new Queue();
        Node parentNode;
        Node childNode;
        boolean continueVisit=true;

        if(nv != null) {
            queue.put(this);
            while(!queue.isEmpty()) {
                parentNode = (Node)queue.get();
                continueVisit = nv.visit(this);
                if(!continueVisit)
                    break;
                if(!parentNode.isLeaf()) {
                    childNode = parentNode.getFirstChild();
                    do {
                        queue.put(childNode);
                        childNode = childNode.getNextSibling();
                    } while(childNode != parentNode.getFirstChild());
                }
            }
        }
        return continueVisit;
    }

    public int getChildCount() {
        Node currNode;
        int count = 0;

        //Do we have any children
        if(!isLeaf()) {
            currNode = getFirstChild();
            do {
                ++count;
                currNode = currNode.getNextSibling();
            } while(currNode != getFirstChild());
        }

        return count;
    }

    public List<Node> getChildList() {
        Node currNode;
        ArrayList<Node> al = new ArrayList<Node>();

        //Do we have any children
        if(!isLeaf()) {
            currNode = getFirstChild();
            do {
                al.add(currNode);
                currNode = currNode.getNextSibling();
            } while(currNode != getFirstChild());
        }

        return al;
    }

  @Override
    public Object clone()
    throws CloneNotSupportedException {
        Node thisClone = (Node) super.clone();
        Node currNode;

        thisClone.release(); //Clear all the attributes
        thisClone.setAllowChildren(getAllowChildren());

        //Do we have any children
        if(!isLeaf()) {
            currNode = getFirstChild();
            do {
                try {
                    thisClone.addChild((Node)currNode.clone());
                } catch(ChildNodeException e) {
                    // This should not happen since we are cloning a parent if we got here.
                }
                currNode = currNode.getNextSibling();
            } while(currNode != getFirstChild());
        }

        return thisClone;
    }

  @Override
    public int compareTo(Node o) {

        int rc;

        if(this.hashCode() < o.hashCode())
            rc=-1;
        else if(this.hashCode() > o.hashCode())
            rc = 1;
        else
            rc = 0;

        return rc;
    }

    public boolean equals(Node o) {
        return this==o;
    }
}
