// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:0


package rs.ac.bg.etf.pp1.ast;

public class NamespaceDecls extends NamespaceDeclList {

    private NamespaceDeclList NamespaceDeclList;
    private NamespaceDecl NamespaceDecl;

    public NamespaceDecls (NamespaceDeclList NamespaceDeclList, NamespaceDecl NamespaceDecl) {
        this.NamespaceDeclList=NamespaceDeclList;
        if(NamespaceDeclList!=null) NamespaceDeclList.setParent(this);
        this.NamespaceDecl=NamespaceDecl;
        if(NamespaceDecl!=null) NamespaceDecl.setParent(this);
    }

    public NamespaceDeclList getNamespaceDeclList() {
        return NamespaceDeclList;
    }

    public void setNamespaceDeclList(NamespaceDeclList NamespaceDeclList) {
        this.NamespaceDeclList=NamespaceDeclList;
    }

    public NamespaceDecl getNamespaceDecl() {
        return NamespaceDecl;
    }

    public void setNamespaceDecl(NamespaceDecl NamespaceDecl) {
        this.NamespaceDecl=NamespaceDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(NamespaceDeclList!=null) NamespaceDeclList.accept(visitor);
        if(NamespaceDecl!=null) NamespaceDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(NamespaceDeclList!=null) NamespaceDeclList.traverseTopDown(visitor);
        if(NamespaceDecl!=null) NamespaceDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(NamespaceDeclList!=null) NamespaceDeclList.traverseBottomUp(visitor);
        if(NamespaceDecl!=null) NamespaceDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NamespaceDecls(\n");

        if(NamespaceDeclList!=null)
            buffer.append(NamespaceDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(NamespaceDecl!=null)
            buffer.append(NamespaceDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NamespaceDecls]");
        return buffer.toString();
    }
}
