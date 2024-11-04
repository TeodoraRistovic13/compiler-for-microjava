package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.Collectors;

import rs.ac.bg.etf.pp1.SemanticAnalyzer.DesigAction;
import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.ac.bg.etf.pp1.CounterVisitor.ComplexArrAssignCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.CondFactCounter;

public class CodeGenerator extends VisitorAdaptor {
	public enum Action{
		ASSIGN, FUNC_CALL, INC, DEC, ADD, SUB, MUL, DIV, MOD
		};
	private int mainPc;
	Action action;
	
	Iterator<Obj> iterator;
	List<Action> actions = new ArrayList<>();
	Stack<Stack<Integer>> addressToPatchStack = new Stack<>();
	Stack<Integer> addressesToPatch = new Stack<>();
	Stack<Integer> addrToPatchDesigArr = new Stack<>();
	Stack<Integer> uncondJumpsToPatch = new Stack<>();
	Stack<Integer> uncondAfterElse = new Stack<>();
	
	//For
	Stack<Integer> stackForLoop = new Stack<>();
	Stack<List<Integer>> breakAddrStack = new Stack<>();
	//int numberOfCondFacts = 0;
	//indeks elem kojem pristupamo
	int arrayElemExpr;
	
	boolean flagHasReturn = false;
	
	int cntDesigArrayElem = 0;
	
	boolean flagArrayElem = false;
	
	public int getMainPc() {
		return mainPc;
	}
	
	public void visit(PrintStmt printStmt) {
		
		Struct expStr = printStmt.getExpr().struct;
		if((expStr.isRefType() && expStr.getElemType() == MyTab.intType) || expStr== MyTab.intType) {
			
			Code.loadConst(5);
			Code.put(Code.print);
		}else {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}
		
		//na kraju ga resetujemo
		//addPrintParam = -1;
		
	}
	
	/*public void visit (PrintVar printVar) {
		
		addPrintParam = printVar.getPrintParam();
	}*/
	
	//iz factor smene
	public void visit(NumFactor numFactor) {
		
		Code.loadConst(numFactor.getNumber());
		
	}
	
	public void visit(CharFactor charFactor) {
		
		Code.loadConst(charFactor.getCharVal());
		
	}
	
	public void visit(BoolFactor boolFactor) {
		
		if(boolFactor.getBoolVal().equals("true")) {
			Code.loadConst(1);
		}else {
			Code.loadConst(0);
		}
		
	}
	
	public void visit(MethodTypeName methodTypeName) {
		
		if("main".equalsIgnoreCase(methodTypeName.getMethName())) {
			mainPc = Code.pc;
		}
		methodTypeName.obj.setAdr(Code.pc);
		int fpCnt = methodTypeName.obj.getLevel();
		int varCnt = methodTypeName.obj.getLocalSymbols().size();
		/*System.out.println("Meth name: "+ methodTypeName.getMethName());
		System.out.println("Fp count: " + fpCnt);
		System.out.println("Var count: " + varCnt);*/
		
		//Generisemo entry
		Code.put(Code.enter);
		Code.put(fpCnt);
		Code.put(varCnt);
		
	}
	
	public void visit(MethodDecl methodDecl) {
		if(methodDecl.getMethodTypeName().getMethodRetType().struct!=MyTab.noType && flagHasReturn==false) {
			Code.put(Code.trap);
			Code.put(1);
			
		}
		
		Code.put(Code.exit);
		Code.put(Code.return_);
		
	}
	
	
	
