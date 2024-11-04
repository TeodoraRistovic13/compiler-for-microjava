// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class ForLoop extends Statement {

    private ForDesignatorStmts ForDesignatorStmts;
    private ForCondFact ForCondFact;
    private ForLoopStmts ForLoopStmts;
    private ForLoopBody ForLoopBody;

    public ForLoop (ForDesignatorStmts ForDesignatorStmts, ForCondFact ForCondFact, ForLoopStmts ForLoopStmts, ForLoopBody ForLoopBody) {
        this.ForDesignatorStmts=ForDesignatorStmts;
        if(ForDesignatorStmts!=null) ForDesignatorStmts.setParent(this);
        this.ForCondFact=ForCondFact;
        if(ForCondFact!=null) ForCondFact.setParent(this);
        this.ForLoopStmts=ForLoopStmts;
        if(ForLoopStmts!=null) ForLoopStmts.setParent(this);
        this.ForLoopBody=ForLoopBody;
        if(ForLoopBody!=null) ForLoopBody.setParent(this);
    }

    public ForDesignatorStmts getForDesignatorStmts() {
        return ForDesignatorStmts;
    }

    public void setForDesignatorStmts(ForDesignatorStmts ForDesignatorStmts) {
        this.ForDesignatorStmts=ForDesignatorStmts;
    }

    public ForCondFact getForCondFact() {
        return ForCondFact;
    }

    public void setForCondFact(ForCondFact ForCondFact) {
        this.ForCondFact=ForCondFact;
    }

    public ForLoopStmts getForLoopStmts() {
        return ForLoopStmts;
    }

    public void setForLoopStmts(ForLoopStmts ForLoopStmts) {
        this.ForLoopStmts=ForLoopStmts;
    }

    public ForLoopBody getForLoopBody() {
        return ForLoopBody;
    }

    public void setForLoopBody(ForLoopBody ForLoopBody) {
        this.ForLoopBody=ForLoopBody;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ForDesignatorStmts!=null) ForDesignatorStmts.accept(visitor);
        if(ForCondFact!=null) ForCondFact.accept(visitor);
        if(ForLoopStmts!=null) ForLoopStmts.accept(visitor);
        if(ForLoopBody!=null) ForLoopBody.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ForDesignatorStmts!=null) ForDesignatorStmts.traverseTopDown(visitor);
        if(ForCondFact!=null) ForCondFact.traverseTopDown(visitor);
        if(ForLoopStmts!=null) ForLoopStmts.traverseTopDown(visitor);
        if(ForLoopBody!=null) ForLoopBody.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ForDesignatorStmts!=null) ForDesignatorStmts.traverseBottomUp(visitor);
        if(ForCondFact!=null) ForCondFact.traverseBottomUp(visitor);
        if(ForLoopStmts!=null) ForLoopStmts.traverseBottomUp(visitor);
        if(ForLoopBody!=null) ForLoopBody.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ForLoop(\n");

        if(ForDesignatorStmts!=null)
            buffer.append(ForDesignatorStmts.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForCondFact!=null)
            buffer.append(ForCondFact.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForLoopStmts!=null)
            buffer.append(ForLoopStmts.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForLoopBody!=null)
            buffer.append(ForLoopBody.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ForLoop]");
        return buffer.toString();
    }
}
