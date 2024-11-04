// generated with ast extension for cup
// version 0.8
// 15/0/2024 16:55:1


package rs.ac.bg.etf.pp1.ast;

public class ScopeDesigVar extends Designator {

    private String namespaceName;
    private String desigVarName;

    public ScopeDesigVar (String namespaceName, String desigVarName) {
        this.namespaceName=namespaceName;
        this.desigVarName=desigVarName;
    }

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName=namespaceName;
    }

    public String getDesigVarName() {
        return desigVarName;
    }

    public void setDesigVarName(String desigVarName) {
        this.desigVarName=desigVarName;
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
        buffer.append("ScopeDesigVar(\n");

        buffer.append(" "+tab+namespaceName);
        buffer.append("\n");

        buffer.append(" "+tab+desigVarName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ScopeDesigVar]");
        return buffer.toString();
    }
}