	public void visit(DesigStmtActions desigStmt) {
		
		switch(action) {
			case ASSIGN:
				//desig ti ne treba na steku ovde
				
				Code.store(desigStmt.getDesignator().obj);
				break;
			case INC:
				Code.load(desigStmt.getDesignator().obj);
				Code.loadConst(1);
				Code.put(Code.add);
				Code.store(desigStmt.getDesignator().obj);
				break;
			case DEC:
				Code.load(desigStmt.getDesignator().obj);
				Code.loadConst(1);
				Code.put(Code.neg);
				Code.put(Code.add);
				Code.store(desigStmt.getDesignator().obj);
				break;
			case FUNC_CALL:
				Obj funcObj = desigStmt.getDesignator().obj;
				int offset = funcObj.getAdr()-Code.pc;
				Obj curr;
				Code.put(Code.call);
				Code.put2(offset);
				//skidamo povratnu vr
				if(funcObj.getType() != Tab.noType){
					Code.put(Code.pop);
				}
				break;
		}
		
		cntDesigArrayElem = 0;//reset
		
	}
	
	public void visit(AssignExpr assign) {
		action = Action.ASSIGN;
	}
	
	public void visit(DesigFuncCall funcCall) {
		action = Action.FUNC_CALL;
	}
	
	public void visit(DesigInc incOp) {
		action = Action.INC;
	}
	
	public void visit(DesigDec decOp) {
		action = Action.DEC;
	}
	
	
	
	
	
	public void visit(NewFactor newFactor) {
		
		Code.put(Code.newarray);
		//niz bajtova
		if(newFactor.getType().struct == Tab.charType) {
			Code.put(0);
		}else {
			Code.put(1);
		}
	}
	

	public void visit(FactorFuncCall funcCall) {
		
		Obj funcObj = funcCall.getDesignator().obj;
		if(!funcCall.getDesignator().obj.getName().equals("ord") &&
				!funcCall.getDesignator().obj.getName().equals("chr")&&
				!funcCall.getDesignator().obj.getName().equals("len")) 
		{
			int offset = funcObj.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
		}
		
	}
	
