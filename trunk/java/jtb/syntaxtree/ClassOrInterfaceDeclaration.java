//
// Generated by JTB 1.3.2
//

package jtb.syntaxtree;

/**
 * Grammar production:
 * f0 -> ( "class" | "interface" )
 * f1 -> <IDENTIFIER>
 * f2 -> [ TypeParameters() ]
 * f3 -> [ ExtendsList(isInterface) ]
 * f4 -> [ ImplementsList(isInterface) ]
 * f5 -> ClassOrInterfaceBody(isInterface)
 */
public class ClassOrInterfaceDeclaration implements Node {
   static final long serialVersionUID = 20050923L;

   private Node parent;
   public NodeChoice f0;
   public NodeToken f1;
   public NodeOptional f2;
   public NodeOptional f3;
   public NodeOptional f4;
   public ClassOrInterfaceBody f5;

   public ClassOrInterfaceDeclaration(NodeChoice n0, NodeToken n1, NodeOptional n2, NodeOptional n3, NodeOptional n4, ClassOrInterfaceBody n5) {
      f0 = n0;
      if ( f0 != null ) f0.setParent(this);
      f1 = n1;
      if ( f1 != null ) f1.setParent(this);
      f2 = n2;
      if ( f2 != null ) f2.setParent(this);
      f3 = n3;
      if ( f3 != null ) f3.setParent(this);
      f4 = n4;
      if ( f4 != null ) f4.setParent(this);
      f5 = n5;
      if ( f5 != null ) f5.setParent(this);
   }

   public void accept(jtb.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(jtb.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(jtb.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(jtb.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
   public void setParent(Node n) { parent = n; }
   public Node getParent()       { return parent; }
}
