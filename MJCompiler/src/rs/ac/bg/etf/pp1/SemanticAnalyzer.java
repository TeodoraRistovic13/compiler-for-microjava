package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;




public class SemanticAnalyzer extends VisitorAdaptor {
	
	public enum DesigAction{ASSIGN, FUNC_CALL, INC, DEC };
	public enum RelOpValue{EQL, NEQ, GRT, GRE, LSS, LSE };
	
	public int nVars;
	
	
	int printCallCount = 0;
	int funcCallCount = 0;
	int varDeclCount = 0;
	int methodsCount = 0;
	int glbVarDeclCount = 0;
	int glbConstDeclCount = 0;
	int glbArrayCount = 0;
	
	int stmtInMainCount = 0;
	
	
	
	String currNamespace;
	Obj currentMethod = null;
	Obj calledFuncNode;
	
	
	MyDumpSymbolTable myDumpSymbTable = new MyDumpSymbolTable();
	
	boolean errorDetected = false;
	List<Boolean> flagActiveForLoop = new ArrayList<>();
	List<Struct> desigStmtArrayTypes = new ArrayList<>();
	
	
	DesigAction currDesigAction;
	RelOpValue currRelopValue;
	
	Obj currTypeNode = null;
	int currConst;
	int formalParamCounter = 0;
	Iterator<Obj> currFormIterator = new ArrayList<Obj>().iterator();
	Stack<Iterator<Obj>> formParamIterStack = new Stack<>();
	
	Logger log = Logger.getLogger(getClass());

	
	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	
	public void visit(ProgName progName) {
		progName.obj = MyTab.insert(Obj.Prog, progName.getProgName(), MyTab.noType);
		MyTab.openScope();
	}
	
	public void visit (NamespaceName nmspcName) {
		nmspcName.obj = Tab.insert(Obj.NO_VALUE, nmspcName.getNamespaceName(), MyTab.noType);
		currNamespace = nmspcName.getNamespaceName();
	}
	
	public void visit(Program program) {
		
		nVars = Tab.currentScope.getnVars();
		Obj mainNode = MyTab.find("main");
		if(!(mainNode.getKind() == Obj.Meth && mainNode.getType() == MyTab.noType && mainNode.getLevel() == 0)) {
			report_error("Greska : U programu ne postoji metoda main void tipa bez argumenata!", null);
		}
		MyTab.chainLocalSymbols(program.getProgName().obj);
		MyTab.closeScope();
	}
	
	public void visit(Namespace nmspc) {
		currNamespace = null;
	}
	
	
	
	public void visit(Type type){
    	Obj typeNode = Tab.find(type.getTypeName());
    	if(typeNode == Tab.noObj){
    		report_error("Greska na liniji " + type.getLine()+ ": nije pronadjen tip '"+ type.getTypeName()+"' u tabeli simbola!", null);
    		type.struct = Tab.noType;
    	}else{
    		if(Obj.Type == typeNode.getKind()){
    			type.struct = typeNode.getType();
    			
    		}else{
    			report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip!", type);
    			type.struct = Tab.noType;
    		}
    	}
    	currTypeNode = typeNode;
    }
	
