// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:0


package rs.ac.bg.etf.pp1.ast;

public class ErrorProgDecl extends ProgramDecl {

    public ErrorProgDecl () {
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
        buffer.append("ErrorProgDecl(\n");

        buffer.append(tab);
        buffer.append(") [ErrorProgDecl]");
        return buffer.toString();
    }
}
