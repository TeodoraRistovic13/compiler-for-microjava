// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:0


package rs.ac.bg.etf.pp1.ast;

public class AssignmentError extends Statement {

    public AssignmentError () {
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
        buffer.append("AssignmentError(\n");

        buffer.append(tab);
        buffer.append(") [AssignmentError]");
        return buffer.toString();
    }
}