	public void visit(VarNameDecl varDecl) {
		
		
		Obj varNode = MyTab.find(varDecl.getVarName());
		String varName;
		
		if(currNamespace != null) {
			varName =currNamespace+"::"+ varDecl.getVarName(); 
		}else {
			varName = varDecl.getVarName();
		}
		
		if(varNode == MyTab.noObj){
			report_info("Deklarisana promenljiva "+ varName, varDecl);
		}else {
			report_error("Greska na liniji " + varDecl.getLine()+ ": vec je deklarisano ime '"+ varDecl.getVarName()+"' u tabeli simbola!", null);
		}
		
		varNode = MyTab.insert(Obj.Var, varName, currTypeNode.getType());
		
	}
	
	
	
	
	public void visit(ConstDeclaration constDecl) {
		
		Obj constNode = MyTab.find(constDecl.getConstName());
		String constName;
		
		if(currNamespace != null) {
			constName =currNamespace+"::"+ constDecl.getConstName(); 
		}else {
			constName = constDecl.getConstName();
		}
		
		if(constNode == MyTab.noObj){
			
			if(!(constDecl.getConstant().struct.equals(currTypeNode.getType()))) {
				report_error("Greska na liniji " + constDecl.getLine()+ ": tip i vrednost konstante se razlikuju!", null);
			}else {
				report_info("Deklarisana konstanta "+ constName, constDecl);
			}
			constNode = MyTab.insert(Obj.Con, constName, currTypeNode.getType());
			constNode.setAdr(currConst);
			
		}else {
			report_error("Greska na liniji " + constDecl.getLine()+ ": vec je deklarisano ime '"+ constDecl.getConstName()+"' u tabeli simbola!", null);
		}
		
		
		
	}
	
	
	public void visit(MethodTypeName methodTypeName){
		
		
		String methName;
		if(currNamespace != null) {
			methName =currNamespace+"::"+ methodTypeName.getMethName(); 
		}else {
			methName = methodTypeName.getMethName();
		}
		Obj methNode = MyTab.find(methodTypeName.getMethName());
		if(methNode == MyTab.noObj){
			if(methodTypeName.getMethodRetType().struct==MyTab.noType) {
				currentMethod = Tab.insert(Obj.Meth, methName, MyTab.noType);
			}else {
				currentMethod = Tab.insert(Obj.Meth, methName, methodTypeName.getMethodRetType().struct);
			}
			methodTypeName.obj = currentMethod;
	    	Tab.openScope();
			report_info("Obradjuje se funkcija " + methodTypeName.getMethName(), methodTypeName);
			
		}else {
			report_error("Greska na liniji " + methodTypeName.getLine()+ ": vec je deklarisano ime '"+ methodTypeName.getMethName()+"' u tabeli simbola!", null);
		}
		
    }
	
	public void visit(MethodDecl methodDecl){
		
		currentMethod.setLevel(formalParamCounter);
		//resetyujemo brojac
		formalParamCounter = 0;
		Tab.chainLocalSymbols(currentMethod);
    	Tab.closeScope();
    	//mislim da nije potrebno ali za svaki sl da ne zaostaje
    	//da budemo tacni
    	currentMethod = null;
    	
    }
	
	public void visit(NumConst number) {
		currConst = number.getNumber();
		number.struct = MyTab.intType;
	
	}
	
	public void visit(BoolConst boolConst) {	
		
		if(boolConst.getBoolVal().equals("true")) {
			currConst = 1;
		}else {
			currConst = 0;
		}
		boolConst.struct = MyTab.boolType;
	}
	
	public void visit(CharConst charConst) {
		
		currConst = (int) charConst.getCharVal();
		
		charConst.struct = MyTab.charType;
		
	}
	
	public void visit(MethRetType retType) {
		retType.struct = retType.getType().struct;
	}
	
	public void visit(MethRetVoid retType) {
		retType.struct= MyTab.noType;
	}
	
	public void visit(VarArrayNameDecl varArray) {
		
		Obj varNode = MyTab.find(varArray.getVarName());
		String varName;
		
		if(currNamespace != null) {
			varName =currNamespace+"::"+ varArray.getVarName(); 
		}else {
			varName = varArray.getVarName();
		}
		
		if(varNode == MyTab.noObj){
			report_info("Deklarisana promenljiva "+ varName, varArray);
		}else {
			report_error("Greska na liniji " + varArray.getLine()+ ": vec je deklarisano ime '"+ varArray.getVarName()+"' u tabeli simbola!", null);
		}
		
		Struct s = new Struct(Struct.Array, currTypeNode.getType());
		
		Tab.insert(Obj.Var, varName, s);
		
	}
	
	
	//Designator
	//(ScopeArrayElem) IDENT:namespaceName SCOPE IDENT:desigArrayName LBRACKET Expr RBRACKET
	public void visit(ScopeArrayElem arrayElem) {
		
		Obj nameNode = MyTab.find(arrayElem.getNamespaceName());
		if(nameNode == MyTab.noObj) {
			report_error("Greska na liniji " + arrayElem.getLine()+ " : ime "+ arrayElem.getNamespaceName() +" nije deklarisano! ", null);
		}
		if(nameNode.getType() != MyTab.noType) {
			report_error("Greska na liniji " + arrayElem.getLine()+ " : ime "+ arrayElem.getNamespaceName() +" nije namespace! ", null);
		}
			
		String varName = arrayElem.getNamespaceName()+"::"+arrayElem.getDesigArrayName();
		
		Obj varNode = MyTab.find(varName);
		if(varNode == MyTab.noObj) {
			report_error("Greska na liniji " + arrayElem.getLine()+ " : ime "+ varName +" nije deklarisano! ", null);
		}else {
			if(varNode.getType().getKind()!=Struct.Array) {
				report_error("Greska na liniji " + arrayElem.getLine()+ " : ime "+arrayElem.getDesigArrayName()+" nije niz! ", null);
			}
			if(arrayElem.getExpr().struct.getKind() != Struct.Int) {
				report_error("Greska na liniji " + arrayElem.getLine()+ " : indeks mora da bude broj! ", null);
			}
			if(varNode.getType().getKind()==Struct.Array && arrayElem.getExpr().struct.getKind() == Struct.Int) {
				printInfo(varNode, arrayElem);
			}
			
		}
		
		Obj obj = new Obj(Obj.Elem, varNode.getName(), varNode.getType().getElemType(), varNode.getAdr(), varNode.getLevel());
		arrayElem.obj = obj;
				
	}
	