	public void visit(ReturnExpr retExpr) {
		flagHasReturn = true;
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(ReturnNoExpr retNoExpr) {
		flagHasReturn = true;
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(Add add) {
		actions.add(Action.ADD);
	}
	public void visit(Sub sub) {
		actions.add(Action.SUB);
	}
	
	public void visit(Mul mul) {
		actions.add(Action.MUL);
	}
	
	public void visit(Div div) {
		actions.add(Action.DIV);
	}
	
	public void visit(Mod mod) {
		actions.add(Action.MOD);
	}
	
	public void visit(AddExpr addExpr) {
		Action a = actions.get(actions.size()-1);
		switch(a) {
			case ADD: Code.put(Code.add); break;
			case SUB: Code.put(Code.sub); break;
			//default: System.exit(15);
		}
		actions.remove(actions.size()-1);
			
	}
	
	public void visit(MulTerm mulTerm) {
		Action a = actions.get(actions.size()-1);
		switch(a) {
			case MUL: Code.put(Code.mul); break;
			case DIV: Code.put(Code.div); break;
			case MOD: Code.put(Code.rem); break;
			//default: System.exit(15);
		}
		actions.remove(actions.size()-1);
	}
	
	
	public void visit(ScopeArrayElem desigArr) {
		
		Obj novi = new Obj(Obj.Var, desigArr.obj.getName(), desigArr.obj.getType(), desigArr.obj.getAdr(), desigArr.obj.getLevel());
		Code.load(novi);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		
		//Obj obj = new Obj(Obj.Elem, desigArr.obj.getName(), desigArr.obj.getType());
		//desigArr.obj = obj;
		
	}
	
	public void visit(DesigArrayElem desigArr) {
		
		
		Obj novi = new Obj(Obj.Var, desigArr.obj.getName(), desigArr.obj.getType(), desigArr.obj.getAdr(), desigArr.obj.getLevel());
		
		Code.load(novi);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		
		//Obj obj = new Obj(Obj.Elem, desigArr.obj.getName(), desigArr.obj.getType());
		//desigArr.obj = obj;
	}
	
	public void visit(DesigVar desigVar) {
		
		//Code.load(desigVar.obj);	
	}
	
	public void visit(VarFactor varFactor) {
		//ucitaces sta treba
		
		Code.load(varFactor.getDesignator().obj);
		
	}
	
	public void visit(ReadExpr readExpr) {
		
		if(readExpr.getDesignator().obj.getType() == MyTab.charType || 
			(readExpr.getDesignator().obj.getType().isRefType() && 
					readExpr.getDesignator().obj.getType().getElemType() == MyTab.charType)) 
		{
			Code.put(Code.bread);
		}else {
			Code.put(Code.read);
		}
		
		Code.store(readExpr.getDesignator().obj);
		
	}
	
	public void visit(NegTermExpr negTerm) {
		
		Code.put(Code.neg);
		
	}
	
	int addrForLoopCond;
	int addrForLoopActions;
	int jmpAfterForLoop;
	int jmpToForLoopBody;
	
	public void visit(RelOpCondFact relopCondFact) {
		
		
		
		if(relopCondFact.getRelOp() instanceof EqlOp) {
			Code.putFalseJump(Code.eq, 0);
			
		}else if(relopCondFact.getRelOp() instanceof NotEqlOp) {
			Code.putFalseJump(Code.ne, 0);
			
		}else if(relopCondFact.getRelOp() instanceof GrtOp) {
			Code.putFalseJump(Code.gt, 0);
		}
		else if(relopCondFact.getRelOp() instanceof GreOp) {
			Code.putFalseJump(Code.ge, 0);
		}
		else if(relopCondFact.getRelOp().getClass() == LssOp.class) {
			Code.putFalseJump(Code.lt, 0);
		}
		else {
			Code.putFalseJump(Code.le, 0);
		}
		
		if(relopCondFact.getParent() instanceof ForCondFactExpr) {
			jmpAfterForLoop = Code.pc - 2;
			Code.putJump(0); // skok na telo fora
			jmpToForLoopBody = Code.pc - 2; // pamtim adrese koje fixupujem
			addrForLoopActions = Code.pc;
			
			stackForLoop.push(jmpAfterForLoop);
			stackForLoop.push(addrForLoopActions);
			
		}else {
			
			addressesToPatch.push(Code.pc - 2);
		}
		
	}
	
	public void visit(ForDesigStmts forInitStmt) {
		
		if(forInitStmt.getParent() instanceof ForLoop) {
			addrForLoopCond = Code.pc;
		}
			
		
	}
	
	public void visit(NoForDesigStmt noForDesigStmt) {
		
		//to je init fora/
		if(noForDesigStmt.getParent() instanceof ForLoop) {
			addrForLoopCond = Code.pc;
		}
		
		//to jr akcija posle svake iteracije
		if(noForDesigStmt.getParent() instanceof ForLoopStmts) {
			Code.putJump(addrForLoopCond);
			Code.fixup(jmpToForLoopBody);//namesta skok na for loop telo
			
			List<Integer> breakAddrs = new ArrayList<>();
			breakAddrStack.push(breakAddrs);
		}
			
		
	}
	
	public void visit(NoForCondFact noForCondFact) {
		
		Code.putJump(0); // skok na telo fora
		jmpToForLoopBody = Code.pc - 2; // pamtim adrese koje fixupujem
		addrForLoopActions = Code.pc;
		
		stackForLoop.push(-1);
		stackForLoop.push(addrForLoopActions);
		
	}
	
	//mali condfact
	public void visit(CondFactExpr condFactExpr) {
		Code.loadConst(1);
		Code.putFalseJump(Code.eq, 0); // skok na kraj fora
		if(condFactExpr.getParent() instanceof ForCondFactExpr) {
			jmpAfterForLoop = Code.pc - 2;
			Code.putJump(0); // skok na telo fora
			jmpToForLoopBody = Code.pc - 2; // pamtim adrese koje fixupujem
			addrForLoopActions = Code.pc;
			
			stackForLoop.push(jmpAfterForLoop);
			stackForLoop.push(addrForLoopActions);
			
		}else {
			addressesToPatch.push(Code.pc - 2);
		}
		
	}
	
	public void visit(ForLoopStmts forLoopStmts) {
		
		Code.putJump(addrForLoopCond);
		Code.fixup(jmpToForLoopBody);//namesta skok na for loop telo
		
		List<Integer> breakAddrs = new ArrayList<>();
		breakAddrStack.push(breakAddrs);
	}
	
	
	
	public void visit(ForLoopBody forLoopBody) {
		
		addrForLoopActions = stackForLoop.pop(); 
		jmpAfterForLoop = stackForLoop.pop();
		
		Code.putJump(addrForLoopActions);
		if(jmpAfterForLoop != -1) {
			Code.fixup(jmpAfterForLoop);
		}
		List<Integer> list = breakAddrStack.pop();
		for(int i = 0; i < list.size();  i++) {
			
			int addr = list.get(i);
			
			Code.fixup(addr);
			
		}
		
		list.clear();
		
	}
	
	public void visit(ContinueStmt continueStmt) {
		
		addrForLoopActions = stackForLoop.peek();
		Code.putJump(addrForLoopActions);
		
	}
	
	
	public void visit(BreakStmt breakStmt) {
			Code.putJump(0);
			//dopuna tekuce liste jumpova izazvanih breakom
			List<Integer> list = breakAddrStack.pop();
			list.add(Code.pc - 2);
			breakAddrStack.push(list);
	}
	
	
	public void visit(IfStatement ifStatement) {
		
		addressesToPatch = addressToPatchStack.pop();
		
		int addr;
		if(ifStatement.getParent() instanceof IfElseStmt) {
			Code.putJump(0); // za posle else
		}
		
		//da skoce na else
		while(!addressesToPatch.isEmpty()) {
				
			addr = addressesToPatch.pop();
			Code.fixup(addr);
		}
		
		if(ifStatement.getParent() instanceof IfElseStmt) {
			uncondAfterElse.push(Code.pc-2);
		}
		
	}
	
	public void visit(ElseStatement elseStmt) {
		
		int addr = uncondAfterElse.pop();
		Code.fixup(addr);
	}
	
	//mali condition
	public void visit(ConditionCondTerm condition) {
		
		//SyntaxNode node = condition.getParent();
		CondFactCounter cfCnt = new CondFactCounter();
		condition.traverseTopDown(cfCnt);
		
		if(condition.getParent() instanceof OrCondition) {
			int addr;
			Code.putJump(0);
			for(int i= 0; i < cfCnt.getCount(); i++) {
				addr = addressesToPatch.pop();
				Code.fixup(addr);
			}
			uncondJumpsToPatch.push(Code.pc-2);
		}
		
		//bezuslovni skok na then granu
		if(condition.getParent() instanceof IfStmt || condition.getParent() instanceof IfElseStmt) {
			int addr;
			while(!uncondJumpsToPatch.isEmpty()) {
				addr = uncondJumpsToPatch.pop();
				Code.fixup(addr);
			}
			
			Stack<Integer> copiedStack = new Stack<>();
	        copiedStack.addAll(addressesToPatch);
			addressToPatchStack.push(copiedStack);
			addressesToPatch.clear();
			
		}
	}
	
	//veliki condition
	public void visit(OrCondition orCond) {
		
		if(orCond.getParent() instanceof OrCondition) {
			SyntaxNode root = orCond.getCondTerm();
			CondFactCounter cfCnt = new CondFactCounter();
			root.traverseTopDown(cfCnt);
			Code.putJump(0);
			int addr;
			for(int i= 0; i < cfCnt.getCount(); i++) {
				addr = addressesToPatch.pop();
				Code.fixup(addr);
			}
			uncondJumpsToPatch.push(Code.pc - 2);
		}
		
		else if(orCond.getParent() instanceof IfStmt || orCond.getParent() instanceof IfElseStmt) {
			int addr;
			while(!uncondJumpsToPatch.isEmpty()) {
				addr = uncondJumpsToPatch.pop();
				Code.fixup(addr);
			}
			
			Stack<Integer> copiedStack = new Stack<>();
	        copiedStack.addAll(addressesToPatch);
			addressToPatchStack.push(copiedStack);
			addressesToPatch.clear();
			
		}
		
	}
	
	
	//print za novi red
	public void visit(PrintStmtEol printEol) {
		
		// 13 je kod za CR (\r)
		Code.loadConst(13); 
		Code.loadConst(1);
		Code.put(Code.bprint);
		
		//10 je kod za LR (\n)
		Code.loadConst(10);
		Code.loadConst(1);
		Code.put(Code.bprint);
		
		
		
	}
	
	
	 Stack<HelperNode> objStack = new Stack<>();
	
	//komplikovana dodela nizova
	public void visit(AddArrElem addArrElem) {
		
		
		ComplexArrAssignCounter elemCnt = new ComplexArrAssignCounter(); 
		addArrElem.traverseTopDown(elemCnt);
		System.out.println("Indeks koji se koristi je:"+ (elemCnt.getCount()-1));
		
		HelperNode node = new HelperNode();
		node.index = elemCnt.getCount() - 1;
		node.obj = addArrElem.getDesignator().obj;
		objStack.push(node);
		
	}
	
	public void visit(SkipArrElem skipArrElem) {
				
	}
	
	public void visit(DesigStmtArrayAssign desigArrAssign) {
		
		System.out.println(objStack.size());
		
		//sad treba da za sve elem niza probam da dodelim vr
		int higestIndex = objStack.peek().index;
		int addrAfterTrap;
		int jmpToTrapException;
		Obj arrSrc = desigArrAssign.getDesignator().obj;
		Obj arrDst = desigArrAssign.getArrayAfterStar().obj;
		
		Code.load(arrDst);
		Code.loadConst(1);
		Code.loadConst(higestIndex + 1);
		Code.put(Code.add);
		
		Code.load(arrSrc);
		Code.put(Code.arraylength);
		
		//ako je dest veci od src
		Code.putFalseJump(Code.le, 0);
		jmpToTrapException = Code.pc - 2;
		Obj objArrElemSrc = new Obj(Obj.Elem, arrSrc.getName(), arrSrc.getType(), arrSrc.getAdr(), arrSrc.getLevel());
		Obj objDstElemSrc = new Obj(Obj.Elem, arrDst.getName(), arrDst.getType(), arrDst.getAdr(), arrDst.getLevel());
		while(!objStack.isEmpty()) {
			
			HelperNode node = objStack.pop();
			Code.load(arrSrc);
			Code.loadConst(node.index);
			Code.load(objArrElemSrc);
			
			Code.store(node.obj);
			
		}
		
		//treba da punim onaj niz
		//Code.load(arrDst);
		//ide dok god ima elem
		//pocinjemo od nule
		//offset mora da se racuna takodje..
		Code.loadConst(0);
		Code.put(Code.dup2);
		int addrLoop = Code.pc;
		Code.load(arrSrc);
		Code.put(Code.dup2);
		Code.put(Code.pop);
		Code.loadConst(higestIndex + 1);
		Code.put(Code.add);
		Code.load(objArrElemSrc);
		Code.store(objDstElemSrc);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.dup);
		Code.load(arrSrc);
		Code.put(Code.arraylength);
		//treba da se doda adresa izlazka iz dodle
		Code.putFalseJump(Code.lt, 0);
		int addr1 = Code.pc - 2;
		Code.put(Code.dup);
		Code.load(arrDst);
		Code.put(Code.arraylength);
		//treba da se doda adresa izlazka iz dodle
		Code.putFalseJump(Code.lt, 0);
		int addr2 = Code.pc - 2;
		Code.put(Code.dup2);
		Code.putJump(addrLoop);
		
		//dodavanje runtime-a
		Code.fixup(jmpToTrapException);
		Code.put(Code.trap);
		Code.put(2);
		
		Code.fixup(addr1);
		Code.fixup(addr2);

		
		
	}
	
	
}




