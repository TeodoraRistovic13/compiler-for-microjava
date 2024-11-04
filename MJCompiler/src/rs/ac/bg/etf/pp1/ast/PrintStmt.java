// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class PrintStmt extends Statement {

    private Expr Expr;
    private PrintAdditionalParam PrintAdditionalParam;

    public PrintStmt (Expr Expr, PrintAdditionalParam PrintAdditionalParam) {
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.PrintAdditionalParam=PrintAdditionalParam;
        if(PrintAdditionalParam!=null) PrintAdditionalParam.setParent(this);
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public PrintAdditionalParam getPrintAdditionalParam() {
        return PrintAdditionalParam;
    }

    public void setPrintAdditionalParam(PrintAdditionalParam PrintAdditionalParam) {
        this.PrintAdditionalParam=PrintAdditionalParam;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
        if(PrintAdditionalParam!=null) PrintAdditionalParam.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(PrintAdditionalParam!=null) PrintAdditionalParam.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(PrintAdditionalParam!=null) PrintAdditionalParam.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("PrintStmt(\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(PrintAdditionalParam!=null)
            buffer.append(PrintAdditionalParam.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintStmt]");
        return buffer.toString();
    }
}