	//(ScopeDesigVar) IDENT:namespaceName SCOPE IDENT:desigVarName
	public void visit(ScopeDesigVar scopeVar) {
		Obj nameNode = MyTab.find(scopeVar.getNamespaceName());
		if(nameNode == MyTab.noObj) {
			report_error("Greska na liniji " + scopeVar.getLine()+ " : ime "+ scopeVar.getNamespaceName() +" nije deklarisano! ", null);
		}
		if(nameNode.getType() != MyTab.noType) {
			report_error("Greska na liniji " + scopeVar.getLine()+ " : ime "+ scopeVar.getNamespaceName() +" nije namespace! ", null);
		}
		
		String varName = scopeVar.getNamespaceName()+"::"+scopeVar.getDesigVarName();
		Obj varNode = MyTab.find(varName);
		if(varNode == MyTab.noObj) {
			report_error("Greska na liniji " + scopeVar.getLine()+ " : ime "+ varName +" nije deklarisano! ", null);
		}else {
			//dodaljujemo iterator ako je metod..zatrebace posle
			if(varNode.getKind()==Obj.Meth) {
				int n = varNode.getLevel();
				formParamIterStack.push( varNode.getLocalSymbols().stream().limit(n).collect(Collectors.toList()).iterator());
			}
			//printInfo(varNode, scopeVar);
			
		}
		scopeVar.obj = varNode;
		
			
	}
	
	//(DesigVar) IDENT:desigVarName
	public void visit(DesigVar desigVar) {
		
		Obj varNode = MyTab.find(desigVar.getDesigVarName());
		
		if(varNode == MyTab.noObj) {
			
			if(currNamespace != null) {
				varNode = MyTab.find(currNamespace+"::"+ desigVar.getDesigVarName());
			}else {
				report_error("Greska na liniji " + desigVar.getLine()+ " : ime "+desigVar.getDesigVarName()+" nije deklarisano! ", null);
			}
		}
		
		if(varNode != MyTab.noObj) {
				//dodaljujemo iterator ako je metod..zatrebace posle
			if(varNode.getKind()==Obj.Meth) {
				int n = varNode.getLevel();
				formParamIterStack.push(varNode.getLocalSymbols().stream().limit(n).collect(Collectors.toList()).iterator());
			}
			
		}else {
			report_error("Greska na liniji " + desigVar.getLine()+ " : ime "+desigVar.getDesigVarName()+" nije deklarisano! ", null);
		}
		
		desigVar.obj = varNode;
		
		
	}
	
