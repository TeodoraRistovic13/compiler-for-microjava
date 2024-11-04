// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class ForLoopStmts implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private ForDesignatorStmts ForDesignatorStmts;

    public ForLoopStmts (ForDesignatorStmts ForDesignatorStmts) {
        this.ForDesignatorStmts=ForDesignatorStmts;
        if(ForDesignatorStmts!=null) ForDesignatorStmts.setParent(this);
    }

    public ForDesignatorStmts getForDesignatorStmts() {
        return ForDesignatorStmts;
    }

    public void setForDesignatorStmts(ForDesignatorStmts ForDesignatorStmts) {
        this.ForDesignatorStmts=ForDesignatorStmts;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ForDesignatorStmts!=null) ForDesignatorStmts.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ForDesignatorStmts!=null) ForDesignatorStmts.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ForDesignatorStmts!=null) ForDesignatorStmts.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ForLoopStmts(\n");

        if(ForDesignatorStmts!=null)
            buffer.append(ForDesignatorStmts.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ForLoopStmts]");
        return buffer.toString();
    }
}
