// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class DesigStmtArrayAssign extends DesignatorStatement {

    private DesignatorArrayList DesignatorArrayList;
    private ArrayAfterStar ArrayAfterStar;
    private Designator Designator;

    public DesigStmtArrayAssign (DesignatorArrayList DesignatorArrayList, ArrayAfterStar ArrayAfterStar, Designator Designator) {
        this.DesignatorArrayList=DesignatorArrayList;
        if(DesignatorArrayList!=null) DesignatorArrayList.setParent(this);
        this.ArrayAfterStar=ArrayAfterStar;
        if(ArrayAfterStar!=null) ArrayAfterStar.setParent(this);
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
    }

    public DesignatorArrayList getDesignatorArrayList() {
        return DesignatorArrayList;
    }

    public void setDesignatorArrayList(DesignatorArrayList DesignatorArrayList) {
        this.DesignatorArrayList=DesignatorArrayList;
    }

    public ArrayAfterStar getArrayAfterStar() {
        return ArrayAfterStar;
    }

    public void setArrayAfterStar(ArrayAfterStar ArrayAfterStar) {
        this.ArrayAfterStar=ArrayAfterStar;
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
        if(ArrayAfterStar!=null) ArrayAfterStar.accept(visitor);
        if(Designator!=null) Designator.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorArrayList!=null) DesignatorArrayList.traverseTopDown(visitor);
        if(ArrayAfterStar!=null) ArrayAfterStar.traverseTopDown(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorArrayList!=null) DesignatorArrayList.traverseBottomUp(visitor);
        if(ArrayAfterStar!=null) ArrayAfterStar.traverseBottomUp(visitor);
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesigStmtArrayAssign(\n");

        if(DesignatorArrayList!=null)
            buffer.append(DesignatorArrayList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ArrayAfterStar!=null)
            buffer.append(ArrayAfterStar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesigStmtArrayAssign]");
        return buffer.toString();
    }
}