	//(DesigArray) IDENT:desigArrayName LBRACKET Expr:expr RBRACKET
	public void visit(DesigArrayElem arrayElem) {
		
		Obj varNode = MyTab.find(arrayElem.getDesigArrayName());
		if(varNode == MyTab.noObj) {
			
			if(currNamespace != null) {
				varNode = MyTab.find(currNamespace+"::"+ arrayElem.getDesigArrayName());
			}else {
				report_error("Greska na liniji " + arrayElem.getLine()+ " : ime "+arrayElem.getDesigArrayName()+" nije deklarisano! ", null);
			}
			
		}
		
		if(varNode != MyTab.noObj) {
			
			if(!varNode.getType().isRefType()) {
				report_error("Greska na liniji " + arrayElem.getLine()+ " : ime "+arrayElem.getDesigArrayName()+" nije niz! ", null);
			}
			if(arrayElem.getExpr().struct != MyTab.intType) {
				report_error("Greska na liniji " + arrayElem.getLine()+ " : indeks mora da bude broj! ", null);
			}
			
			if(varNode.getType().getKind()==Struct.Array && arrayElem.getExpr().struct.getKind() == Struct.Int) {
				printInfo(varNode, arrayElem);
			}
			
		}else {
			report_error("Greska na liniji " + arrayElem.getLine()+ " : ime "+arrayElem.getDesigArrayName()+" nije deklarisano! ", null);
		}
		
		Obj obj = new Obj(Obj.Elem, varNode.getName(), varNode.getType().getElemType(), varNode.getAdr(), varNode.getLevel());
		//arrayElem.obj = varNode;
		arrayElem.obj = obj;
		
		
	}
	
	//DesignatorActions
	
	//(AssignExpr) EQUAL Expr:e
	public void visit(AssignExpr assignExpr) {
		currDesigAction=DesigAction.ASSIGN;
		assignExpr.struct=assignExpr.getExpr().struct;
	}
	
	//(DesigInc) INC
	public void visit(DesigInc designInc) {
		currDesigAction=DesigAction.INC;
	}
	
	//(DesigDec) DEC
	public void visit(DesigDec designDec) {
		currDesigAction=DesigAction.DEC;
	}
	
	//(DesigFuncCall) LPAREN ActualPars RPAREN
	public void visit(DesigFuncCall designFuncCall) {
		currDesigAction=DesigAction.FUNC_CALL;
	}
	
	//DesignatorStatement
	
	//(DesigStmtActions) Designator:d DesignatorActions
	public void visit(DesigStmtActions desigStmtAction) {
		
		Obj designator = desigStmtAction.getDesignator().obj; 
		switch(currDesigAction) {
		case ASSIGN:
			if(designator != MyTab.noObj) {
				if(designator.getKind()!= Obj.Var && designator.getKind() != Obj.Elem) {
					report_error("Greska na liniji " + desigStmtAction.getLine()+ " : ime "+ designator.getName()+" nije promenljiva ni element niza! ", null);
				}else {
					
					if(!(desigStmtAction.getDesignatorActions().struct.assignableTo(designator.getType()))) {
						report_error("Greska na liniji " + desigStmtAction.getLine()+ ": src i dst nisu kompatibilni pri dodeli! ", null);
					}
					
				}
			}
			break;
		case INC: 
		case DEC:
			if(designator != MyTab.noObj) {
				if(designator.getKind() != Obj.Var && designator.getKind() != Obj.Elem) {
					report_error("Greska na liniji " + desigStmtAction.getLine()+ " : ime "+ designator.getName()+" nije promenljiva ni element niza! ", null);
				}else {
					
					if(designator.getType() != MyTab.intType) {
						report_error("Greska na liniji " + desigStmtAction.getLine()+ " : ime "+ designator.getName()+" nije int tipa! ", null);
					}
					
					
				}
			}
			
			break;
		case FUNC_CALL:
			if(designator.getKind() != Obj.Meth) {
				report_error("Greska na liniji " + desigStmtAction.getLine()+ " : ime "+ designator.getName()+" nije globalna fja gl programa!", null);
			}else {
				calledFuncNode = designator;
			}
			break;
			
		}
		
		desigStmtAction.struct = desigStmtAction.getDesignator().obj.getType();
		
	}
	
	public void visit(ArrAfterStar arrAfterStar) {
		
		if(!arrAfterStar.getDesignator().obj.getType().isRefType()) {
			report_error("Greska na liniji " + arrAfterStar.getLine()+ " : sa desne strane znaka jednakosti mora da bude niz!", null);
		}
		arrAfterStar.obj = arrAfterStar.getDesignator().obj;
		
		
	}
	
