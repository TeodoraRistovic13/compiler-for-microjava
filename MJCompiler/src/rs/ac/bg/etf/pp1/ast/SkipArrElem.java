// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class SkipArrElem extends DesignatorArrayList {

    private DesignatorArrayList DesignatorArrayList;

    public SkipArrElem (DesignatorArrayList DesignatorArrayList) {
        this.DesignatorArrayList=DesignatorArrayList;
        if(DesignatorArrayList!=null) DesignatorArrayList.setParent(this);
    }

    public DesignatorArrayList getDesignatorArrayList() {
        return DesignatorArrayList;
    }

    public void setDesignatorArrayList(DesignatorArrayList DesignatorArrayList) {
        this.DesignatorArrayList=DesignatorArrayList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorArrayList!=null) DesignatorArrayList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorArrayList!=null) DesignatorArrayList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorArrayList!=null) DesignatorArrayList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SkipArrElem(\n");

        if(DesignatorArrayList!=null)
            buffer.append(DesignatorArrayList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SkipArrElem]");
        return buffer.toString();
    }
}
