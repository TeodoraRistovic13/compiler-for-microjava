package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;

public class CounterVisitor extends VisitorAdaptor {

	protected int count;
	
	public int getCount(){
		return count;
	}
	
	public static class FormParamCounter extends CounterVisitor{
	
		public void visit(FormalParamDecl formParamDecl){
			count++;
		}
	}
	
	public static class VarCounter extends CounterVisitor{
		
		public void visit(VarDecl varDecl){
			count++;
		}
	}
	
	public static class CondFactCounter extends CounterVisitor{
		
		public void visit(RelOpCondFact condFact){
			count++;
			
		}
		
		public void visit(CondFactExpr condFact){
			count++;
			
		}
	}
	
	public static class ComplexArrAssignCounter extends CounterVisitor{
		
		public void visit(AddArrElem condFact){
			count++;
			//System.out.println("Add");
			
		}
		
		public void visit(SkipArrElem condFact){
			count++;
			//System.out.println("Skip");
			
		}
		
		public void visit(NoDesigArrayList condFact){
			//count++;
			//System.out.println("No");
			
		}
		
		
	}
	
	
	
	
}