	//(DesigStmtArray) LBRACKET DesignatorArrayList MUL Designator RBRACKET EQUAL Designator
	public void visit(DesigStmtArrayAssign desigStmtArr) {
	
		for(int i = 0; i < desigStmtArrayTypes.size();i++) {
			
			if(!desigStmtArr.getArrayAfterStar().obj.getType().getElemType().assignableTo(desigStmtArrayTypes.get(i))) {
				report_error("Greska na liniji " + desigStmtArr.getParent().getLine()+ " : oznaka sa desne strane jednakosti nije kompatibilna pri dodeli sa " + (i+1) + ". oznakom sa leve strane jednakosti!", null);
			}
				
		}
		
		//drugi uslov
		if(!desigStmtArr.getDesignator().obj.getType().isRefType()) {
			report_error("Greska na liniji " + desigStmtArr.getParent().getLine()+ " : oznaka sa leve strane jednakosti posle znaka * mora da bude niz!", null);
		}else if(!desigStmtArr.getDesignator().obj.getType().equals(desigStmtArr.getArrayAfterStar().obj.getType())) {
			report_error("Greska na liniji " + desigStmtArr.getParent().getLine()+ " : oznaka posle '*' nije niz isto tipa kao i niz sa desne str jednkosti!", null);
		}
		
		desigStmtArrayTypes.clear();
		
	}
	
	
	//DesignatorArrayList
	//(AddArrElem) DesignatorArrayList Designator:d COMMA
	public void visit(AddArrElem addArrElem) {
		desigStmtArrayTypes.add(addArrElem.getDesignator().obj.getType());
		Obj designator = addArrElem.getDesignator().obj;
		
		if(designator.getKind()!= Obj.Var && designator.getKind()!= Obj.Elem) {
			report_error("Greska na liniji " + addArrElem.getLine()+ " : oznaka sa leve strane jednakosti pre * mora da bude promenljiva ili elem niza", null);
		}
	}
	
	//(SkipArrElem) DesignatorArrayList COMMA
	public void visit(SkipArrElem skipArrElem) {
		
	}
	
	
	
	
	//Statements
	//If uslovi
	//(IfStmt) IF LPAREN Condition RPAREN Statement
	public void visit(IfStmt ifStmt) {
		
		if(ifStmt.getCondition().struct != MyTab.boolType) {
			report_error("Greska na liniji " + ifStmt.getLine()+ ": uslov mora da bude bool tipa!", null);
		}
	}
	
	//(IfElseStmt) IF LPAREN Condition RPAREN Statement ELSE Statement
	public void visit(IfElseStmt ifElseStmt) {
		if(ifElseStmt.getCondition().struct != MyTab.boolType) {
			report_error("Greska na liniji " + ifElseStmt.getLine()+ ": uslov mora da bude bool tipa!", null);
		}
	}
	
	
	//RETURN Expr:t SEMI
	public void visit(ReturnExpr retExpr) {
		
		retExpr.struct = retExpr.getExpr().struct;
		if(currentMethod == null) {
			report_error("Greska na liniji " + retExpr.getLine()+ ": return iskaz se koristi samo u telu fje!", null);
		}
		else if(currentMethod.getType() != MyTab.noType) {
			if(!(retExpr.struct.assignableTo(currentMethod.getType()))){
				report_error("Greska na liniji " + retExpr.getLine()+ ": izraz koji se vraca ne odgovara povratnom tipu metode "+ currentMethod.getName()+"!", null);
			}
		}else {
			report_error("Greska na liniji " + retExpr.getLine()+ " : metoda "+  currentMethod.getName() + " je tipa void, a return vraca povratnu vrednost! ", null);
		}
		
		
	}
	
	//RETURN SEMI
	public void visit(ReturnNoExpr retNoExpr) {
		if(currentMethod.getType() != MyTab.noType) {
			report_error("Greska na liniji " + retNoExpr.getLine()+ " : metoda "+  currentMethod.getName() + " nije void tipa, a return ne vraca povratnu vrednost! ", null);
		}
	}

	//READ LPAREN Designator:d RPAREN SEMI
	public void visit(ReadExpr readExpr) {
		
		Obj desigObj = readExpr.getDesignator().obj;
		if(desigObj != MyTab.noObj) {	
			
			if(desigObj.getKind() != Obj.Var && desigObj.getKind() != Obj.Elem) {
				report_error("Greska na liniji " + readExpr.getLine()+ " : ime "+ desigObj.getName()+" nije promenljiva ni niz!", null);	
			}
			
			if(desigObj.getType() != MyTab.intType && desigObj.getType() != MyTab.charType && desigObj.getType() != MyTab.boolType) {
				report_error("Greska na liniji " + readExpr.getLine()+ " : ime "+ desigObj.getName()+" nije int, char ili bool tipa!", null);
			}	
		}
	}
	
