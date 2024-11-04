// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class PrintVar extends PrintAdditionalParam {

    private Integer printParam;

    public PrintVar (Integer printParam) {
        this.printParam=printParam;
    }

    public Integer getPrintParam() {
        return printParam;
    }

    public void setPrintParam(Integer printParam) {
        this.printParam=printParam;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("PrintVar(\n");

        buffer.append(" "+tab+printParam);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintVar]");
        return buffer.toString();
    }
}
