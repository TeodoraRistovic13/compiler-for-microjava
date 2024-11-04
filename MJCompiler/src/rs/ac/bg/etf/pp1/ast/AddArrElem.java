// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class AddArrElem extends DesignatorArrayList {

    private DesignatorArrayList DesignatorArrayList;
    private Designator Designator;

    public AddArrElem (DesignatorArrayList DesignatorArrayList, Designator Designator) {
        this.DesignatorArrayList=DesignatorArrayList;
        if(DesignatorArrayList!=null) DesignatorArrayList.setParent(this);
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
    }

    public DesignatorArrayList getDesignatorArrayList() {
        return DesignatorArrayList;
    }

    public void setDesignatorArrayList(DesignatorArrayList DesignatorArrayList) {
        this.DesignatorArrayList=DesignatorArrayList;
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorArrayList!=null) DesignatorArrayList.accept(visitor);
        if(Designator!=null) Designator.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorArrayList!=null) DesignatorArrayList.traverseTopDown(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorArrayList!=null) DesignatorArrayList.traverseBottomUp(visitor);
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AddArrElem(\n");

        if(DesignatorArrayList!=null)
            buffer.append(DesignatorArrayList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AddArrElem]");
        return buffer.toString();
    }
}