	//PRINT LPAREN Expr:expr PrintAdditionalParam RPAREN SEMI
	//proveri da li print prima niz !
	public void visit(PrintStmt printStmt) {
		
		Struct printStruct = printStmt.getExpr().struct;
		if(printStruct != MyTab.intType && printStruct != MyTab.charType && printStruct != MyTab.boolType) {
			report_error("Greska na liniji " + printStmt.getLine()+ ": podatak koji se stampa mora da bude int, char ili bool tipa!", null);
		}
		
	}
	
	//(ForLoop) FOR LPAREN ForDesignatorStmts SEMI ForCondFact SEMI ForDesignatorStmts RPAREN Statement
	public void visit(ForLoop forLoop) {
		
		if(!(flagActiveForLoop.isEmpty())) {
			int last_index = flagActiveForLoop.size()-1;
			flagActiveForLoop.remove(last_index);
		}
		
	}
	
	//(BreakStmt) BREAK SEMI
	public void visit(BreakStmt breakStmt) {
		
		report_info("Pronadjen break na liniji: "+ breakStmt.getLine() , breakStmt);
		if(flagActiveForLoop.isEmpty()) {
			report_error("Greska na liniji " + breakStmt.getLine() + ": iskaz break se može koristiti samo unutar for petlje!", null);
		}
		
	}
	
	//(ContinueStmt) CONTINUE SEMI
	public void visit(ContinueStmt continueStmt) {
		
		report_info("Pronadjen continue na liniji: "+ continueStmt.getLine() , continueStmt);
		if(flagActiveForLoop.isEmpty()) {
			report_error("Greska na liniji " + continueStmt.getLine()+ ": iskaz continue se može koristiti samo unutar for petlje!", null);
		}		
	}
		
	//(ForCondFactExpr) CondFact
	public void visit(ForCondFactExpr forCondExpr) {
		report_info("Obradjuje se for petlja na liniji: "+ forCondExpr.getLine(), forCondExpr);
		flagActiveForLoop.add(true);
	}
	
	
	//(NoForCondFact) /* epsilon */
	public void visit(NoForCondFact noForCondExpr) {
		report_info("Obradjuje se for petlja na liniji: "+ noForCondExpr.getParent().getLine(), noForCondExpr);
		flagActiveForLoop.add(true);
	}
	
	
	
	
	//Factor
	//(VarFactor) Designator:d
	public void visit(VarFactor varFactor) {
		
		varFactor.struct = varFactor.getDesignator().obj.getType();
		
	}
	//(NumFactor) NUMBER:number
	public void visit(NumFactor numFactor) {
		
		numFactor.struct= MyTab.intType;
		
	}
	//(CharFactor) CHAR_CONST:charVal
	public void visit(CharFactor charFactor) {
		charFactor.struct= MyTab.charType;
	}
	//(BoolFactor) BOOL_CONST:boolVal
	public void visit(BoolFactor boolFactor) {
		boolFactor.struct= MyTab.boolType;
	}
	
	//(FactorFuncCall) Designator:d LPAREN ActualPars RPAREN
	public void visit(FactorFuncCall factorFuncCall) {
		
		factorFuncCall.struct= factorFuncCall.getDesignator().obj.getType();
		if(factorFuncCall.getDesignator().obj.getKind() != Obj.Meth) {
			report_error("Greska na liniji " + factorFuncCall.getLine()+ "oznaka nije deklarisana kao funkcija!", null);
		}
		
		calledFuncNode = factorFuncCall.getDesignator().obj;
		//mora da ima povratnu vr jer se koristi u izrazima
		if(factorFuncCall.getDesignator().obj.getType() == Tab.noType) {
			report_error("Greska na liniji " + factorFuncCall.getLine()+ ": fja '"+ factorFuncCall.getDesignator().obj.getName()+"' ne moze da se koristi u izrazima jer nema povratnu vrednost!", null);
		}
		
	}
	
	//(NewFactor) NEW Type:newType LBRACKET Expr:expr RBRACKET
	public void visit(NewFactor newFactor) {
		Struct s = new Struct(Struct.Array, newFactor.getType().struct);
		newFactor.struct = s;
		if(newFactor.getExpr().struct != MyTab.intType) {
			report_error("Greska na liniji " + newFactor.getLine()+ ": broj elementa niza mora da bude tipa int!", null);
		}
		
	}
	
