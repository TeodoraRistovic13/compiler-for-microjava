// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:0


package rs.ac.bg.etf.pp1.ast;

public class NamespaceList extends ProgramNamespaceList {

    private ProgramNamespaceList ProgramNamespaceList;
    private Namespace Namespace;

    public NamespaceList (ProgramNamespaceList ProgramNamespaceList, Namespace Namespace) {
        this.ProgramNamespaceList=ProgramNamespaceList;
        if(ProgramNamespaceList!=null) ProgramNamespaceList.setParent(this);
        this.Namespace=Namespace;
        if(Namespace!=null) Namespace.setParent(this);
    }

    public ProgramNamespaceList getProgramNamespaceList() {
        return ProgramNamespaceList;
    }

    public void setProgramNamespaceList(ProgramNamespaceList ProgramNamespaceList) {
        this.ProgramNamespaceList=ProgramNamespaceList;
    }

    public Namespace getNamespace() {
        return Namespace;
    }

    public void setNamespace(Namespace Namespace) {
        this.Namespace=Namespace;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ProgramNamespaceList!=null) ProgramNamespaceList.accept(visitor);
        if(Namespace!=null) Namespace.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ProgramNamespaceList!=null) ProgramNamespaceList.traverseTopDown(visitor);
        if(Namespace!=null) Namespace.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ProgramNamespaceList!=null) ProgramNamespaceList.traverseBottomUp(visitor);
        if(Namespace!=null) Namespace.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NamespaceList(\n");

        if(ProgramNamespaceList!=null)
            buffer.append(ProgramNamespaceList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Namespace!=null)
            buffer.append(Namespace.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NamespaceList]");
        return buffer.toString();
    }
}