	public void visit(FactorExpr factorExpr) {
		
		factorExpr.struct = factorExpr.getExpr().struct;
	}
	
	
	//Term
	public void visit(FactorTerm factorTerm) {
		factorTerm.struct = factorTerm.getFactor().struct;
	}
	
	public void visit(MulTerm mulTerm) {
		
		mulTerm.struct = mulTerm.getTerm().struct;
		if(mulTerm.getFactor().struct != MyTab.intType || mulTerm.getTerm().struct != MyTab.intType) {
			report_error("Greska na liniji " + mulTerm.getLine()+ ": operandi nisu tipa int!", null);
		}
		
	}
	
	//Expr
	//(PosTermExpr) Term:term
	public void visit(PosTermExpr posExpr) {
		posExpr.struct=posExpr.getTerm().struct;
		
	}
	//(NegTermExpr) MINUS Term:term
	public void visit(NegTermExpr negExpr) {
		negExpr.struct=negExpr.getTerm().struct;
		if(negExpr.getTerm().struct != MyTab.intType) {
			report_error("Greska na liniji " + negExpr.getLine()+ ": operand koji se negira mora da bude int!", null);
		}
		
	}
	
	//(AddExpr) Expr:expr AddOp Term:term
	public void visit(AddExpr addExpr) {
		
		
		addExpr.struct=addExpr.getExpr().struct;//moze da se uzme struct i od term-a
		if(addExpr.getTerm().struct != MyTab.intType || addExpr.getExpr().struct != MyTab.intType) {
			report_error("Greska na liniji " + addExpr.getLine()+ ": operandi nisu tipa int!", null);
		}
		
		if(!addExpr.getExpr().struct.compatibleWith(addExpr.getTerm().struct)) {
			report_error("Greska na liniji " + addExpr.getLine()+ ": operandi nisu kompatibilni!", null);
		}
	}
	
	//RelOp
	public void visit(EqlOp eql) {
		currRelopValue = RelOpValue.EQL;
	}
	
	public void visit(NotEqlOp neql) {
		currRelopValue = RelOpValue.NEQ;
	}
	
	public void visit(GrtOp grt) {
		currRelopValue = RelOpValue.NEQ;
	}
	
	public void visit(GreOp gre) {
		currRelopValue = RelOpValue.GRE;
	}
	
	public void visit(LssOp lss) {
		currRelopValue = RelOpValue.LSS;
	}
	
	public void visit(LseOp lse) {
		currRelopValue = RelOpValue.LSE;
	}
	
	//CondFact
	//(RelOpCondFact) Expr:exprLeft RelOp Expr:exprRight
	public void visit(RelOpCondFact relopFact) {
		
		if(!relopFact.getExpr().struct.compatibleWith(relopFact.getExpr1().struct)) {
			report_error("Greska na liniji " + relopFact.getLine()+ ": operandi koji su u relaciji nisu kompatibilni!", null);
		}
		
		relopFact.struct = MyTab.boolType;
		
	}
	
	//(SimpleCondFact) Expr:expr
	public void visit(CondFactExpr condFactExpr) {
		condFactExpr.struct = condFactExpr.getExpr().struct;
		
	}
	
	//CondTerm
	//(AndCondTerm) CondTerm LAND CondFact
	public void visit(AndCondTerm andCondTerm) {
		Obj typeNode = MyTab.find("bool");
		andCondTerm.struct= typeNode.getType();
	}
	
	//(CondTermFact) CondFact
	public void visit(CondTermFact condTermFact) {
		condTermFact.struct = condTermFact.getCondFact().struct;
	}
	
	//Condition
	//(OrCondition) Condition LOR CondTerm
	public void visit(OrCondition orCondition) {
		Obj typeNode = MyTab.find("bool");
		orCondition.struct = typeNode.getType();
	}
	
	//(ConditionCondTerm) CondTerm
	public void visit(ConditionCondTerm condCondTerm) {
		condCondTerm.struct = condCondTerm.getCondTerm().struct;
	}
	
	//FormPars
	
	public void visit(NoFormParam  noFormParam) {
		formalParamCounter = 0;
	}
	
	
	
	//(FormParamVar) Type:paramType IDENT
	public void visit(FormParamVar formParam) {
		
		formParam.struct=formParam.getType().struct;
		
		Obj varNode = MyTab.find(formParam.getParamName());
		
		if(varNode == MyTab.noObj){
			report_info("Deklarisan parametar "+ formParam.getParamName()+" metode: "+ currentMethod.getName()+".", formParam);
		}else {
			report_error("Greska na liniji " + formParam.getLine()+ ": vec je deklarisano ime '"+ formParam.getParamName()+"' u tabeli simbola!", null);
		}
		
		varNode = MyTab.insert(Obj.Var, formParam.getParamName(), formParam.getType().struct);
		formalParamCounter++;
		
	}
	
	
	//(FormParamArray) Type:paramType IDENT LBRACKET RBRACKET
	public void visit(FormParamArray formParamArr) {
		
		
		formParamArr.struct=formParamArr.getType().struct;
		
		Obj varNode = MyTab.find(formParamArr.getParamName());
		
		if(varNode == MyTab.noObj){
			report_info("Deklarisana promenljiva "+ formParamArr.getParamName(), formParamArr);
		}else {
			report_error("Greska na liniji " + formParamArr.getLine()+ ": vec je deklarisano ime '"+ formParamArr.getParamName()+"' u tabeli simbola!", null);
		}
		
		
		Struct s = new Struct(Struct.Array, currTypeNode.getType());
		//Struct s = new Struct(Struct.Array, formParamArr.struct);
		
		Tab.insert(Obj.Var, formParamArr.getParamName(), s);
		formalParamCounter++;
	}
	
	//(FormalParamDecl) FormalParamDecl
	public void visit(FormParamDecl formParamDecl) {
		formParamDecl.struct = formParamDecl.getFormalParamDecl().struct;
		
	}
	
	
	//ActualPars
	
	//U Factor smeni i u DesigAction
	//Factor ::= (FactorFuncCall) Designator:d LPAREN ActualPars RPAREN
	//(DesigFuncCall) LPAREN ActualPars RPAREN
	
	//(ActualParam) Expr:expr
	public void visit(ActualParam actualParam) {
		
		actualParam.struct= actualParam.getExpr().struct;
		Obj currFormParam;
		currFormIterator = formParamIterStack.pop();
		if(currFormIterator.hasNext()) {
			currFormParam = currFormIterator.next();
			if(!actualParam.struct.assignableTo(currFormParam.getType())) {
				report_error("Greska na liniji " + actualParam.getLine()+ ": stvarni parametar ne odgovara formalnom parametru metode po tipu!", null);
			}
			
		}else {
			report_error("Greska na liniji " + actualParam.getLine()+ ": broj stvarnih parametara je veci od broja formalnih parametara!", null);
		}
	}
	
	//(ActualParamsList) ActualParamList COMMA Expr:expr
	public void visit(ActualParamsList actualParamList) {
		//msm da ne mora nista
		Obj currFormParam;
		if(currFormIterator.hasNext()) {
			currFormParam = currFormIterator.next();
			if(!actualParamList.getExpr().struct.assignableTo(currFormParam.getType())) {
				report_error("Greska na liniji " + actualParamList.getLine()+ ": stvarni parametar ne odgovara formalnom parametru metode po tipu!", null);
			}
			
		}else {
			report_error("Greska na liniji " + actualParamList.getLine()+ ": broj stvarnih parametara je veci od broja formalnih parametara!", null);
		}
	}
	
	//(ActPars) ActualParamList
	public void visit(ActPars actPars) {
		
		if(currFormIterator.hasNext()) {
			report_error("Greska na liniji " + actPars.getLine()+ ": broj stvarnih parametara je manji od broja formalnih parametara!", null);
		}
	}
	
	//(NoActPars) /* epsilon */
	public void visit(NoActPars noActPars) {
		
		if(currFormIterator.hasNext()) {
			report_error("Greska na liniji " + noActPars.getParent().getLine()+ ": broj stvarnih parametara je manji od broja formalnih parametara!", null);
		}
	}
	
	
	public boolean passed(){
    	return !errorDetected;
    }
	
	public void printInfo(Obj node, SyntaxNode s) {
		myDumpSymbTable.visitObjNode(node);
		report_info("Pretraga na "+s.getLine()+"("+node.getName()+")"+", nadjeno "+ myDumpSymbTable.getOutput(), s);
		myDumpSymbTable.resetOutput();
	}
	

}
